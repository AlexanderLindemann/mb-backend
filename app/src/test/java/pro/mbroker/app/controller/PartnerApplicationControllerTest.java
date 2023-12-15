package pro.mbroker.app.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PartnerApplicationControllerTest extends AbstractControllerTest {

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
                .andExpect(jsonPath("$.content", hasSize(2)))
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

    }

}
