package pro.mbroker.app.mapper;

import org.mapstruct.*;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.response.ProgramResponse;
import pro.mbroker.app.model.program.Program;

@Mapper(config = ProgramMapperConfig.class)
public interface ProgramMapper {
    @Mapping(target = "programDetail", ignore = true)
    ProgramResponse toProgramResponseMapper(Program program);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "programDetail", ignore = true)
    @Mapping(target = "creditParameter", ignore = true)
    Program toProgramMapper(BankProgramRequest bankProgramRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creditParameter", ignore = true)
    @Mapping(target = "programDetail", ignore = true)
    void updateProgramFromRequest(BankProgramRequest updateProgramRequest, @MappingTarget Program program);

}