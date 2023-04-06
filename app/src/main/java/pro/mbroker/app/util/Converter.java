package pro.mbroker.app.util;

import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.request.CreditProgramDetailResponse;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.CreditProgramDetail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class Converter {
    private Converter() {
    }

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

    public static CreditProgramDetailResponse convertCreditDetailToEnumFormat(CreditProgramDetail creditProgramDetail) {
        return new CreditProgramDetailResponse()
                .setCreditPurposeType(Converter.convertStringListToEnumList(creditProgramDetail.getCreditPurposeType(), CreditPurposeType.class))
                .setRealEstateType(Converter.convertStringListToEnumList(creditProgramDetail.getRealEstateType(), RealEstateType.class))
                .setInclude(Converter.convertStringListToEnumList(creditProgramDetail.getInclude(), RegionType.class))
                .setExclude(Converter.convertStringListToEnumList(creditProgramDetail.getExclude(), RegionType.class));
    }

    public static CreditProgramDetail convertCreditDetailToStringFormat(BankProgramRequest createCreditParameter) {
        return new CreditProgramDetail()
                .setCreditPurposeType(Converter.convertEnumListToStringList(createCreditParameter.getCreditPurposeType()))
                .setInclude(Converter.convertEnumListToStringList(createCreditParameter.getInclude()))
                .setExclude(Converter.convertEnumListToStringList(createCreditParameter.getExclude()))
                .setRealEstateType(Converter.convertEnumListToStringList(createCreditParameter.getRealEstateType()));
    }
}
