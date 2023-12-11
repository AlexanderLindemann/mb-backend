package pro.mbroker.app.integration.cian;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "cianAPIClient", url = "${api.cian.api-url}")
public interface CianAPIClient {
    @GetMapping(value = "${api.cian.real-estate-path}", consumes = "application/json")
    RealEstateCianResponse getRealEstate();
}
