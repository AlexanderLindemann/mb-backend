package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.NotificationController;
import pro.mbroker.api.dto.response.NotificationBankLetterResponse;
import pro.mbroker.app.service.NotificationService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationControllerImp implements NotificationController {

    private final NotificationService notificationService;

    @Override
    public NotificationBankLetterResponse getCustomerInfoForBankLetter(UUID bankApplicationId) {
        return notificationService.getCustomerInfoForBankLetter(bankApplicationId);
    }
}
