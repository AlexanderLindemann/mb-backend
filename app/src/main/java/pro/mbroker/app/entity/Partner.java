package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import pro.mbroker.api.enums.PartnerType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "partner")
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    //ID организации из смарта
    @Column(name = "smart_deal_organization_id", nullable = false)
    private Integer smartDealOrganizationId;
    //Название партнера
    @Column(name = "name", nullable = false, length = 1000)
    private String name;
    //Тип партнера
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PartnerType type;
    //Вид недвижимости
    @Column(name = "real_estate_type")
    private String realEstateType;
    //Цель кредита
    @Column(name = "credit_purpose_type")
    private String creditPurposeType;
    //Банк
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    private Bank bank;
    //Программы банка
    @ManyToMany
    @JoinTable(
            name = "partner_credit_program",
            joinColumns = @JoinColumn(name = "partner_id"),
            inverseJoinColumns = @JoinColumn(name = "credit_program_id"))
    private List<CreditProgram> creditPrograms = new ArrayList<>();
    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private List<RealEstateAddress> realEstateAddress = new ArrayList<>();

}
