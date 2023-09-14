package pro.mbroker.app.service;

import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerProfile;

import java.util.Map;

public interface DocxFieldHandler {
    Map<String, String> replaceFieldValue(byte[] file, BankApplication bankApplication, BorrowerProfile borrowerProfile);
}
