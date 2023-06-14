package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pro.mbroker.api.dto.request.BorrowerDocumentRequest;
import pro.mbroker.api.dto.request.BorrowerProfileRequest;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.entity.BorrowerProfile;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface BorrowerProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "partnerApplication", ignore = true)
    @Mapping(target = "borrowerProfileStatus", ignore = true)
    @Mapping(target = "borrowerDocument", ignore = true)
    @Mapping(source = "request.firstName", target = "firstName")
    @Mapping(source = "request.lastName", target = "lastName")
    @Mapping(source = "request.middleName", target = "middleName")
    @Mapping(source = "request.phoneNumber", target = "phoneNumber")
    BorrowerProfile toBorrowerProfile(BorrowerProfileRequest request);

    @Mapping(source = "borrowerProfileStatus", target = "status")
    BorrowerProfileRequest toBorrowerProfileDto(BorrowerProfile request);


    @Mapping(source = "request.firstName", target = "firstName")
    @Mapping(source = "request.lastName", target = "lastName")
    @Mapping(source = "request.middleName", target = "middleName")
    @Mapping(source = "request.phoneNumber", target = "phoneNumber")
    @Mapping(source = "borrowerProfileStatus", target = "status")
    @Mapping(target = "documents", expression = "java(mapBorrowerDocuments(request.getBorrowerDocument()))")
    BorrowerProfileResponse toBorrowerProfileResponse(BorrowerProfile request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "borrowerProfileStatus", ignore = true)
    @Mapping(target = "partnerApplication", ignore = true)
    @Mapping(target = "borrowerDocument", ignore = true)
    @Mapping(source = "request.firstName", target = "firstName")
    @Mapping(source = "request.lastName", target = "lastName")
    @Mapping(source = "request.middleName", target = "middleName")
    @Mapping(source = "request.phoneNumber", target = "phoneNumber")
    BorrowerProfile toBorrowerProfile(BorrowerProfileResponse request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "partnerApplication", ignore = true)
    @Mapping(target = "borrowerProfileStatus", ignore = true)
    @Mapping(target = "borrowerDocument", ignore = true)
    @Mapping(source = "request.firstName", target = "firstName")
    @Mapping(source = "request.lastName", target = "lastName")
    @Mapping(source = "request.middleName", target = "middleName")
    @Mapping(source = "request.phoneNumber", target = "phoneNumber")
    void updateBorrowerProfile(BorrowerProfileRequest request, @MappingTarget BorrowerProfile profile);


    default BorrowerDocumentRequest toBorrowerDocumentRequest(BorrowerDocument borrowerDocument) {
        BorrowerDocumentRequest request = new BorrowerDocumentRequest();
        request.setAttachmentId(borrowerDocument.getAttachment().getId());
        request.setAttachmentName(borrowerDocument.getAttachment().getName());
        request.setBankId(borrowerDocument.getBank().getId());
        request.setBorrowerProfileId(borrowerDocument.getBorrowerProfile().getId());
        request.setDocumentType(borrowerDocument.getDocumentType());
        return request;
    }

    default List<BorrowerDocumentRequest> mapBorrowerDocuments(List<BorrowerDocument> borrowerDocuments) {
        return borrowerDocuments.stream()
                .map(this::toBorrowerDocumentRequest)
                .collect(Collectors.toList());
    }

}