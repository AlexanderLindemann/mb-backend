package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.ReportController;
import pro.mbroker.api.dto.request.PartnerApplicationServiceRequest;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.service.PartnerApplicationService;
import pro.mbroker.app.service.ReportService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportControllerImpl implements ReportController {
    private final PartnerApplicationService partnerApplicationService;
    private final ReportService reportService;

    @Override
    public void getReport(HttpServletResponse response, PartnerApplicationServiceRequest request) {
        request.setSize(Integer.MAX_VALUE);
        request.setPage(0);
        List<PartnerApplication> partnerApplicationResponses = partnerApplicationService.getAllPartnerApplication(request).getContent();
        reportService.generateCsvReport(response, partnerApplicationResponses);
    }
}
