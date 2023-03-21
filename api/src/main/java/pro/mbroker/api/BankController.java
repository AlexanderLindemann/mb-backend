package pro.mbroker.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.BankContactRequest;
import pro.mbroker.api.dto.BankResponse;

import java.util.UUID;

@Api("API Банка")
@RestController
@RequestMapping("/public/bank")
public interface BankController {

    @PostMapping
    ResponseEntity<BankResponse> createBank(
            @RequestParam("name") String name,
            @RequestParam("logo") MultipartFile logoFile
    );

    @GetMapping("/{id}")
    ResponseEntity<BankResponse> getBankById(@PathVariable UUID id);

    @PostMapping("/{id}/contacts")
    ResponseEntity<BankResponse> addBankContact(
            @PathVariable UUID id,
            @ModelAttribute BankContactRequest contactRequest);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteBank(@PathVariable UUID id);

    @DeleteMapping("/{bankId}/contacts/{contactId}")
    ResponseEntity<BankResponse> deleteBankContact(
            @PathVariable UUID contactId);
}
