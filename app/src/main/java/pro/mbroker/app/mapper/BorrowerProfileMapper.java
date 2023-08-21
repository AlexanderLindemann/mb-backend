package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pro.mbroker.api.dto.request.BorrowerDocumentRequest;
import pro.mbroker.api.dto.request.BorrowerProfileRequest;
import pro.mbroker.api.dto.response.BorrowerProfileFullResponse;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.entity.BorrowerProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(uses = {BorrowerEmployerMapper.class, BankMapper.class})
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
    @Mapping(target = "employer", ignore = true)
    @Mapping(target = "realEstate", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "prevFullName", constant = "null")
    @Mapping(target = "birthdate", constant = "null")
    @Mapping(target = "age", expression = "java(null)")
    @Mapping(target = "gender", expression = "java(null)")
    @Mapping(target = "maritalStatus", expression = "java(null)")
    @Mapping(target = "children", expression = "java(null)")
    @Mapping(target = "marriageContract", expression = "java(null)")
    @Mapping(target = "education", expression = "java(null)")
    @Mapping(target = "passportNumber", constant = "null")
    @Mapping(target = "passportIssuedDate", constant = "null")
    @Mapping(target = "passportIssuedByCode", constant = "null")
    @Mapping(target = "passportIssuedByName", constant = "null")
    @Mapping(target = "registrationAddress", constant = "null")
    @Mapping(target = "residenceAddress", constant = "null")
    @Mapping(target = "registrationType", expression = "java(null)")
    @Mapping(target = "snils", constant = "null")
    @Mapping(target = "residenceRF", expression = "java(null)")
    @Mapping(target = "employmentStatus", expression = "java(null)")
    @Mapping(target = "totalWorkExperience", expression = "java(null)")
    @Mapping(target = "mainIncome", expression = "java(null)")
    @Mapping(target = "additionalIncome", expression = "java(null)")
    @Mapping(target = "pension", expression = "java(null)")
    @Mapping(target = "proofOfIncome", expression = "java(null)")
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
    @Mapping(target = "partnerApplication", ignore = true)
    @Mapping(target = "borrowerProfileStatus", ignore = true)
    @Mapping(target = "borrowerDocument", ignore = true)
    @Mapping(source = "request.firstName", target = "firstName")
    @Mapping(source = "request.lastName", target = "lastName")
    @Mapping(source = "request.middleName", target = "middleName")
    @Mapping(source = "request.phoneNumber", target = "phoneNumber")
    @Mapping(target = "employer", ignore = true)
    @Mapping(target = "realEstate", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "prevFullName", constant = "null")
    @Mapping(target = "birthdate", ignore = true)
    @Mapping(target = "age", expression = "java(null)")
    @Mapping(target = "gender", expression = "java(null)")
    @Mapping(target = "maritalStatus", expression = "java(null)")
    @Mapping(target = "children", expression = "java(null)")
    @Mapping(target = "marriageContract", expression = "java(null)")
    @Mapping(target = "education", expression = "java(null)")
    @Mapping(target = "passportNumber", constant = "null")
    @Mapping(target = "passportIssuedDate", ignore = true)
    @Mapping(target = "passportIssuedByCode", constant = "null")
    @Mapping(target = "passportIssuedByName", constant = "null")
    @Mapping(target = "registrationAddress", constant = "null")
    @Mapping(target = "residenceAddress", constant = "null")
    @Mapping(target = "registrationType", expression = "java(null)")
    @Mapping(target = "snils", constant = "null")
    @Mapping(target = "residenceRF", expression = "java(null)")
    @Mapping(target = "employmentStatus", expression = "java(null)")
    @Mapping(target = "totalWorkExperience", expression = "java(null)")
    @Mapping(target = "mainIncome", expression = "java(null)")
    @Mapping(target = "additionalIncome", expression = "java(null)")
    @Mapping(target = "pension", expression = "java(null)")
    @Mapping(target = "proofOfIncome", expression = "java(null)")
    void updateBorrowerProfile(BorrowerProfileRequest request, @MappingTarget BorrowerProfile profile);

    @Mapping(source = "borrowerProfileStatus", target = "status")
    @Mapping(target = "documents", expression = "java(mapBorrowerDocuments(borrowerProfile.getBorrowerDocument()))")
    BorrowerProfileFullResponse toBorrowerProfileFullResponse(BorrowerProfile borrowerProfile);

    default BorrowerDocumentRequest toBorrowerDocumentRequest(BorrowerDocument borrowerDocument) {
        BorrowerDocumentRequest request = new BorrowerDocumentRequest();
        request.setAttachmentId(borrowerDocument.getAttachment().getId());
        request.setAttachmentName(borrowerDocument.getAttachment().getName());
        if (Objects.nonNull(borrowerDocument.getBank())) {
            request.setBankId(borrowerDocument.getBank().getId());
        }
        request.setBorrowerProfileId(borrowerDocument.getBorrowerProfile().getId());
        request.setDocumentType(borrowerDocument.getDocumentType());
        return request;
    }

    default List<BorrowerDocumentRequest> mapBorrowerDocuments(List<BorrowerDocument> borrowerDocuments) {
        if (borrowerDocuments == null) {
            return new ArrayList<>();
        }
        return borrowerDocuments.stream()
                .filter(BorrowerDocument::isActive)
                .map(this::toBorrowerDocumentRequest)
                .collect(Collectors.toList());
    }

}