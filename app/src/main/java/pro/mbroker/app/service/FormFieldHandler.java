package pro.mbroker.app.service;

import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.PartnerApplication;

import java.util.Map;

public interface FormFieldHandler {

    Map<String, String> replaceFieldValue(PartnerApplication partnerApplication, BorrowerProfile borrowerProfile);
}
