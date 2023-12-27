package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credit_program")
public class CreditProgram extends BaseEntity {

    @Column(name = "credit_program_name", nullable = false)
    private String programName;

    @Column(name = "start_program_date")
    private LocalDateTime programStartDate;

    @Column(name = "end_program_date")
    private LocalDateTime programEndDate;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "full_description", length = 1000)
    private String fullDescription;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinColumn(name = "credit_program_detail_id", referencedColumnName = "id")
    private CreditProgramDetail creditProgramDetail;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinColumn(name = "credit_parameter_id", referencedColumnName = "id")
    private CreditParameter creditParameter;

    @DecimalMin(value = "0.00", inclusive = true, message = "Base rate cannot be negative")
    @DecimalMax(value = "100.00", inclusive = true, message = "Base rate cannot be greater than 100.00")
    private Double baseRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @DecimalMin(value = "-100.0000", inclusive = true, message = "Rate cannot be less -100.00")
    @DecimalMax(value = "100.0000", inclusive = true, message = "Rate cannot be greater than 100.00")
    private Double salaryClientInterestRate;

    @Column(name = "cian_id", nullable = true)
    private Integer cianId;
}
