package pro.mbroker.app.service;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.mbroker.app.util.TokenExtractor;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
public class TokenParserTest extends AbstractServiceTest {

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
