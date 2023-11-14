package pro.mbroker.app.entity.underwriting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.app.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "underwriting_decision")
public class UnderwritingDecision extends BaseEntity {
    @Column(name = "integration_id")
    private String integrationId;

    @Column(name = "credit_amount")
    private int creditAmount;

    @Column(name = "interest_rate")
    private double interestRate;

    @Column(name = "credit_term_years")
    private int creditTermYears;

    @Column(name = "status")
    private int status;

    @Column(name = "description")
    private String description;

    @Column(name = "approved_sum")
    private int approvedSum;

    @Column(name = "annuity")
    private int annuity;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "credit_program_name")
    private String creditProgramName;

}
