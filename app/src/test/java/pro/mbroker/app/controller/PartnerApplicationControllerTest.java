package pro.mbroker.app.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
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
                .andExpect(jsonPath("$.realEstateType", is("APARTMENT")))
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

}
