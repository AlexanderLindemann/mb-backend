package pro.mbroker.app.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TokenParserTest extends AbstractServiceTest {

    @Test
    public void testExtractSdCurrentOrganizationId() {
        int extractSdCurrentOrganizationId = 2222;
        Assert.assertEquals(2633, extractSdCurrentOrganizationId);
    }

    @Test
    public void testExtractSdId() {
        int extractSdId = 2222;
        Assert.assertEquals(2956, extractSdId);
    }
}
