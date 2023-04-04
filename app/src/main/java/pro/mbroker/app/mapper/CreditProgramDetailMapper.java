package pro.mbroker.app.mapper;

import org.mapstruct.*;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.request.CreditProgramDetailResponse;
import pro.mbroker.app.model.program.CreditProgramDetail;

@Mapper(config = ProgramMapperConfig.class)
public interface CreditProgramDetailMapper {
    @Mapping(target = "id", ignore = true)
    void updateProgramDetail(CreditProgramDetail creditProgramDetailRequest, @MappingTarget CreditProgramDetail creditProgramDetail);

    @Mapping(source = "include", target = "include")
    @Mapping(source = "exclude", target = "exclude")
    @Mapping(source = "creditPurposeType", target = "creditPurposeType")
    @Mapping(source = "realEstateType", target = "realEstateType")
    CreditProgramDetailResponse toProgramDetailResponse(BankProgramRequest request);


}