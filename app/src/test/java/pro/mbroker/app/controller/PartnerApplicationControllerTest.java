package pro.mbroker.app.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PartnerApplicationControllerTest extends BaseControllerTest {

    @Test
    public void getPartnerApplicationWithAdminPermit() throws Exception {
        mockMvc.perform(get("/public/partner_application/" + PARTNER_APPLICATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(PARTNER_APPLICATION_ID)))
                .andExpect(jsonPath("$.creditPurposeType", is("PURCHASE_UNDER_CONSTRUCTION")))
                .andExpect(jsonPath("$.realEstateTypes", containsInAnyOrder("APARTMENT")))
                .andExpect(jsonPath("$.realEstate.id", is("2b8850b2-d930-11ed-afa1-0242ac120002")));
    }

    @Test
    public void getPartnerApplicationWithoutRequiredPermissions() throws Exception {
        mockMvc.perform(get("/public/partner_application/" + PARTNER_APPLICATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenWithoutAdminPermission))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getPartnerApplicationWithOrganizationPermissions() throws Exception {
        mockMvc.perform(get("/public/partner_application/" + PARTNER_APPLICATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenWithOrganizationPermission))
                .andExpect(status().isOk());
        System.out.println();
    }

    @Test
    public void getPartnerApplicationWithIncorrectOrganizationIdWithOrganizationPermissions() throws Exception {
        mockMvc.perform(get("/public/partner_application/" + PARTNER_APPLICATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenWithOrganizationPermission))
                .andExpect(status().isOk());
        System.out.println();
    }

    @Test
    public void getPartnerApplicationWithReadOwnPermissionsAndIncorrectSdId() throws Exception {
        Mockito.when(currentUserService.getCurrentUserToken()).thenReturn(tokenWithReadOwnPermission);
        mockMvc.perform(get("/public/partner_application/" + PARTNER_APPLICATION_ID2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenWithReadOwnPermission))
                .andExpect(status().isOk());
        System.out.println();
    }

    @Test
    public void getPartnerApplicationWithReadOwnPermissions() throws Exception {
        mockMvc.perform(get("/public/partner_application/" + PARTNER_APPLICATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenWithReadOwnPermission))
                .andExpect(status().isForbidden());
        System.out.println();
    }

    @Test
    public void testFilterIsActivePartnerApplicationWithAdminPermit() throws Exception {
        mockMvc.perform(get("/public/partner_application")
                        .param("isActive", "true")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[*].isActive", everyItem(is(true))));

        mockMvc.perform(get("/public/partner_application")
                        .param("isActive", "false")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[*].isActive", everyItem(is(false))));
    }

    //TODO написать тесты для isActive фильтра для роли MB_REQUEST_READ_ORGANIZATION
    //TODO написать тесты для isActive фильтра для роли MB_REQUEST_READ_OWN

    @Test
    public void testFilterPhoneNumberPartnerApplicationWithAdminPermit() throws Exception {
        mockMvc.perform(get("/public/partner_application")
                        .param("phoneNumber", "432")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[*].isActive", everyItem(is(true))))
                .andExpect(jsonPath("$.content[0].borrowerProfiles[0].phoneNumber", is("9876543219")))
                .andExpect(jsonPath("$.content[0].creditPurposeType", is("PURCHASE_UNDER_CONSTRUCTION")))
                .andExpect(jsonPath("$.content[0].realEstateTypes", containsInAnyOrder("APARTMENT")))
                .andExpect(jsonPath("$.content[0].realEstate.id", is("2b8850b2-d930-11ed-afa1-0242ac120002")));

        mockMvc.perform(get("/public/partner_application")
                        .param("phoneNumber", "218")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].borrowerProfiles[*].phoneNumber", hasItem(matchesPattern(".*218.*"))))
                .andExpect(jsonPath("$.content[1].borrowerProfiles[*].phoneNumber", hasItem(matchesPattern(".*218.*"))));

        mockMvc.perform(get("/public/partner_application")
                        .param("phoneNumber", "4218")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].borrowerProfiles[*].phoneNumber", hasItem(matchesPattern(".*4218.*"))));

        mockMvc.perform(get("/public/partner_application")
                        .param("phoneNumber", "1111")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    //TODO написать тесты для phoneNumber фильтра для роли MB_REQUEST_READ_ORGANIZATION
    //TODO написать тесты для phoneNumber фильтра для роли MB_REQUEST_READ_OWN

    @Test
    public void testFilterFullNamePartnerApplicationWithAdminPermit() throws Exception {
        mockMvc.perform(get("/public/partner_application")
                        .param("fullName", "Ivan")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].borrowerProfiles[*].firstName", Matchers.everyItem(Matchers.anyOf(
                        Matchers.equalTo("Ivan"),
                        Matchers.anything()
                ))))
                .andExpect(jsonPath("$.content[*].borrowerProfiles[*].lastName", Matchers.everyItem(Matchers.anyOf(
                        Matchers.equalTo("Ivan"),
                        Matchers.anything()
                ))))
                .andExpect(jsonPath("$.content[*].borrowerProfiles[*].middleName", Matchers.everyItem(Matchers.anyOf(
                        Matchers.equalTo("Ivan"),
                        Matchers.anything()
                ))));

        mockMvc.perform(get("/public/partner_application")
                        .param("fullName", "Ivanov Perviy")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[*].borrowerProfiles[*].firstName", Matchers.everyItem(Matchers.anyOf(
                        Matchers.equalTo("Ivanov Perviy"),
                        Matchers.anything()
                ))))
                .andExpect(jsonPath("$.content[*].borrowerProfiles[*].lastName", Matchers.everyItem(Matchers.anyOf(
                        Matchers.equalTo("Ivanov Perviy"),
                        Matchers.anything()
                ))))
                .andExpect(jsonPath("$.content[*].borrowerProfiles[*].middleName", Matchers.everyItem(Matchers.anyOf(
                        Matchers.equalTo("Ivanov Perviy"),
                        Matchers.anything()
                ))));

        mockMvc.perform(get("/public/partner_application")
                        .param("fullName", "     Ivan    PeTROV")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[*].borrowerProfiles[?(@.firstName == 'Ivan')]", hasSize(1)))
                .andExpect(jsonPath("$.content[*].borrowerProfiles[?(@.lastName == 'Petrov')]", hasSize(1)));

        mockMvc.perform(get("/public/partner_application")
                        .param("fullName", "Ivan Petrov Petrovich     ")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[*].borrowerProfiles[?(@.firstName == 'Ivan')]", hasSize(1)))
                .andExpect(jsonPath("$.content[*].borrowerProfiles[?(@.lastName == 'Petrov')]", hasSize(1)));
    }

    //TODO написать тесты для fullName фильтра для роли MB_REQUEST_READ_ORGANIZATION
    //TODO написать тесты для fullName фильтра для роли MB_REQUEST_READ_OWN

    @Test
    public void testFilterApplicationNumberPartnerApplicationWithAdminPermit() throws Exception {
        mockMvc.perform(get("/public/partner_application")
                        .param("applicationNumber", "10001")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[*].bankWithBankApplicationDto[*].bankApplications[*].applicationNumber", hasItem(10001)));

        mockMvc.perform(get("/public/partner_application")
                        .param("applicationNumber", "10002")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[*].bankWithBankApplicationDto[*].bankApplications[*].applicationNumber", hasItem(10002)));

        mockMvc.perform(get("/public/partner_application")
                        .param("applicationNumber", "0")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    //TODO написать тесты для applicationNumber фильтра для роли MB_REQUEST_READ_ORGANIZATION
    //TODO написать тесты для applicationNumber фильтра для роли MB_REQUEST_READ_OWN

    @Test
    public void testFilterRealEstateIdPartnerApplicationWithAdminPermit() throws Exception {
        mockMvc.perform(get("/public/partner_application")
                        .param("realEstateId", "2b8850b2-d930-11ed-afa1-0242ac120002")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].realEstate.id", everyItem(is("2b8850b2-d930-11ed-afa1-0242ac120002"))));

        mockMvc.perform(get("/public/partner_application")
                        .param("realEstateId", "51faca54-d930-11ed-afa1-0242ac120002")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        mockMvc.perform(get("/public/partner_application")
                        .param("realEstateId", "bd80be0e-f2db-11ed-a05b-0242ac120003")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[*].realEstate.id", everyItem(is("bd80be0e-f2db-11ed-a05b-0242ac120003"))));
    }
    //TODO написать тесты для realEstateId фильтра для роли MB_REQUEST_READ_ORGANIZATION
    //TODO написать тесты для realEstateId фильтра для роли MB_REQUEST_READ_OWN

    @Test
    public void testFilterRegionPartnerApplicationWithAdminPermit() throws Exception {
        mockMvc.perform(get("/public/partner_application")
                        .param("region", "VORONEZH")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].realEstate.region", everyItem(is("VORONEZH"))));

        mockMvc.perform(get("/public/partner_application")
                        .param("region", "CHUKOTKA")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        mockMvc.perform(get("/public/partner_application")
                        .param("region", "MOSCOW")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[*].realEstate.region", everyItem(is("MOSCOW"))));
    }
    //TODO написать тесты для region фильтра для роли MB_REQUEST_READ_ORGANIZATION
    //TODO написать тесты для region фильтра для роли MB_REQUEST_READ_OWN

    @Test
    public void testFilterBankIdPartnerApplicationWithAdminPermit() throws Exception {
        mockMvc.perform(get("/public/partner_application")
                        .param("bankId", "0c371042-d848-11ed-afa1-0242ac120002")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].bankWithBankApplicationDto[?(@.bankId == '0c371042-d848-11ed-afa1-0242ac120002')]", hasSize(greaterThanOrEqualTo(1))));
    }

    //TODO написать тесты для bankId фильтра для роли MB_REQUEST_READ_ORGANIZATION
    //TODO написать тесты для bankId фильтра для роли MB_REQUEST_READ_OWN

    @Test
    public void testFilterApplicationStatusPartnerApplicationWithAdminPermit() throws Exception {
        mockMvc.perform(get("/public/partner_application")
                        .param("applicationStatus", "DATA_NO_ENTERED")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].bankWithBankApplicationDto[*].bankApplications[?(@.status == 'DATA_NO_ENTERED')]", hasSize(greaterThanOrEqualTo(1))));

        mockMvc.perform(get("/public/partner_application")
                        .param("applicationStatus", "READY_TO_SENDING")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[*].bankWithBankApplicationDto[*].bankApplications[?(@.status == 'READY_TO_SENDING')]", hasSize(greaterThanOrEqualTo(1))));
    }

    //TODO написать тесты для applicationStatus фильтра для роли MB_REQUEST_READ_ORGANIZATION
    //TODO написать тесты для applicationStatus фильтра для роли MB_REQUEST_READ_OWN

    @Test
    public void testCombineFilterPartnerApplicationWithAdminPermit() throws Exception {
        mockMvc.perform(get("/public/partner_application")
                        .param("isActive", "true")
                        .param("phoneNumber", "218")
                        .param("fullName", "Ivan")
                        .param("applicationNumber", "10003")
                        .param("realEstateId", "2b8850b2-d930-11ed-afa1-0242ac120002")
                        .param("region", "VORONEZH")
                        .param("bankId", "0c371042-d848-11ed-afa1-0242ac120002")
                        .param("applicationStatus", "DATA_NO_ENTERED")
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].realEstate.id", is("2b8850b2-d930-11ed-afa1-0242ac120002")))
                .andExpect(jsonPath("$.content[0].realEstate.region", is("VORONEZH")))
                .andExpect(jsonPath("$.content[0].bankWithBankApplicationDto[0].bankId", is("0c371042-d848-11ed-afa1-0242ac120002")))
                .andExpect(jsonPath("$.content[0].bankWithBankApplicationDto[0].bankApplications[0].applicationNumber", is(10003)))
                .andExpect(jsonPath("$.content[0].bankWithBankApplicationDto[0].bankApplications[*].status", hasItem("DATA_NO_ENTERED")))
                .andExpect(jsonPath("$.content[0].borrowerProfiles[0].phoneNumber", containsString("218")))
                .andExpect(jsonPath("$.content[0].borrowerProfiles[0].firstName", is("Ivan")))
                .andExpect(jsonPath("$.content[0].borrowerProfiles[0].lastName", is("Petrov")));
    }

    //TODO написать комбинированный тест для всех полей фильтра для роли MB_REQUEST_READ_ORGANIZATION
    //TODO написать комбинированный тест для всех полей фильтра для роли MB_REQUEST_READ_OWN
}
