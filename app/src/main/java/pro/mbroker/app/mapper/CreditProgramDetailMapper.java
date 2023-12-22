package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.request.CreditProgramDetailResponse;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.entity.CreditProgramDetail;
import pro.mbroker.app.util.Converter;

import java.util.List;

@Mapper(config = ProgramMapperConfig.class)
public interface CreditProgramDetailMapper {
    @Mapping(target = "id", ignore = true)
    void updateProgramDetail(CreditProgramDetail creditProgramDetailRequest, @MappingTarget CreditProgramDetail creditProgramDetail);

    @Mapping(source = "include", target = "include")
    @Mapping(source = "exclude", target = "exclude")
    @Mapping(source = "creditPurposeType", target = "creditPurposeType")
    @Mapping(source = "realEstateType", target = "realEstateType")
    @Mapping(source = "creditProgramType", target = "creditProgramType")
    CreditProgramDetailResponse toProgramDetailResponse(BankProgramRequest request);

    @Named("stringToRealEstateTypeList")
    default List<RealEstateType> stringToRealEstateTypeList(String str) {
        return Converter.convertStringListToEnumList(str, RealEstateType.class);
    }

    @Named("stringToCreditPurposeTypeList")
    default List<CreditPurposeType> stringToCreditPurposeTypeList(String str) {
        return Converter.convertStringListToEnumList(str, CreditPurposeType.class);
    }

    @Named("realEstateTypeListToString")
    default String realEstateTypeListToString(List<RealEstateType> realEstateTypes) {
        return Converter.convertEnumListToString(realEstateTypes);
    }

    @Named("creditPurposeTypeListToString")
    default String creditPurposeTypeListToString(List<CreditPurposeType> creditPurposeTypes) {
        return Converter.convertEnumListToString(creditPurposeTypes);
    }
}