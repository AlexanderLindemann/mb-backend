package pro.mbroker.app.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import pro.mbroker.app.entity.BaseEntity;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class BaseEntityAuditAspect {
    @NonNull
    private final CurrentUserService currentUserService;

    @Before("execution(* pro.mbroker.app.repository.*.save(..)) && args(baseEntity, ..)")
    public void beforeSave(JoinPoint joinPoint, BaseEntity baseEntity) {
        String currentUserToken = currentUserService.getCurrentUserToken();
        int sdId = extractSdIdFromToken(currentUserToken);

        if (baseEntity.getCreatedBy() == null) {
            baseEntity.setCreatedBy(sdId);
            baseEntity.setCreatedAt(LocalDateTime.now());
            baseEntity.setUpdatedAt(LocalDateTime.now());
        } else {
            baseEntity.setUpdatedBy(sdId);
            baseEntity.setUpdatedAt(LocalDateTime.now());
        }
    }

    private int extractSdIdFromToken(String token) {
        return TokenExtractor.extractSdId(token);
    }
}
