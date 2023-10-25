package pro.mbroker.app.service;

import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.app.TestData;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "classpath:sql/test_data.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Disabled
public class BorrowerProfileServiceTest extends AbstractServiceTest {

    @Autowired
    private BorrowerProfileService borrowerProfileService;
    @Autowired
    private TestData testData;

    @Test
    public void testUpdateGenericBorrowerProfile() {
        BorrowerRequest borrowerRequest = new BorrowerRequest()
                .setMainBorrower(testData.getBorrowerProfileRequestList().get(0)
                        .setId(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003")))
                .setId(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"));

        BorrowerResponse orUpdateGenericBorrowerApplication = borrowerProfileService.createOrUpdateGenericBorrowerProfile(borrowerRequest);
        BorrowerProfileResponse mainBorrower = orUpdateGenericBorrowerApplication.getMainBorrower();
        assertThat(mainBorrower.getEmail(), Matchers.is("test@test.com"));
        assertThat(mainBorrower.getFirstName(), Matchers.is("TestFirstName"));
        assertThat(mainBorrower.getLastName(), Matchers.is("TestLastName"));
    }

    @Test
    public void testUpdateBorrowerProfile() {
        BorrowerRequest borrowerRequest = new BorrowerRequest()
                .setMainBorrower(testData.getBorrowerProfileRequestList().get(0)
                        .setId(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003")))
                .setId(UUID.fromString("3b339aa4-5462-485a-9118-5922cd948566"));
        BorrowerResponse orUpdateGenericBorrowerApplication = borrowerProfileService.createOrUpdateBorrowerProfile(borrowerRequest);
        BorrowerProfileResponse mainBorrower = orUpdateGenericBorrowerApplication.getMainBorrower();
        assertThat(mainBorrower.getEmail(), Matchers.is("test@test.com"));
        assertThat(mainBorrower.getFirstName(), Matchers.is("TestFirstName"));
        assertThat(mainBorrower.getLastName(), Matchers.is("TestLastName"));
    }

    @Test
    public void testUpdateBorrowerProfile2() {
        BorrowerResponse orUpdateGenericBorrowerApplication = borrowerProfileService.getBorrowersByPartnerApplicationId(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"));
        BorrowerProfileResponse mainBorrower = orUpdateGenericBorrowerApplication.getMainBorrower();
        assertThat(mainBorrower.getEmail(), Matchers.is("test@test.com"));
        assertThat(mainBorrower.getFirstName(), Matchers.is("Ivan"));
        assertThat(mainBorrower.getLastName(), Matchers.is("Ivanov"));
        assertThat(mainBorrower.getMiddleName(), Matchers.is("Ivanovich"));
        assertThat(mainBorrower.getPhoneNumber(), Matchers.is("+90000000000"));
    }

}
