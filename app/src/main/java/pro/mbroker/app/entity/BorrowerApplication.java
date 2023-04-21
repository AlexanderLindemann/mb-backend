package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import pro.mbroker.api.enums.ApplicationStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "borrower_application")
public class BorrowerApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_program_id", referencedColumnName = "id")
    private CreditProgram creditProgram;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status")
    private ApplicationStatus applicationStatus;

    @CreationTimestamp
    @Column(name = "last_edit_date")
    private ZonedDateTime lastEditDate;

    @Column(name = "monthly_payment")
    private BigDecimal monthlyPayment;

    @Column(name = "down_payment")
    private BigDecimal downPayment;

    @Column(name = "credit_term")
    private Integer monthCreditTerm;

    @Column(name = "overpayment")
    private BigDecimal overpayment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_application_id", nullable = false)
    private PartnerApplication partnerApplication;
}
