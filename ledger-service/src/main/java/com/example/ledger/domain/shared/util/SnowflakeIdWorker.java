package com.example.ledger.domain.shared.util;

/**
 * Distributed Sequence Generator.
 * Twitter snowflake: https://github.com/twitter/snowflake/tree/snowflake-2010
 * This class should be used as a Singleton.
 * Make sure that you create and reuse a Single instance of Snowflake per node in your distributed system cluster.
 */
public class SnowflakeIdWorker {
    /**
     * start time stamp (2020-01-01)
     */
    private final long twepoch = 1577808000000L;

    private final long workerIdBits = 5L;

    private final long datacenterIdBits = 5L;

    /**
     * 31
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 31
     */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    private final long sequenceBits = 12L;

    /**
     * shit 12 bits
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 17 bits (12+5)
     */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /**
     * 22 bits (5+5+12)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /**
     * 4095 (0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * (0~31)
     */
    private long workerId;

    /**
     * (0~31)
     */
    private long datacenterId;

    /**
     * Sequence within milliseconds (0~4095)
     */
    private long sequence = 0L;

    private long lastTimestamp = -1L;

    /**
     * @param workerId     (0~31)
     * @param datacenterId (0~31)
     */
    public SnowflakeIdWorker(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        //  the system clock has rolled back past this time
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // If they are generated at the same time, perform the sequence within milliseconds.
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // Sequence overflow in milliseconds
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //Timestamp changes, sequence resets within milliseconds
        else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        //form a 64-bit ID
        return ((timestamp - twepoch) << timestampLeftShift) //
                | (datacenterId << datacenterIdShift) //
                | (workerId << workerIdShift) //
                | sequence;
    }


    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
        for (int i = 0; i < 1000; i++) {
            long id = idWorker.nextId();
            System.out.println(Long.toBinaryString(id));
            System.out.println(id);
        }
    }
}
