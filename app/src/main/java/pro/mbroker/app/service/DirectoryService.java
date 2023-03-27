package pro.mbroker.app.service;

import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.EmploymentType;
import pro.mbroker.api.dto.response.EnumDescription;
import pro.mbroker.api.enums.RealEstateType;
import pro.smartdeal.common.enums.EnumWithValue;

import java.util.Arrays;
import java.util.List;

public interface DirectoryService {

    List<Class<? extends EnumWithValue<String>>> ENUMS = Arrays.asList(
            CreditPurposeType.class,
            EmploymentType.class,
            RealEstateType.class
    );

    List<EnumDescription> getDirectory();

}
