package pro.mbroker.app.service;

import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.app.exception.ReportGenerationException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ReportService {

    /**
     * Метод генерирует файл формата csv с отчетом о заявках партнера
     *
     * @param response                    объект HttpServletResponse, в который должен быть записан отчет
     * @param partnerApplicationResponses список объектов PartnerApplicationResponse для формирования отчет
     * @throws ReportGenerationException при генерации отчета возникает ошибка
     */
    void generateCsvReport(HttpServletResponse response, List<PartnerApplicationResponse> partnerApplicationResponses);
}

