package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.BorrowerProfileStatus;
import pro.mbroker.api.enums.DocumentType;
import pro.mbroker.api.enums.EmploymentStatus;
import pro.mbroker.api.enums.PartnerApplicationStatus;
import pro.mbroker.api.enums.ProofOfIncome;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BaseEntity;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.entity.BorrowerEmployer;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.service.StatusService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    private static final List<DocumentType> REQUIRED_DOCUMENT_TYPES =
            Arrays.asList(DocumentType.BORROWER_PASSPORT, DocumentType.BORROWER_SNILS);

    private static final Set<BankApplicationStatus> UNCHANGEABLE_STATUSES = Set.of(
            BankApplicationStatus.SENT_TO_BANK,
            BankApplicationStatus.SENDING_TO_BANK,
            BankApplicationStatus.APPLICATION_APPROVED,
            BankApplicationStatus.CREDIT_APPROVED,
            BankApplicationStatus.REFINEMENT,
            BankApplicationStatus.REJECTED,
            BankApplicationStatus.SENDING_ERROR,
            BankApplicationStatus.EXPIRED
    );

    @Override
    public boolean statusChanger(PartnerApplication application) {
        boolean borrowerStatusChanged = checkBorrowerStatus(application);
        boolean bankApplicationStatusChanged = checkBankApplicationStatus(application);
        boolean partnerApplicationStatusChanged = partnerApplicationStatusChanged(application);
        return partnerApplicationStatusChanged || borrowerStatusChanged || bankApplicationStatusChanged;
    }

    private boolean checkBorrowerStatus(PartnerApplication partnerApplication) {
        boolean isChange = false;
        List<BorrowerProfile> activeBorrowers = partnerApplication.getBorrowerProfiles().stream()
                .filter(BaseEntity::isActive)
                .collect(Collectors.toList());
        for (BorrowerProfile borrowerProfile : activeBorrowers) {
            if (borrowerProfile != null) {
                if (Objects.isNull(borrowerProfile.getBorrowerProfileStatus())) {
                    borrowerProfile.setBorrowerProfileStatus(BorrowerProfileStatus.DATA_NO_ENTERED);
                    isChange = true;
                }
                List<BorrowerDocument> borrowerDocuments = borrowerProfile.getBorrowerDocument();
                BorrowerProfileStatus currentStatus = borrowerProfile.getBorrowerProfileStatus();

                if (Objects.nonNull(borrowerDocuments)) {
                    boolean allDocumentsPresent = checkRequiredDocuments(borrowerProfile);
                    if (allDocumentsPresent
                            && (isBorrowerMainInfoComplete(borrowerProfile)
                            && isPassportInfoComplete(borrowerProfile)
                            && isEmployerInfoComplete(borrowerProfile)
                            && isIncomeInfoComplete(borrowerProfile))) {

                        if (borrowerProfile.getBorrowerDocument().stream()
                                .filter(BaseEntity::isActive)
                                .map(BorrowerDocument::getDocumentType)
                                .anyMatch(docType -> docType == DocumentType.GENERATED_SIGNATURE_FORM
                                        || docType == DocumentType.SIGNATURE_FORM)) {
                            if (!currentStatus.equals(BorrowerProfileStatus.DOCS_SIGNED)) {
                                borrowerProfile.setBorrowerProfileStatus(BorrowerProfileStatus.DOCS_SIGNED);
                                isChange = true;
                            }
                        } else {
                            if (!currentStatus.equals(BorrowerProfileStatus.DATA_ENTERED)) {
                                borrowerProfile.setBorrowerProfileStatus(BorrowerProfileStatus.DATA_ENTERED);
                                isChange = true;
                            }
                        }
                    } else {
                        if (!currentStatus.equals(BorrowerProfileStatus.DATA_NO_ENTERED)) {
                            borrowerProfile.setBorrowerProfileStatus(BorrowerProfileStatus.DATA_NO_ENTERED);
                            isChange = true;
                        }
                    }
                } else {
                    if (!currentStatus.equals(BorrowerProfileStatus.DATA_NO_ENTERED)) {
                        borrowerProfile.setBorrowerProfileStatus(BorrowerProfileStatus.DATA_NO_ENTERED);
                        isChange = true;
                    }
                }
            }
        }
        return isChange;
    }

    private boolean checkBankApplicationStatus(PartnerApplication partnerApplication) {
        boolean isChange = false;
        if (Objects.nonNull(partnerApplication) && Objects.nonNull(partnerApplication.getBankApplications())) {
            List<BankApplication> bankApplications = new ArrayList<>(partnerApplication.getBankApplications());
            bankApplications.removeIf(app -> !app.isActive() ||
                    (app.getBankApplicationStatus() != null &&
                            UNCHANGEABLE_STATUSES.contains(app.getBankApplicationStatus())));
            boolean borrowersInSignedStatus = allBorrowerInSignedStatus(partnerApplication.getBorrowerProfiles());
            for (BankApplication bankApplication : bankApplications) {
                if (Objects.nonNull(bankApplication)) {
                    BankApplicationStatus currentStatus = bankApplication.getBankApplicationStatus();
                    if (currentStatus == null) {
                        bankApplication.setBankApplicationStatus(BankApplicationStatus.DATA_NO_ENTERED);
                        isChange = true;
                    } else {
                        if (borrowersInSignedStatus) {
                            if (!currentStatus.equals(BankApplicationStatus.READY_TO_SENDING)) {
                                bankApplication.setBankApplicationStatus(BankApplicationStatus.READY_TO_SENDING);
                                isChange = true;
                            }
                        } else {
                            if (!currentStatus.equals(BankApplicationStatus.DATA_NO_ENTERED)) {
                                bankApplication.setBankApplicationStatus(BankApplicationStatus.DATA_NO_ENTERED);
                                isChange = true;
                            }
                        }
                    }
                }
            }
        }
        return isChange;
    }

    private boolean partnerApplicationStatusChanged(PartnerApplication partnerApplication) {
        boolean isChange = false;
        if (Objects.isNull(partnerApplication.getPartnerApplicationStatus())) {
            partnerApplication.setPartnerApplicationStatus(PartnerApplicationStatus.UPLOADING_DOCS);
            return true;
        }
        if (partnerApplication.getBankApplications().stream()
                .anyMatch(bankApplication -> bankApplication.getBankApplicationStatus().equals(BankApplicationStatus.CREDIT_APPROVED))) {
            if (!partnerApplication.getPartnerApplicationStatus().equals(PartnerApplicationStatus.CREDIT_APPROVED)) {
                partnerApplication.setPartnerApplicationStatus(PartnerApplicationStatus.CREDIT_APPROVED);
                isChange = true;
            }
        }
        if (partnerApplication.getBankApplications().stream()
                .allMatch(bankApplication -> bankApplication.getBankApplicationStatus().equals(BankApplicationStatus.EXPIRED))) {
            if (!partnerApplication.getPartnerApplicationStatus().equals(PartnerApplicationStatus.EXPIRED)) {
                partnerApplication.setPartnerApplicationStatus(PartnerApplicationStatus.EXPIRED);
                isChange = true;
            }
        }
        if (partnerApplication.getBankApplications().stream()
                .allMatch(bankApplication -> bankApplication.getBankApplicationStatus().equals(BankApplicationStatus.REJECTED))) {
            if (!partnerApplication.getPartnerApplicationStatus().equals(PartnerApplicationStatus.REJECTED)) {
                partnerApplication.setPartnerApplicationStatus(PartnerApplicationStatus.REJECTED);
                isChange = true;
            }
        }
        return isChange;
    }

    private boolean checkRequiredDocuments(BorrowerProfile borrowerProfile) {
        List<BorrowerDocument> borrowerDocuments = borrowerProfile.getBorrowerDocument();
        Set<DocumentType> documentTypes = borrowerDocuments.stream()
                .filter(BorrowerDocument::isActive)
                .map(BorrowerDocument::getDocumentType)
                .collect(Collectors.toSet());
        boolean majorDocumentIsPresent = documentTypes.containsAll(REQUIRED_DOCUMENT_TYPES);

        boolean proofOfIncomeIsPresent = (borrowerProfile.getProofOfIncome() == ProofOfIncome.NO_CONFIRMATION)
                || (borrowerProfile.getProofOfIncome() != null
                && documentTypes.contains(DocumentType.INCOME_CERTIFICATE));
        return majorDocumentIsPresent && proofOfIncomeIsPresent;
    }

    private boolean isIncomeInfoComplete(BorrowerProfile profile) {
        return profile.getMainIncome() != null && profile.getProofOfIncome() != null;
    }

    private boolean isEmployerInfoComplete(BorrowerProfile profile) {
        BorrowerEmployer employer = profile.getEmployer();
        if (profile.getEmploymentStatus() != null && profile.getEmploymentStatus() == EmploymentStatus.UNEMPLOYED)
            return true;
        else {
            return profile.getEmploymentStatus() != null
                    && profile.getTotalWorkExperience() != null
                    && !StringUtils.isEmpty(employer.getName())
                    && employer.getBranch() != null
                    && employer.getTin() != null
                    && employer.getTin().length() > 9
                    && !StringUtils.isEmpty(employer.getPhone())
                    && employer.getNumberOfEmployees() != null
                    && employer.getOrganizationAge() != null
                    && !StringUtils.isEmpty(employer.getAddress())
                    && employer.getWorkExperience() != null
                    && !StringUtils.isEmpty(employer.getPosition());
        }
    }

    private boolean isBorrowerMainInfoComplete(BorrowerProfile profile) {
        boolean isCommonInfoComplete = !Objects.requireNonNullElse(profile.getFirstName(), "").isBlank()
                && !Objects.requireNonNullElse(profile.getLastName(), "").isBlank()
                && profile.getPhoneNumber() != null
                && profile.getPhoneNumber().length() == 10
                && profile.getBirthdate() != null
                && profile.getGender() != null
                && !Objects.requireNonNullElse(profile.getSnils(), "").isBlank();
        if (profile.getEmploymentStatus() == null
                || profile.getEmploymentStatus().equals(EmploymentStatus.UNEMPLOYED)) {
            return isCommonInfoComplete;
        } else {
            return isCommonInfoComplete && profile.getEmployer() != null;
        }
    }

    private boolean isPassportInfoComplete(BorrowerProfile profile) {
        return !StringUtils.isEmpty(profile.getPassportNumber())
                && profile.getPassportIssuedDate() != null
                && !StringUtils.isEmpty(profile.getPassportIssuedByName())
                && !StringUtils.isEmpty(profile.getRegistrationAddress());
    }

    private boolean allBorrowerInSignedStatus(List<BorrowerProfile> borrowers) {
        return borrowers.stream()
                .filter(BorrowerProfile::isActive)
                .allMatch(x -> x.getBorrowerProfileStatus() == BorrowerProfileStatus.DOCS_SIGNED);
    }

}
