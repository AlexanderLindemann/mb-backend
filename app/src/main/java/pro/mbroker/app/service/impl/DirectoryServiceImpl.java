package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.mbroker.api.dto.response.EnumDescription;
import pro.mbroker.api.dto.response.EnumItemDescription;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.service.DirectoryService;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectoryServiceImpl implements DirectoryService {

    @Override
    public List<EnumDescription> getAllDirectory() {
        return getEnumDescriptions(DIRECTORY_ENUMS);
    }

    @Override
    public List<EnumDescription> getFilteredRegion(List<RegionType> include, List<RegionType> exclude) {
        List<RegionType> filteredRegions = (Objects.isNull(include) || include.isEmpty()) ?
                new ArrayList<>(Arrays.asList(RegionType.values())) : new ArrayList<>(include);
        filteredRegions.removeAll(exclude);
        List<EnumDescription> allRegionsDescriptions = getEnumDescriptions(REGION_ENUMS);
        return allRegionsDescriptions.stream()
                .map(enumDescription -> {
                    List<EnumItemDescription> filteredValues = enumDescription.getValues().stream()
                            .filter(enumItemDescription -> filteredRegions.contains(RegionType.valueOf(enumItemDescription.getCode())))
                            .collect(Collectors.toList());
                    return new EnumDescription()
                            .setCode(enumDescription.getCode())
                            .setName(enumDescription.getName())
                            .setDescription(enumDescription.getDescription())
                            .setValues(filteredValues);
                })
                .collect(Collectors.toList());
    }

    private List<EnumDescription> getEnumDescriptions(List<Class<? extends EnumWithValue<String>>> enumClasses) {
        List<EnumDescription> result = new ArrayList<>();
        for (Class<? extends EnumWithValue<String>> enumClass : enumClasses) {
            DictionaryEnum dictionaryEnum = enumClass.getAnnotation(DictionaryEnum.class);
            if (dictionaryEnum != null) {
                EnumWithValue<String>[] enumConstants = enumClass.getEnumConstants();
                List<EnumItemDescription> values = new ArrayList<>();
                if (enumConstants != null) {
                    for (EnumWithValue<String> enumConstant : enumConstants) {
                        values.add(new EnumItemDescription()
                                .setCode(enumConstant.getValue())
                                .setName((enumConstant.getName()))
                                .setDescription(enumConstant.getDescription()));
                    }
                    if (result.stream().anyMatch(EnumDescription -> EnumDescription.getCode().equals(dictionaryEnum.code()))) {
                        throw new RuntimeException("Enumeration with code " + dictionaryEnum.code() + " + is already exist");
                    }
                    result.add(new EnumDescription()
                            .setCode(dictionaryEnum.code())
                            .setName(dictionaryEnum.name())
                            .setDescription(dictionaryEnum.description())
                            .setValues(values));
                }
            }
        }
        return result;
    }

}
