package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.PartnerApplicationStatus;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "partner_application")
public class PartnerApplication extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", referencedColumnName = "id")
    private Partner partner;

    @Column(name = "maternal_capital_amount", precision = 12, scale = 0)
    private BigDecimal maternalCapitalAmount;

    @Column(name = "subsidy_amount", precision = 12, scale = 0)
    private BigDecimal subsidyAmount;

    @Column(name = "payment_source")
    private String paymentSource;

    @Column(name = "insurances")
    private String insurances;

    @Column(name = "external_creator_id")
    private Integer externalCreatorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_purpose_type", nullable = false)
    private CreditPurposeType creditPurposeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "partner_application_status")
    private PartnerApplicationStatus partnerApplicationStatus;

    @Column(name = "real_estate_types", nullable = false)
    private String realEstateTypes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "real_estate_id", referencedColumnName = "id")
    private RealEstate realEstate;

    @OneToMany(mappedBy = "partnerApplication", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<BankApplication> bankApplications = new ArrayList<>();

    @OneToMany(mappedBy = "partnerApplication", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BorrowerProfile> borrowerProfiles = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "mortgage_calculation_id", referencedColumnName = "id")
    private MortgageCalculation mortgageCalculation;

    @Column(name = "updated_at", updatable = false)
    private LocalDateTime updatedAt;
}
