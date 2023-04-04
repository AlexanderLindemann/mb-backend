package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class BankProgramRequest {

    @NotNull(message = "Program name is required")
    private String programName;

    private ZonedDateTime programStartDate;

    private ZonedDateTime programEndDate;

    @Size(max = 1000, message = "Description cannot be longer than 1000 characters")
    private String description;

    @Size(max = 1000, message = "Full description cannot be longer than 1000 characters")
    private String fullDescription;

    private List<CreditPurposeType> creditPurposeType;

    private List<RealEstateType> realEstateType;

    private List<RegionType> include;

    private List<RegionType> exclude;

    private CreditParameterResponse creditParameter;

    @DecimalMin(value = "0.00", inclusive = true, message = "Base rate cannot be negative")
    @DecimalMax(value = "100.00", inclusive = true, message = "Base rate cannot be greater than 100.00")
    private Double baseRate;

}
