package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.entity.underwriting.Underwriting;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
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

    /*
    Базовая процентная ставка, связанная с банковской заявкой,
    фиксируется на момент подачи заявки в банк,
    чтобы она оставалась неизменной на протяжении всего периода обработки.
     */
    @DecimalMin(value = "0.00", message = "Base rate cannot be negative")
    @DecimalMax(value = "100.00", message = "Base rate cannot be greater than 100.00")
    private Double lockBaseRate;
    /*
    Процентная ставка для зарплатного клиента, связанная с банковской заявкой,
    фиксируется на момент подачи заявки в банк,
    чтобы она оставалась неизменной на протяжении всего периода обработки.
    */
    @DecimalMin(value = "0.00", message = "Base rate cannot be negative")
    @DecimalMax(value = "100.00", message = "Base rate cannot be greater than 100.00")
    private Double lockSalaryBaseRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "real_estate_type", nullable = false)
    private RealEstateType realEstateType;

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_number_seq")
    @SequenceGenerator(name = "application_number_seq", sequenceName = "application_number_seq", allocationSize = 1)
    @Column(name = "application_number", insertable = false, updatable = false)
    private Integer applicationNumber;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "partner_application_id", nullable = false)
    private PartnerApplication partnerApplication;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "underwriting_id", referencedColumnName = "id")
    private Underwriting underwriting;
}
