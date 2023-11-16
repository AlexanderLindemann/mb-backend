package pro.mbroker.app.service;

import pro.mbroker.app.entity.PartnerApplication;

public interface StatusService {

    /**
     * Метод меняет статус PartnerApplication, BankApplication, BorrowerProfile
     *
     * @param application объект PartnerApplication
     * @return boolean было ли изменения статуса или он остался прежний
     */
    boolean statusChanger(PartnerApplication application);

}
