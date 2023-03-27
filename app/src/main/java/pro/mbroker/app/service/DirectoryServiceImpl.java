package pro.mbroker.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.mbroker.api.dto.response.EnumDescription;
import pro.mbroker.api.dto.response.EnumItemDescription;
import pro.smartdeal.common.enums.DictionaryEnum;
import pro.smartdeal.common.enums.EnumWithValue;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DirectoryServiceImpl implements DirectoryService {

    @Override
    public List<EnumDescription> getDirectory() {
        List<EnumDescription> result = new ArrayList<>();
        for (Class<? extends EnumWithValue<String>> enumClass : ENUMS) {
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
