package pro.mbroker.app.util;

import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
}
