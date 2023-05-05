package pro.mbroker.app.util;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
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
        String payload = getPayload(token);
        JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        try {
            JSONObject jsonPayload = (JSONObject) parser.parse(payload);
            JSONObject data = (JSONObject) jsonPayload.get("data");
            int sdCurrentOrganizationIdAsInt = Integer.parseInt((String) data.get("sd_current_organization_id"));
            log.info("Извлечен sd_current_organization_id: {}", sdCurrentOrganizationIdAsInt);
            return sdCurrentOrganizationIdAsInt;
        } catch (net.minidev.json.parser.ParseException e) {
            log.error("Ошибка при разборе JSON-строки", e);
            throw new RuntimeException(e);
        }
    }

    private static String getPayload(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            log.error("Некорректный формат токена");
            throw new IllegalArgumentException("Некорректный формат токена");
        }
        return new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
    }

    public static int extractSdId(String token) {
        String payload = getPayload(token);
        JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        try {
            JSONObject jsonPayload = (JSONObject) parser.parse(payload);
            JSONObject data = (JSONObject) jsonPayload.get("data");
            int sdIdAsInt = Integer.parseInt((String) data.get("sd_id"));
            log.info("Извлечен sd_id: {}", sdIdAsInt);
            return sdIdAsInt;
        } catch (net.minidev.json.parser.ParseException e) {
            log.error("Ошибка при разборе JSON-строки", e);
            throw new RuntimeException(e);
        }
    }

    public List<String> getPermissions(String token) {
        List<String> permissions = new ArrayList<>();
        String payload = TokenExtractor.getPayload(token);
        JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        try {
            JSONObject jsonPayload = (JSONObject) parser.parse(payload);
            JSONArray scopes = (JSONArray) jsonPayload.get("scope");
            if (scopes != null) {
                for (Object scope : scopes) {
                    permissions.add(scope.toString());
                }
            }
        } catch (net.minidev.json.parser.ParseException e) {
            log.error("Ошибка при разборе JSON-строки", e);
            throw new RuntimeException(e);
        }
        return permissions;
    }
}