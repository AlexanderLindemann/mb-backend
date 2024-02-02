package pro.mbroker.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.app.entity.Attachment;
import pro.mbroker.app.repository.AttachmentRepository;
import pro.smartdeal.ng.attachment.api.AttachmentRestApi;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;


public class AttachmentServiceTest extends BaseServiceTest {

    @Autowired
    private AttachmentService attachmentService;
    @MockBean
    private AttachmentRestApi attachmentRestApi;
    @MockBean
    private AttachmentRepository attachmentRepository;

    @BeforeEach
    public void setup() {
        when(attachmentRepository.findAttachmentById(anyLong())).thenReturn(Optional.ofNullable(new Attachment().setName("testFile").setId(1L)));
        MultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "Test file content".getBytes());
        when(attachmentRestApi.download(anyLong())).thenReturn(mockFile);
    }

    @Test
    public void testDownloadAttachment() {
        Mockito.when(attachmentRepository.findAttachmentById(anyLong())).thenReturn(Optional.ofNullable(new Attachment().setName("testFile").setId(1L)));
        MultipartFile download = attachmentService.download(1L);
        assertEquals(download.getName(), "file");
        assertEquals(download.getOriginalFilename(), "test.txt");
    }
}