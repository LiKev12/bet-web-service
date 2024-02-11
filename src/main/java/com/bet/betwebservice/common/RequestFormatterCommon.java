package com.bet.betwebservice.common;

import com.fasterxml.jackson.databind.JsonNode;
public class RequestFormatterCommon {
    public static String fString(JsonNode jsonNode) {
        if (jsonNode.isNull()) {
            return null;
        }
        String trimmedStringInput = jsonNode.asText().trim();
        if (trimmedStringInput.length() == 0) {
            return null;
        }
        return trimmedStringInput;
    }

    public static Integer fInteger(JsonNode jsonNode) {
        if (jsonNode.isNull()) {
            return null;
        }
        return jsonNode.asInt();
    }

    public static String fUUID(JsonNode jsonNode) {
        if (jsonNode.isNull()) {
            return null;
        }
        String trimmedUuidInput = jsonNode.asText().trim();
        if (trimmedUuidInput.length() == 0) {
            return null;
        }
        return trimmedUuidInput;
    }

    public static boolean fBoolean(JsonNode jsonNode) {
        return Boolean.valueOf(jsonNode.asBoolean());
    }

    public static Integer fTimestamp(JsonNode jsonNode) {
        if (jsonNode.isNull()) {
            return null;
        }
        if (jsonNode.asInt() <= 0) {
            return null;
        }
        return jsonNode.asInt();
    }
}
