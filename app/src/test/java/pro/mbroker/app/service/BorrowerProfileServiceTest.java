package pro.mbroker.app.service;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.app.TestData;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BorrowerProfileServiceTest extends BaseServiceTest {
    @Autowired
    private BorrowerProfileService borrowerProfileService;
    @Autowired
    private TestData testData;

    @Test
    public void testUpdateGenericBorrowerProfile() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080"));
        BorrowerRequest borrowerRequest = new BorrowerRequest()
                .setMainBorrower(testData.getBorrowerProfileRequestList().get(0)
                        .setId(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003")))
                .setId(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"));

        BorrowerResponse orUpdateGenericBorrowerApplication = borrowerProfileService.createOrUpdateGenericBorrowerProfile(borrowerRequest, mockRequest, 1234);
        orUpdateGenericBorrowerApplication.getCoBorrower().forEach(profile -> Assert.assertTrue(Objects.nonNull(profile.getLink())));
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
        BorrowerResponse orUpdateGenericBorrowerApplication = borrowerProfileService.createOrUpdateBorrowerProfile(borrowerRequest, 1234);
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
        assertThat(mainBorrower.getLastName(), Matchers.is("Ivanov Perviy"));
        assertThat(mainBorrower.getMiddleName(), Matchers.is("Ivanovich"));
        assertThat(mainBorrower.getPhoneNumber(), Matchers.is("9876543219"));
    }

}
