package pro.mbroker.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableWebSecurity
@SpringBootApplication
@EnableTransactionManagement
public class MortgageBrokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MortgageBrokerApplication.class, args);
    }

}
