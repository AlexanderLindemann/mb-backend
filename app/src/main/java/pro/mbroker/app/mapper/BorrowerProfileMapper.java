package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pro.mbroker.api.dto.request.BorrowerProfileRequest;
import pro.mbroker.api.dto.response.BorrowerDocumentResponse;
import pro.mbroker.api.dto.response.BorrowerEmployerResponse;
import pro.mbroker.api.dto.response.BorrowerProfileForUpdateResponse;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.entity.BorrowerEmployer;
import pro.mbroker.app.entity.BorrowerProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(uses = {BorrowerEmployerMapper.class, BankMapper.class, BorrowerDocumentMapper.class})
public interface BorrowerProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "partnerApplication", ignore = true)
    @Mapping(source = "status", target = "borrowerProfileStatus", defaultValue = "DATA_NO_ENTERED")
    @Mapping(target = "borrowerDocument", ignore = true)
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "middleName", target = "middleName")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(target = "employer", ignore = true)
    @Mapping(target = "realEstate", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "prevFullName", ignore = true)
    @Mapping(target = "birthdate", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "maritalStatus", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "marriageContract", ignore = true)
    @Mapping(target = "passportNumber", ignore = true)
    @Mapping(target = "passportIssuedDate", ignore = true)
    @Mapping(target = "passportIssuedByCode", ignore = true)
    @Mapping(target = "passportIssuedByName", ignore = true)
    @Mapping(target = "registrationAddress", ignore = true)
    @Mapping(target = "residenceAddress", ignore = true)
    @Mapping(target = "registrationType", ignore = true)
    @Mapping(target = "snils", ignore = true)
    @Mapping(target = "residenceRF", ignore = true)
    @Mapping(target = "employmentStatus", ignore = true)
    @Mapping(target = "totalWorkExperience", ignore = true)
    @Mapping(target = "mainIncome", ignore = true)
    @Mapping(target = "additionalIncome", ignore = true)
    @Mapping(target = "pension", ignore = true)
    @Mapping(target = "proofOfIncome", ignore = true)
    @Mapping(target = "residencyOutsideRU", ignore = true)
    @Mapping(target = "longTermStayOutsideRU", ignore = true)
    @Mapping(target = "isPublicOfficial", ignore = true)
    @Mapping(target = "tin", ignore = true)
    @Mapping(target = "familyRelation", ignore = true)
    @Mapping(target = "relatedPublicOfficial", ignore = true)
    @Mapping(target = "publicOfficialPosition", ignore = true)
    @Mapping(target = "tinForeign", ignore = true)
    @Mapping(target = "birthPlace", ignore = true)
    @Mapping(target = "citizenship", ignore = true)
    @Mapping(target = "taxResidencyCountries", ignore = true)
    @Mapping(target = "link", ignore = true)
    @Mapping(target = "educations", ignore = true)
    BorrowerProfile toBorrowerProfile(BorrowerProfileRequest request);

    @Mapping(source = "request.firstName", target = "firstName")
    @Mapping(source = "request.lastName", target = "lastName")
    @Mapping(source = "request.middleName", target = "middleName")
    @Mapping(source = "request.phoneNumber", target = "phoneNumber")
    @Mapping(source = "request.link.shortLink", target = "link")
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
    @Mapping(source = "request.status", target = "borrowerProfileStatus", defaultValue = "DATA_NO_ENTERED")
    @Mapping(target = "borrowerDocument", ignore = true)
    @Mapping(source = "request.firstName", target = "firstName")
    @Mapping(source = "request.lastName", target = "lastName")
    @Mapping(source = "request.middleName", target = "middleName")
    @Mapping(source = "request.phoneNumber", target = "phoneNumber")
    @Mapping(target = "employer", ignore = true)
    @Mapping(target = "realEstate", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "prevFullName", ignore = true)
    @Mapping(target = "birthdate", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "maritalStatus", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "marriageContract", ignore = true)
    @Mapping(target = "educations", ignore = true)
    @Mapping(target = "passportNumber", ignore = true)
    @Mapping(target = "passportIssuedDate", ignore = true)
    @Mapping(target = "passportIssuedByCode", ignore = true)
    @Mapping(target = "passportIssuedByName", ignore = true)
    @Mapping(target = "registrationAddress", ignore = true)
    @Mapping(target = "residenceAddress", ignore = true)
    @Mapping(target = "registrationType", ignore = true)
    @Mapping(target = "snils", ignore = true)
    @Mapping(target = "residenceRF", ignore = true)
    @Mapping(target = "employmentStatus", ignore = true)
    @Mapping(target = "totalWorkExperience", ignore = true)
    @Mapping(target = "mainIncome", ignore = true)
    @Mapping(target = "additionalIncome", ignore = true)
    @Mapping(target = "pension", ignore = true)
    @Mapping(target = "proofOfIncome", ignore = true)
    @Mapping(target = "residencyOutsideRU", ignore = true)
    @Mapping(target = "longTermStayOutsideRU", ignore = true)
    @Mapping(target = "isPublicOfficial", ignore = true)
    @Mapping(target = "tin", ignore = true)
    @Mapping(target = "familyRelation", ignore = true)
    @Mapping(target = "relatedPublicOfficial", ignore = true)
    @Mapping(target = "publicOfficialPosition", ignore = true)
    @Mapping(target = "tinForeign", ignore = true)
    @Mapping(target = "birthPlace", ignore = true)
    @Mapping(target = "citizenship", ignore = true)
    @Mapping(target = "taxResidencyCountries", ignore = true)
    @Mapping(target = "link", ignore = true)
    void updateBorrowerProfile(BorrowerProfileRequest request, @MappingTarget BorrowerProfile profile);

    @Mapping(source = "borrowerProfileStatus", target = "status", defaultValue = "DATA_NO_ENTERED")
    @Mapping(target = "documents", expression = "java(mapBorrowerDocuments(borrowerProfile.getBorrowerDocument()))")
    @Mapping(target = "educations", ignore = true)
    BorrowerProfileForUpdateResponse toBorrowerProfileForUpdate(BorrowerProfile borrowerProfile);


    default BorrowerDocumentResponse toBorrowerDocumentResponse(BorrowerDocument borrowerDocument) {
        BorrowerDocumentResponse response = new BorrowerDocumentResponse();
        response.setAttachmentId(borrowerDocument.getAttachment().getId());
        if (Objects.nonNull(borrowerDocument.getBank())) {
            response.setBankId(borrowerDocument.getBank().getId());
        }
        response.setBorrowerProfileId(borrowerDocument.getBorrowerProfile().getId());
        response.setAttachmentName(borrowerDocument.getAttachment().getName());
        response.setDocumentType(borrowerDocument.getDocumentType());
        response.setSizeBytes(borrowerDocument.getAttachment().getSizeBytes());
        response.setUpdatedAt(String.valueOf(borrowerDocument.getUpdatedAt()));
        return response;
    }

    default List<BorrowerDocumentResponse> mapBorrowerDocuments(List<BorrowerDocument> borrowerDocuments) {
        if (borrowerDocuments == null) {
            return new ArrayList<>();
        }
        return borrowerDocuments.stream()
                .filter(BorrowerDocument::isActive)
                .map(this::toBorrowerDocumentResponse)
                .collect(Collectors.toList());
    }

    BorrowerEmployerResponse toEmployerDto(BorrowerEmployer borrowerEmployer);


    default List<UUID> mapSalaryBanksToUuidList(Set<Bank> bankResponses) {
        return bankResponses.stream()
                .map(Bank::getId)
                .collect(Collectors.toList());
    }
}