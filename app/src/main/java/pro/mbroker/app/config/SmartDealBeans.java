package pro.mbroker.app.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pro.smartdeal.ng.common.api.SdApiPackages;
import pro.smartdeal.ng.common.security.config.CommonWebSecurityConfigurationAdapter;

@Configuration
@ComponentScan({
        "pro.smartdeal.ng.common.security.service",
        "pro.smartdeal.ng.attachment.api.service"
})
@EnableFeignClients(basePackages = {
        SdApiPackages.ATTACHMENT
})
public class SmartDealBeans {

    @Bean
    public CommonWebSecurityConfigurationAdapter securityAdapter() {
        return new CommonWebSecurityConfigurationAdapter();
    }

}
