package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.request.CreditParameterResponse;
import pro.mbroker.api.dto.request.CreditProgramDetailResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CreditProgramResponse {
    private UUID id;

    private String programName;

    private LocalDateTime programStartDate;

    private LocalDateTime programEndDate;

    private String description;

    private String fullDescription;

    private CreditProgramDetailResponse creditProgramDetail;

    private CreditParameterResponse creditParameter;

    private Double baseRate;

    private Double salaryClientInterestRate;

    private BankResponse bank;

    private LocalDateTime updatedAt;
}

