package pro.mbroker.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@SpringBootApplication
@EnableJpaAuditing
public class MortgageBrokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MortgageBrokerApplication.class, args);
    }

}
