package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.dto.request.notification.NotificationStatusRequest;
import pro.mbroker.api.dto.response.BankApplicationResponse;
import pro.mbroker.api.enums.BankApplicationStatus;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Api(value = "API Банковской Заявки", tags = "API Банковской Заявки")
@RestController
@RequestMapping("/public/v1/bank_application")
public interface BankApplicationController {

    @ApiOperation("получить банковскую заявку по id")
    @GetMapping("/{bankApplicationId}")
    BankApplicationResponse getBankApplicationById(@PathVariable UUID bankApplicationId);

    @ApiOperation("обновить банковскую заявку")
    @PutMapping()
    BankApplicationResponse updateBankApplication(@RequestBody BankApplicationRequest request, Integer sdId);

    @ApiOperation("поменять главного заемщика")
    @PutMapping("/{bankApplicationId}")
    BankApplicationResponse changeMainBorrowerByBankApplicationId(@PathVariable UUID bankApplicationId,
                                                                  @NotNull @RequestParam("newMainBorrowerId") UUID newMainBorrowerId, Integer sdId);

    @ApiOperation("обновить статус заявки")
    @PutMapping("/{bankApplicationId}/change_status")
    BankApplicationResponse changeStatus(@PathVariable UUID bankApplicationId,
                                         @NotNull @RequestParam("status") BankApplicationStatus status, Integer sdId);

    @ApiOperation("обновить статус заявок по ApplicationNumber")
    @PutMapping("/update-statuses")
    ResponseEntity<String> updateStatuses(@RequestBody NotificationStatusRequest bankApplications, Integer sdId);
}
