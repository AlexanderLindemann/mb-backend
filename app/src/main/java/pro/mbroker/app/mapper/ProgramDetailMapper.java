package pro.mbroker.app.mapper;

import org.mapstruct.*;
import pro.mbroker.app.model.program.ProgramDetail;

@Mapper(config = ProgramMapperConfig.class)
public interface ProgramDetailMapper {
    @Mapping(target = "id", ignore = true)
    void updateProgramDetail(ProgramDetail programDetailRequest, @MappingTarget ProgramDetail programDetail);

}