package pro.mbroker.app.service;

import org.junit.Assert;
import org.junit.Test;
import pro.mbroker.app.util.TokenExtractor;

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
