package pro.mbroker.app.mapper.underwriting;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pro.mbroker.api.dto.request.notification.Error;
import pro.mbroker.app.entity.underwriting.UnderwritingError;

@Mapper
public interface UnderwritingErrorMapper {

    Error toError(UnderwritingError underwritingError);



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    UnderwritingError toUnderwritingError(Error error);
}
