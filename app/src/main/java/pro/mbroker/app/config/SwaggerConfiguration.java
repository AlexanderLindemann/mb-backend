package pro.mbroker.app.config;

import com.google.common.base.Predicates;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@RequiredArgsConstructor
@SuppressWarnings("PMD")
public class SwaggerConfiguration {

    private static final String BASE_PROJECT_PACKAGE = "pro.mbroker.app.web";
    private static final String PUBLIC_API_ANT_PATTERN = "/public/**";
    private final BuildProperties buildProperties;

    @Bean
    public Docket internalApiDocket() {
        String title = "Private";

        ApiInfo apiInfo = new ApiInfoBuilder()
                .title(title)
                .description("API for internal services usage")
                .version(buildProperties.getVersion())
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(title)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage(BASE_PROJECT_PACKAGE))
                .paths(Predicates.not(PathSelectors.ant(PUBLIC_API_ANT_PATTERN)))
                .build();
    }

    @Bean
    public Docket publicApiDocket() {
        String title = "Public";

        ApiInfo apiInfo = new ApiInfoBuilder()
                .title(title)
                .description("API accessible by Frontend client via API gateway")
                .version(buildProperties.getVersion())
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(title)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage(BASE_PROJECT_PACKAGE))
                .paths(PathSelectors.any())
                .build();
    }
}

