package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.app.entity.underwriting.Underwriting;

import javax.persistence.*;
import java.math.BigDecimal;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_borrower", referencedColumnName = "id")
    private BorrowerProfile mainBorrower;

    @Enumerated(EnumType.STRING)
    @Column(name = "bank_application_status")
    private BankApplicationStatus bankApplicationStatus;

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

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_number_seq")
    @SequenceGenerator(name = "application_number_seq", sequenceName = "application_number_seq", allocationSize = 1)
    @Column(name = "application_number", insertable = false, updatable = false)
    private Integer applicationNumber;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "partner_application_id", nullable = false)
    private PartnerApplication partnerApplication;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "underwriting_id", referencedColumnName = "id")
    private Underwriting underwriting;
}
