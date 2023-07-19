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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BankProgramRequest {
    @NotNull
    private UUID bankId;

    @NotNull(message = "Program name is required")
    private String programName;

    private LocalDateTime programStartDate;

    private LocalDateTime programEndDate;

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

    @DecimalMin(value = "-100.0000", inclusive = true, message = "Rate cannot be less -100.00")
    @DecimalMax(value = "100.0000", inclusive = true, message = "Rate cannot be greater than 100.00")
    private Double salaryClientInterestRate;

    //Установка начала времени действия программы на 00:00
    public void resetEarliestTime() {
        if (this.programStartDate != null) {
            this.programStartDate = this.programStartDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
    }

    //Установка времени окончания действия программы на 23:59
    public void resetLatestTime() {
        if (this.programEndDate != null) {
            this.programEndDate = this.programEndDate.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        }
    }


}
