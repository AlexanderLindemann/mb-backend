package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.ReportController;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.app.service.PartnerApplicationService;
import pro.mbroker.app.service.ReportService;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportControllerImpl implements ReportController {
    private final PartnerApplicationService partnerApplicationService;
    private final ReportService reportService;

    @Override
    public void getReport(HttpServletResponse response, int page, int size, String sortBy, String sortOrder, LocalDateTime startDate, LocalDateTime endDate) {
        List<PartnerApplicationResponse> partnerApplicationResponses = partnerApplicationService.getAllPartnerApplication(page, size, sortBy, sortOrder, startDate, endDate)
                .stream()
                .map(partnerApplicationService::buildPartnerApplicationResponse)
                .collect(Collectors.toList());
        reportService.generateCsvReport(response, partnerApplicationResponses);
    }
}
