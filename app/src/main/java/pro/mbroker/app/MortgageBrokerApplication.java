package pro.mbroker.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableWebSecurity
@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients
@EnableScheduling
@EnableAsync
public class MortgageBrokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MortgageBrokerApplication.class, args);
    }

}
