package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pro.mbroker.api.dto.response.AttachmentResponse;
import pro.mbroker.api.dto.response.StorageResponse;
import pro.mbroker.app.entity.FileStorage;

@Mapper
public interface StorageMapper {

    @Mapping(target = "url", ignore = true)
    StorageResponse toStorageResponse(FileStorage storage);

    AttachmentResponse toAttachmentResponse(FileStorage storage);
}