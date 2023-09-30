package pro.mbroker.app.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pro.smartdeal.ng.common.api.SdApiPackages;

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


}
