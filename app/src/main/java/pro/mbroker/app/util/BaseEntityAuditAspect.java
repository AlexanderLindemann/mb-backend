package pro.mbroker.app.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BaseEntity;
import pro.mbroker.app.entity.PartnerApplication;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

import java.util.Collection;

@Aspect
@Component
@RequiredArgsConstructor
public class BaseEntityAuditAspect {
    @NonNull
    private final CurrentUserService currentUserService;

    @Before("execution(* pro.mbroker.app.repository.*.save(..)) && args(baseEntity, ..)")
    public void beforeSave(BaseEntity baseEntity) {
        setAuditFields(baseEntity);
        if (baseEntity instanceof PartnerApplication) {
            PartnerApplication partnerApplication = (PartnerApplication) baseEntity;
            if (partnerApplication.getBankApplications() != null) {
                for (BankApplication bankApplication : partnerApplication.getBankApplications()) {
                    setAuditFields(bankApplication);
                }
            }
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
        } else {
            baseEntity.setUpdatedBy(sdId);
        }
    }

    private int extractSdIdFromToken(String token) {
        return TokenExtractor.extractSdId(token);
    }
}

