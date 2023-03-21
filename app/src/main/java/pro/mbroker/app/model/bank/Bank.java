package pro.mbroker.app.model.bank;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "banks")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "logo")
    private String logo;

    @OneToMany(mappedBy = "bank", cascade = CascadeType.ALL)
    private List<BankContact> contacts;

}
