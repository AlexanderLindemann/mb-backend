package pro.mbroker.app.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@ToString(of = "id")
@Entity
public class BaseMortgageInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Profile profile;

    @Column
    private String fio;

    @Column
    private BigDecimal propertyPrice;

    @Column
    private BigDecimal mortgageSum;

    @Column
    private BigDecimal firstPay;

    @Column
    private Integer durationMonths;

    @Column
    private String type;

}
