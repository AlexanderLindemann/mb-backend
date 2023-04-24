package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credit_program")
public class CreditProgram extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "credit_program_name", nullable = false)
    private String programName;

    @CreatedDate
    @Column(name = "start_program_date")
    private ZonedDateTime programStartDate;

    @Column(name = "end_program_date")
    private ZonedDateTime programEndDate;

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

}
