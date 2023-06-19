package pro.mbroker.app.service;

import pro.mbroker.api.dto.response.NotificationBankLetterResponse;

public interface NotificationService {

    /**
     * Метод агрегирует информацию о пользователе
     * и его документах для формирования заявки,
     * которая будет отправлена в банк
     *
     * @param applicationNumber номер заявки
     * @return данные готовые для отправки в письме
     */
    NotificationBankLetterResponse getCustomerInfoForBankLetter(Integer applicationNumber);

}
