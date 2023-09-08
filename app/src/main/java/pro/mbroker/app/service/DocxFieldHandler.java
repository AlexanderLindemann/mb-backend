package pro.mbroker.app.service;

import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerProfile;

import java.util.Map;

public interface DocxFieldHandler {
    Map<String, String> replaceFieldValue(MultipartFile file, BankApplication bankApplication, BorrowerProfile borrowerProfile);
}
