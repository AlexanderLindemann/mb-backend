package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.api.dto.response.PartnerResponse;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.util.Converter;

import java.util.List;

@Mapper(uses = {Converter.class, RealEstateAddressMapper.class})
public interface PartnerMapper {
    @Mapping(target = "bankCreditProgram", ignore = true)
    @Mapping(target = "creditPurposeType", qualifiedByName = "stringToCreditPurposeTypeList")
    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "realEstateAddress", ignore = true)
    @Mapping(target = "realEstateType", qualifiedByName = "stringToRealEstateTypeList")
    PartnerResponse toPartnerResponseMapper(Partner partner);


    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creditPrograms", ignore = true)
    @Mapping(target = "realEstateType", qualifiedByName = "realEstateTypeListToString")
    @Mapping(target = "creditPurposeType", qualifiedByName = "creditPurposeTypeListToString")
    @Mapping(target = "realEstateAddress", ignore = true)
    Partner toPartnerMapper(PartnerRequest request);

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
        return Converter.convertEnumListToStringList(realEstateTypes);
    }

    @Named("creditPurposeTypeListToString")
    default String creditPurposeTypeListToString(List<CreditPurposeType> creditPurposeTypes) {
        return Converter.convertEnumListToStringList(creditPurposeTypes);
    }
}