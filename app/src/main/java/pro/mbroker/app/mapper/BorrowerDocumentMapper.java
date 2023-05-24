package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pro.mbroker.api.dto.response.BorrowerDocumentResponse;
import pro.mbroker.app.entity.BorrowerDocument;

@Mapper
public interface BorrowerDocumentMapper {
    @Mapping(source = "borrowerDocument.id", target = "borrowerProfileId")
    @Mapping(source = "borrowerDocument.bank.id", target = "bankId")
    BorrowerDocumentResponse toBorrowerDocumentResponse(BorrowerDocument borrowerDocument);

}