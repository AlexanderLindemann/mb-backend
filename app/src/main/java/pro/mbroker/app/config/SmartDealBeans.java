package pro.mbroker.app.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pro.mbroker.app.util.CachingRequestBodyFilter;
import pro.smartdeal.ng.common.api.SdApiPackages;
import pro.smartdeal.ng.common.security.config.CommonWebSecurityConfigurationAdapter;

@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
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

    @Bean
    public CachingRequestBodyFilter customCachingRequestBodyFilter() {
        return new CachingRequestBodyFilter();
    }
}
