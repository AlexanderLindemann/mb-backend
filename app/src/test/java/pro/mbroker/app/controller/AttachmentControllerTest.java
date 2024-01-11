package pro.mbroker.app.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import pro.smartdeal.ng.attachment.api.AttachmentRestApi;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AttachmentControllerTest extends AbstractControllerTest {

    @MockBean
    private AttachmentRestApi attachmentService;

    @BeforeEach
    public void setup() {
        MultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "Test file content".getBytes());
        when(attachmentService.download(anyLong())).thenReturn(mockFile);
    }

    @Test
    public void testDownloadAttachmentWithAdminProperties() throws Exception {
        Mockito.when(currentUserService.getCurrentUserToken()).thenReturn(tokenWithAdminPermission);
        mockMvc.perform(get("/public/attachment/1/file")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenWithAdminPermission))
                .andExpect(status().isOk());
    }

    @Test
    public void testDownloadAttachmentWithoutAdminProperties() throws Exception {
        Mockito.when(currentUserService.getCurrentUserToken()).thenReturn(tokenWithoutAdminPermission);
        mockMvc.perform(get("/public/attachment/1/file")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenWithoutAdminPermission))
                .andExpect(status().isForbidden());
    }
}
