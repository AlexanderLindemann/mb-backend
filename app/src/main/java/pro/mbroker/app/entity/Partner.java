package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.api.enums.PartnerType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "partner")
public class Partner extends BaseEntity {
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
    //Программы банка
    @ManyToMany
    @JoinTable(
            name = "partner_credit_program",
            joinColumns = @JoinColumn(name = "partner_id"),
            inverseJoinColumns = @JoinColumn(name = "credit_program_id"))
    private Set<CreditProgram> creditPrograms = new HashSet<>();
    //Объекты застройщика
    @OneToMany(mappedBy = "partner", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<RealEstate> realEstates = new ArrayList<>();
    //Контакты партнера
    @OneToMany(mappedBy = "partner", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<PartnerContact> partnerContacts = new ArrayList<>();
    //Cian Id
    @Column(name = "cian_id")
    private Integer cianId;
}
