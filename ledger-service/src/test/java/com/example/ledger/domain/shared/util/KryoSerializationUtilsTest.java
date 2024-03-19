package com.example.ledger.domain.shared.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class KryoSerializationUtilsTest {
    @Test
    void serialize() {
        Map<String, String> obj = new HashMap<>();
        obj.put("test", "yes");

        var bytes = KryoSerializationUtils.serialize(obj);

        Assertions.assertTrue(bytes.length > 0);
    }

    @Test
    void deserialize() {
        HashMap<String, String> obj = new HashMap<>();
        obj.put("test", "yes");

        var bytes = KryoSerializationUtils.serialize(obj);
        var result = KryoSerializationUtils.deserialize(bytes);

        Assertions.assertEquals(obj, result);
    }
}