package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.ReportController;
import pro.mbroker.api.dto.request.PartnerApplicationServiceRequest;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.app.service.PartnerApplicationService;
import pro.mbroker.app.service.ReportService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportControllerImpl implements ReportController {
    private final PartnerApplicationService partnerApplicationService;
    private final ReportService reportService;

    @Override
    public void getReport(HttpServletResponse response, PartnerApplicationServiceRequest request) {
        List<PartnerApplicationResponse> partnerApplicationResponses = partnerApplicationService.getAllPartnerApplication(request).getContent()
                .stream()
                .map(partnerApplicationService::buildPartnerApplicationResponse)
                .collect(Collectors.toList());
        reportService.generateCsvReport(response, partnerApplicationResponses);
    }
}
