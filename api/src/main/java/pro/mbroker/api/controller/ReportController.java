package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Api(value = "API Отчета", tags = "API Отчета")
@RestController
@RequestMapping("/public/report")
public interface ReportController {
    @ApiOperation("получить report")
    @GetMapping("/partner_application")
    void getReport(HttpServletResponse response,
                   @RequestParam(defaultValue = "0") int page,
                   @RequestParam(defaultValue = "10") int size,
                   @RequestParam(defaultValue = "updatedAt") String sortBy,
                   @RequestParam(defaultValue = "asc") String sortOrder,
                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate);

}
