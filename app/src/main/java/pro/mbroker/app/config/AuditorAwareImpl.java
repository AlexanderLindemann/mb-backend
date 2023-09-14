package pro.mbroker.app.config;

import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@SuppressWarnings("PMD")
public class AuditorAwareImpl implements AuditorAware<Integer> {

    @Override
    public @NonNull Optional<Integer> getCurrentAuditor() {
        String currentUserToken = "currentUserToken";
        return Optional.of(2222);
    }
}
