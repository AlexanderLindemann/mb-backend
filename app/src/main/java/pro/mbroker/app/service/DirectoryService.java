package pro.mbroker.app.service;

import pro.mbroker.api.dto.response.EnumDescription;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.EmploymentType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;
import pro.smartdeal.common.enums.EnumWithValue;

import java.util.Arrays;
import java.util.List;

public interface DirectoryService {

    List<Class<? extends EnumWithValue<String>>> DIRECTORY_ENUMS = Arrays.asList(
            CreditPurposeType.class,
            EmploymentType.class,
            RealEstateType.class,
            RegionType.class
    );
    List<Class<? extends EnumWithValue<String>>> REGION_ENUMS = Arrays.asList(
            RegionType.class
    );


    List<EnumDescription> getAllDirectory();

    List<EnumDescription> getFilteredRegion(List<RegionType> include, List<RegionType> exclude);
}
