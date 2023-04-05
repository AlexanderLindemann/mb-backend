package pro.mbroker.app.mapper;

import org.mapstruct.*;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.response.CreditProgramResponse;
import pro.mbroker.app.model.program.CreditProgram;

@Mapper(config = ProgramMapperConfig.class)
public interface ProgramMapper {
    @Mapping(target = "creditProgramDetail", ignore = true)
    CreditProgramResponse toProgramResponseMapper(CreditProgram creditProgram);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creditProgramDetail", ignore = true)
    @Mapping(target = "creditParameter", ignore = true)
    @Mapping(target = "bank", ignore = true)
    CreditProgram toProgramMapper(BankProgramRequest bankProgramRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creditParameter", ignore = true)
    @Mapping(target = "creditProgramDetail", ignore = true)
    @Mapping(target = "bank", ignore = true)
    void updateProgramFromRequest(BankProgramRequest updateProgramRequest, @MappingTarget CreditProgram creditProgram);

}