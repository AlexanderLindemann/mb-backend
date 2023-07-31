package pro.mbroker.app.config;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import pro.mbroker.app.util.TokenExtractor;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Integer> {

    private final CurrentUserService currentUserService;

    @Autowired
    public AuditorAwareImpl(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Override
    public @NonNull Optional<Integer> getCurrentAuditor() {
        String currentUserToken = currentUserService.getCurrentUserToken();
        return Optional.of(TokenExtractor.extractSdId(currentUserToken));
    }
}
