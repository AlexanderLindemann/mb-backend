package pro.mbroker.app.service;

import pro.mbroker.api.dto.response.NotificationBankLetterResponse;

import java.util.UUID;

public interface NotificationService {

    /**
     * Метод агрегирует информацию о пользователе
     * и его документах для формирования заявки,
     * которая будет отправлена в банк
     *
     * @param bankApplicationId номер заявки
     * @return данные готовые для отправки в письме
     */
    NotificationBankLetterResponse getCustomerInfoForBankLetter(UUID bankApplicationId);
}
