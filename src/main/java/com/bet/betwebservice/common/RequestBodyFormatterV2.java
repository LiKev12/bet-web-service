package com.bet.betwebservice.common;

import com.fasterxml.jackson.databind.JsonNode;

public class RequestBodyFormatterV2 {
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

    public static String fStringLowercase(JsonNode jsonNode) {
        if (jsonNode.isNull()) {
            return null;
        }
        String trimmedStringInput = jsonNode.asText().trim();
        return trimmedStringInput.toLowerCase();
    }

    public static Integer fInt(JsonNode jsonNode) {
        if (jsonNode.isNull()) {
            return null;
        }
        return jsonNode.asInt();
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
