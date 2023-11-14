package pro.mbroker.api.dto.request.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Decision {
    private String integrationId;
    private int creditAmount;
    private double interestRate;
    private int creditTermYears;
    private int status;
    private String description;
    private int approvedSum;
    private int annuity;
    private LocalDateTime endDate;
    private String creditProgramName;

    public Decision() {}
}
