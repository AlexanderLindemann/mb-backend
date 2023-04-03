package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
public class ProgramRequest {
    private UUID id;
    private String programName;
    private ZonedDateTime programStartDate;
    private ZonedDateTime programEndDate;
    private String description;
    private String fullDescription;
    private ProgramDetailRequest programDetail;
    private CreditParameterRequest creditParameter;
    private Double baseRate;
}

