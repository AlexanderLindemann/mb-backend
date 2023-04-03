package pro.mbroker.app.mapper;

import org.mapstruct.*;
import pro.mbroker.api.dto.request.BankProgramSettingRequest;
import pro.mbroker.api.dto.request.ProgramRequest;
import pro.mbroker.app.model.program.Program;

@Mapper
public interface ProgramMapper {
    @Mapping(target = "programDetail", ignore = true)
    ProgramRequest toProgramRequestMapper(Program program);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "programDetail", ignore = true)
    @Mapping(target = "creditParameter", ignore = true)
    Program toProgramMapper(BankProgramSettingRequest bankProgramSettingRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creditParameter", ignore = true)
    @Mapping(target = "programDetail", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProgramFromRequest(BankProgramSettingRequest updateProgramRequest, @MappingTarget Program program);

}