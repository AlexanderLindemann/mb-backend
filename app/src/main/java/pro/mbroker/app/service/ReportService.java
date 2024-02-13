package pro.mbroker.app.service;

import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.exception.ReportGenerationException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ReportService {

    /**
     * Метод генерирует файл формата csv с отчетом о заявках партнера
     *
     * @param response            объект HttpServletResponse, в который должен быть записан отчет
     * @param partnerApplications список объектов PartnerApplication для формирования отчет
     * @throws ReportGenerationException при генерации отчета возникает ошибка
     */
    void generateCsvReport(HttpServletResponse response, List<PartnerApplication> partnerApplications);
}

