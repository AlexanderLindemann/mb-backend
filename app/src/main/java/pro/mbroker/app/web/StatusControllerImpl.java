package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.StatusController;
import pro.mbroker.api.dto.response.SignatureFormStatusResponse;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.StatusService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusControllerImpl implements StatusController {
    private final StatusService statusService;
    private final BorrowerProfileService borrowerProfileService;

    @Override
    public SignatureFormStatusResponse isApplicationFullySigned(UUID borrowerId) {
        boolean applicationFullySigned =
                statusService.isApplicationFullySigned(borrowerProfileService.getBorrowerProfile(borrowerId));
        return new SignatureFormStatusResponse().setApplicationFullySigned(applicationFullySigned);
    }
}