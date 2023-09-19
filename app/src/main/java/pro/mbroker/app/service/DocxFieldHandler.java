package pro.mbroker.app.service;

import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.PartnerApplication;

import java.util.Map;

public interface DocxFieldHandler {
    Map<String, String> replaceFieldValue(byte[] file, PartnerApplication partnerApplication, BorrowerProfile borrowerProfile);
}