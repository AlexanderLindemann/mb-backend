package pro.mbroker.app.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import pro.mbroker.api.dto.response.PublicKeyResponse;

@FeignClient(name = "publicKeyClient", url = "http://mb-dev-dc1:20100")
public interface PublicKeyClient {
    @GetMapping("/api/v1/token/public-key")
    PublicKeyResponse getPublicKey();
}

