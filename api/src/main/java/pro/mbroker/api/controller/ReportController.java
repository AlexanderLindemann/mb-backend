package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.mbroker.api.dto.request.PartnerApplicationServiceRequest;

import javax.servlet.http.HttpServletResponse;

@Api(value = "API Отчета", tags = "API Отчета")
@RestController
@RequestMapping("/public/report")
public interface ReportController {

    @ApiOperation("получить report")
    @PostMapping("/partner_application")
    void getReport(HttpServletResponse response, @RequestBody PartnerApplicationServiceRequest request);
}
