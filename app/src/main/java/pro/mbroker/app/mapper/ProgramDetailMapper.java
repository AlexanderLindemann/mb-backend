package pro.mbroker.app.mapper;

import org.mapstruct.*;
import pro.mbroker.app.model.program.ProgramDetail;

@Mapper
public interface ProgramDetailMapper {
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProgramDetail(ProgramDetail programDetailRequest, @MappingTarget ProgramDetail programDetail);

}