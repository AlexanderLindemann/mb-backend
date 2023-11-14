package pro.mbroker.api.dto.request.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnderwritingResponse {
    private Decision decision;

    private String additionalConditionsStep;
    private AdditionalConditions additionalConditions;
    private PositiveDecision positiveDecision;
    private UnderwritingReport underwritingReport;
    private Error error;
    private String opportunityId;
    public UnderwritingResponse() {}
}
