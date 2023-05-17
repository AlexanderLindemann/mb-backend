package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.api.enums.ApplicationStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank_application")
public class BankApplication extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_program_id", referencedColumnName = "id")
    private CreditProgram creditProgram;

    @Column(name = "main_borrower")
    private UUID mainBorrower;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status")
    private ApplicationStatus applicationStatus;

    @Column(name = "monthly_payment")
    private BigDecimal monthlyPayment;

    @Column(name = "real_estate_price")
    private BigDecimal realEstatePrice;

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
