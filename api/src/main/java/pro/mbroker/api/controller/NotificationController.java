package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.response.NotificationBankLetterResponse;

import java.util.UUID;

@Api(value = "API Для взаимодействия с mb-notification", tags = "API Для взаимодействия с mb-notification")
@RestController
@RequestMapping("/public/notification")
public interface NotificationController {

    @ApiOperation("Данные клиента для отправки заявки в банк")
    @GetMapping("{bankApplicationId}/get_customer_info_for_bank_letter")
    NotificationBankLetterResponse getCustomerInfoForBankLetter(@PathVariable UUID bankApplicationId);

}
