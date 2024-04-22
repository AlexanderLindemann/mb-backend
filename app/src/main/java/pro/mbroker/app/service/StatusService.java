package pro.mbroker.app.service;

import pro.mbroker.app.entity.PartnerApplication;

import java.util.UUID;

public interface StatusService {

    /**
     * Метод меняет статус PartnerApplication, BankApplication, BorrowerProfile
     *
     * @param application объект PartnerApplication
     * @return boolean было ли изменения статуса или он остался прежний
     */
    boolean statusChanger(PartnerApplication application);

    /**
     * Метод информирует - подписаны ли все документы по заявке PartnerApplication
     *
     * @param borrowerId объект UUID
     * @return boolean подписаны ли все документы по заявке
     */
    boolean isApplicationFullySigned(UUID borrowerId);
}
