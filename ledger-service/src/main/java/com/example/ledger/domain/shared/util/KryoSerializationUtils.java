package com.example.ledger.domain.shared.util;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@UtilityClass
public class KryoSerializationUtils {
    private static final ThreadLocal<Kryo> kryoLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @SneakyThrows
    public static byte[] serialize(Object object) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             Output output = new Output(stream)) {
            kryoLocal.get().writeClassAndObject(output, object);
            return output.toBytes();
        }
    }

    @SneakyThrows
    public static Object deserialize(byte[] bytes) {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
             Input input = new Input(stream);) {
            return kryoLocal.get().readClassAndObject(input);
        }
    }
}
