package pro.mbroker.app.mapper.underwriting;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pro.mbroker.api.dto.request.notification.Decision;
import pro.mbroker.app.entity.underwriting.UnderwritingDecision;

@Mapper
public interface UnderwritingDecisionMapper {

    Decision toDecision(UnderwritingDecision entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    UnderwritingDecision toBanksDecision(Decision model);
}
