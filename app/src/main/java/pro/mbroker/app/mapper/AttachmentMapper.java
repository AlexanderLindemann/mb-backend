package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pro.mbroker.api.dto.response.AttachmentResponse;
import pro.mbroker.app.entity.Attachment;


@Mapper
public interface AttachmentMapper {

    @Mapping(target = "sizeBytes", ignore = true)
    @Mapping(target = "mimeType", ignore = true)
    @Mapping(target = "contentMd5", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    AttachmentResponse toAttachmentResponse(Attachment attachment);

}
