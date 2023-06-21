package pro.mbroker.app.exception;

import java.io.IOException;

public class ReportGenerationException extends RestException {
    public ReportGenerationException(IOException e) {
        super("Error occurred while generating CSV report" + e.getMessage());
    }

}
