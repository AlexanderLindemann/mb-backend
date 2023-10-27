package pro.mbroker.app.util;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
@NoArgsConstructor
public final class TokenExtractor {

    public static int extractSdCurrentOrganizationId(String token) {
        JSONObject data = getJsonPayloadFromTokenByValue(token, "data", JSONObject.class);
        int sdCurrentOrganizationIdAsInt = 0;
        if (data != null) {
            sdCurrentOrganizationIdAsInt = Integer.parseInt((String) data.get("sd_current_organization_id"));
            log.info("Извлечен sd_current_organization_id: {}", sdCurrentOrganizationIdAsInt);
        }
        return sdCurrentOrganizationIdAsInt;
    }

    public static int extractSdId(String token) {
        JSONObject data = getJsonPayloadFromTokenByValue(token, "data", JSONObject.class);
        int sdIdAsInt = 0;
        if (data != null) {
            sdIdAsInt = Integer.parseInt((String) data.get("sd_id"));
            log.info("Извлечен sd_id: {}", sdIdAsInt);
        }
        return sdIdAsInt;
    }

    public static String extractPhoneNumber(String token) {
        String phoneNumber = getJsonPayloadFromTokenByValue(token, "phone", String.class);
        log.info("Извлечен phone_number: {}", phoneNumber);
        return phoneNumber;
    }

    public List<String> getPermissions(String token) {
        List<String> permissions = new ArrayList<>();
        log.info("Начат процесс получения permissions из токена");
        JSONArray scopes = getJsonPayloadFromTokenByValue(token, "scope", JSONArray.class);
        if (scopes != null) {
            for (Object scope : scopes) {
                permissions.add(scope.toString());
            }
        }
        log.info("Permissions получены");
        return permissions;
    }

    private static String getPayload(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            log.error("Некорректный формат токена");
            throw new IllegalArgumentException("Некорректный формат токена");
        }
        return new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
    }

    private static <T> T getJsonPayloadFromTokenByValue(String token, String value, Class<T> clazz) {
        String payload = getPayload(token);
        JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        try {
            JSONObject jsonPayload = (JSONObject) parser.parse(payload);
            Object extractedValue = jsonPayload.get(value);
            return clazz.cast(extractedValue);
        } catch (ParseException e) {
            log.error("Ошибка при разборе JSON-строки", e);
            throw new RuntimeException(e);
        }
    }
}
