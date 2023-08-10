package pro.mbroker.app.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import pro.mbroker.app.entity.*;
import pro.mbroker.app.repository.PartnerApplicationRepository;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

import java.time.LocalDateTime;
import java.util.Collection;

@Aspect
@Component
@RequiredArgsConstructor
public class BaseEntityAuditAspect {

    @NonNull
    private final CurrentUserService currentUserService;
    private final PartnerApplicationRepository partnerApplicationRepository;

    @Before("execution(* pro.mbroker.app.repository.*.save(..)) && args(baseEntity, ..)")
    public void beforeSave(BaseEntity baseEntity) {
        setAuditFields(baseEntity);
        if (baseEntity instanceof MortgageCalculation) {
            PartnerApplication partnerApplication = partnerApplicationRepository.findByMortgageCalculation((MortgageCalculation) baseEntity);
            if (partnerApplication != null && partnerApplication.isActive()) {
                setAuditFields(partnerApplication);
            }
            return;
        }

        if (baseEntity instanceof BankApplication) {
            setAuditFields(((BankApplication) baseEntity).getPartnerApplication());
            return;
        }

        if (baseEntity instanceof BorrowerProfile) {
            setAuditFields(((BorrowerProfile) baseEntity).getPartnerApplication());
            return;
        }

        if (baseEntity instanceof PartnerApplication) {
            PartnerApplication partnerApplication = (PartnerApplication) baseEntity;
            partnerApplication.getBankApplications().forEach(this::setAuditFields);
            partnerApplication.getBorrowerProfiles().forEach(this::setAuditFields);
            if (partnerApplication.getMortgageCalculation() != null) {
                setAuditFields(partnerApplication.getMortgageCalculation());
            }
            return;
        }

        if (baseEntity instanceof Bank && ((Bank) baseEntity).getContacts() != null) {
            ((Bank) baseEntity).getContacts().forEach(this::setAuditFields);
            return;
        }

        if (baseEntity instanceof Partner && ((Partner) baseEntity).getRealEstates() != null) {
            ((Partner) baseEntity).getRealEstates().forEach(this::setAuditFields);
        }
    }

    @Before("execution(* pro.mbroker.app.repository.*.saveAll(..)) && args(entities, ..)")
    public void beforeSaveAll(Collection<BaseEntity> entities) {
        entities.forEach(this::beforeSave);
    }

    private void setAuditFields(BaseEntity baseEntity) {
        String currentUserToken = currentUserService.getCurrentUserToken();
        int sdId = extractSdIdFromToken(currentUserToken);
        if (baseEntity.getCreatedBy() == null) {
            baseEntity.setCreatedBy(sdId);
            baseEntity.setCreatedAt(LocalDateTime.now());
        } else {
            baseEntity.setUpdatedBy(sdId);
            baseEntity.setUpdatedAt(LocalDateTime.now());
        }
    }

    private int extractSdIdFromToken(String token) {
        return TokenExtractor.extractSdId(token);
    }
}


