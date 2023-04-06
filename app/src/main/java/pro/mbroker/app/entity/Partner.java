package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.api.enums.PartnerType;
import pro.mbroker.api.enums.RegionType;

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
    //Название партнера
    @Column(name = "partner_name", nullable = false)
    private String partnerName;
    //Тип партнера
    @Column(name = "partner_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PartnerType partnerType;
    //Вид недвижимости
    @Column(name = "real_estate_type")
    private String realEstateType;
    //Название ЖК
    @Column(name = "residential_complex_name", nullable = false, length = 100)
    private String residentialComplexName;
    //Регион
    @Column(name = "region", nullable = false)
    @Enumerated(EnumType.STRING)
    private RegionType region;
    //Адрес
    @Column(name = "address", length = 100)
    private String address;
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
}
