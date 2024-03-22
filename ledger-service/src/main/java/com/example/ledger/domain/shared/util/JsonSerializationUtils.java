package com.example.ledger.domain.shared.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

import java.util.TreeMap;

public class JsonSerializationUtils {
    private static final ObjectMapper objectMapper =
            new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                    .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                    .enable(SerializationFeature.INDENT_OUTPUT)
                    .registerModule(new JavaTimeModule());

    @SneakyThrows
    public static String serialize(final Object obj) {
        if (obj == null) {
            return null;
        }
        return objectMapper.writeValueAsString(obj);
    }

    @SneakyThrows
    public static <T> T deserialize(String json, Class<T> valueType) {
        if (json == null) {
            return null;
        }
        return objectMapper.readValue(json, valueType);
    }

    @SneakyThrows
    public static <T> T deserialize(String json, TypeReference<T> valueTypeRef) {
        if (json == null) {
            return null;
        }
        return objectMapper.readValue(json, valueTypeRef);
    }

    /**
     * sometimes use this function we can easily compare two json body.
     */
    @SneakyThrows
    public static String sort(String json) {
        final ObjectMapper sortingMapper =
                JsonMapper.builder()
                        .nodeFactory(new SortingNodeFactory())
                        .build()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                        .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

        return sortingMapper.readTree(json).toString();
    }

    static class SortingNodeFactory extends JsonNodeFactory {
        @Override
        public ObjectNode objectNode() {
            return new ObjectNode(this, new TreeMap<>());
        }
    }
}