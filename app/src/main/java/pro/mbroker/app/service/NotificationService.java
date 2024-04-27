package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.notification.NotificationPartnerLetterRequest;
import pro.mbroker.api.dto.response.notification.NotificationBankLetterResponse;
import pro.mbroker.api.dto.response.notification.NotificationPartnerLetterResponse;

import java.util.Optional;
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

    /**
     * Метод агрегирует информацию из профиля заемщика и связанных с ним документов
     * для формирования ответа на уведомление, которое будет отправлено партнерам.
     * Это может быть использовано для уведомления о различных этапах заявки на ипотеку.
     *
     * @param request запрос, содержащий данные для идентификации заемщика и типа триггера
     * @return Optional с заполненным NotificationPartnerLetterResponse, если контакты партнера
     * имеют соответствующие триггеры для отправки уведомления и если документы полностью подписаны.
     * Возвращает пустой Optional, если условия для отправки уведомления не удовлетворены,
     * например, если не подписаны документы или отсутствуют контакты для отправки уведомления.
     */
    Optional<NotificationPartnerLetterResponse> getPartnerInfoForPartnerLetter(NotificationPartnerLetterRequest request);
}
