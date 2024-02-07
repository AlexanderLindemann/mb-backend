package pro.mbroker.app.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${aws.accessKey}")
    private String ACCESS_KEY;
    @Value("${aws.secretKey}")
    private String SECRET_KEY;
    @Value("${aws.s3.service-endpoint}")
    private String S3_SERVICE_ENDPOINT;
    @Value("${aws.s3.region}")
    private String S3_SINGING_REGION;

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)))
                .withEndpointConfiguration(new AmazonS3ClientBuilder.EndpointConfiguration(S3_SERVICE_ENDPOINT, S3_SINGING_REGION))
                .build();
    }
}