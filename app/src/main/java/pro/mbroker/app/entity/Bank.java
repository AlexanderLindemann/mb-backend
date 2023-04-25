package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    @Column(name = "logo_attachment_id")
    private Long logoAttachmentId;
    @Column(name = "order_number")
    private Integer orderNumber;

    @OneToMany(mappedBy = "bank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankContact> contacts;

    @OneToMany(mappedBy = "bank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CreditProgram> creditPrograms;

}
