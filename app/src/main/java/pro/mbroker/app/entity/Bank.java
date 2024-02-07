package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank")
public class Bank extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "order_number")
    private Integer orderNumber;

    @OneToMany(mappedBy = "bank", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<BankContact> contacts;

    @OneToMany(mappedBy = "bank", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<CreditProgram> creditPrograms;

    @Column(name = "cian_id")
    private Integer cianId;

    @OneToOne
    @JoinColumn(name = "file_storage_id", referencedColumnName = "id")
    private FileStorage logoFileStorage;
}
