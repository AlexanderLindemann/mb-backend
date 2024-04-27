package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.mbroker.api.dto.request.notification.NotificationPartnerLetterRequest;
import pro.mbroker.api.dto.response.notification.NotificationBankLetterResponse;
import pro.mbroker.api.dto.response.notification.NotificationPartnerLetterResponse;

import java.util.Optional;
import java.util.UUID;

@Api(value = "API Для взаимодействия с mb-notification", tags = "API Для взаимодействия с mb-notification")
@RestController
@RequestMapping("/public/notification")
public interface NotificationController {

    @ApiOperation("Данные клиента для отправки заявки в банк")
    @GetMapping("{bankApplicationId}/get_customer_info_for_bank_letter")
    NotificationBankLetterResponse getCustomerInfoForBankLetter(@PathVariable UUID bankApplicationId);

    @ApiOperation("Данные клиента для отправки уведомления партнеру")
    @PostMapping("/service/application")
    Optional<NotificationPartnerLetterResponse> getPartnerInfoForPartnerLetter(@RequestBody(required = false) NotificationPartnerLetterRequest request);
}
