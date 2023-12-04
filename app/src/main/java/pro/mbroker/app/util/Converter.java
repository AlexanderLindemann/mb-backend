package pro.mbroker.app.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public final class Converter {

    public static String convertEnumListToStringList(List<? extends Enum<?>> enums) {
        return enums.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    public static <T extends Enum<T>> List<T> convertStringListToEnumList(String str, Class<T> enumClass) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .map(strEnum -> Enum.valueOf(enumClass, strEnum))
                .collect(Collectors.toList());
    }

    public static String generateBase64FromFile(MultipartFile multipartFile) {
        if (multipartFile == null) {
            return null;
        }
        try {
            byte[] logoBytes = multipartFile.getBytes();
            return Base64.getEncoder().encodeToString(logoBytes);
        } catch (IOException e) {
            log.error("Ошибка при обработке файла: {}", e.getMessage());
        }
        return null;
    }
}
