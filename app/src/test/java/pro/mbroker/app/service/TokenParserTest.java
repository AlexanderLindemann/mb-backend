package pro.mbroker.app.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import pro.mbroker.app.util.TokenExtractor;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yaml")
public class TokenParserTest {
    @Value("${test_token}")
    private String apiToken;

    @Test
    public void testExtractSdCurrentOrganizationId() {
        int extractSdCurrentOrganizationId = TokenExtractor.extractSdCurrentOrganizationId(apiToken);
        Assert.assertEquals(2633, extractSdCurrentOrganizationId);
    }

    @Test
    public void testExtractSdId() {
        int extractSdId = TokenExtractor.extractSdId(apiToken);
        Assert.assertEquals(2956, extractSdId);
    }
}
