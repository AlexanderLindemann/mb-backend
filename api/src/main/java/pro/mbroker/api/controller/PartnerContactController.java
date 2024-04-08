package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.mbroker.api.dto.request.PartnerContactRequest;
import pro.mbroker.api.dto.response.PartnerContactResponse;

import java.util.List;
import java.util.UUID;

@Api(value = "API Контактов Партнера", tags = "API Контактов Партнера")
@RestController
@RequestMapping("/public/partner_contact")
public interface PartnerContactController {

    @ApiOperation("удалить контакт партнера по contactId")
    @DeleteMapping("/{id}")
    void deletePartnerContact(@PathVariable UUID id, Integer sdId);

    @ApiOperation("получить контакты партнера по partnerId")
    @GetMapping("/{partnerId}")
    List<PartnerContactResponse> getPartnerContacts(@PathVariable UUID partnerId);

    @ApiOperation("добавить контакт партнера по partnerId")
    @PostMapping("/{partnerId}")
    PartnerContactResponse addPartnerContact(@PathVariable UUID partnerId, Integer sdId, @RequestBody PartnerContactRequest request);

    @ApiOperation("отредактировать контакт партнера по contactId")
    @PutMapping("/{id}")
    PartnerContactResponse editPartnerContact(@PathVariable UUID id, Integer sdId, @RequestBody PartnerContactRequest request);
}