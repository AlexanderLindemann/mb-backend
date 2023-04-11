package pro.mbroker.app.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.request.CreditProgramDetailResponse;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.CreditProgramDetail;

@Component
@Slf4j
public class CreditProgramConverter {

    public CreditProgramDetailResponse convertCreditDetailToEnumFormat(CreditProgramDetail creditProgramDetail) {
        return new CreditProgramDetailResponse()
                .setCreditPurposeType(Converter.convertStringListToEnumList(creditProgramDetail.getCreditPurposeType(), CreditPurposeType.class))
                .setRealEstateType(Converter.convertStringListToEnumList(creditProgramDetail.getRealEstateType(), RealEstateType.class))
                .setInclude(Converter.convertStringListToEnumList(creditProgramDetail.getInclude(), RegionType.class))
                .setExclude(Converter.convertStringListToEnumList(creditProgramDetail.getExclude(), RegionType.class));
    }

    public CreditProgramDetail convertCreditDetailToStringFormat(BankProgramRequest createCreditParameter) {
        return new CreditProgramDetail()
                .setCreditPurposeType(Converter.convertEnumListToStringList(createCreditParameter.getCreditPurposeType()))
                .setInclude(Converter.convertEnumListToStringList(createCreditParameter.getInclude()))
                .setExclude(Converter.convertEnumListToStringList(createCreditParameter.getExclude()))
                .setRealEstateType(Converter.convertEnumListToStringList(createCreditParameter.getRealEstateType()));
    }

}
