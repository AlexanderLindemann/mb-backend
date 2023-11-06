package pro.mbroker.app.entity;

import lombok.Getter;
import lombok.Setter;
import pro.mbroker.api.enums.CreditProgramType;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "credit_program_detail")
public class CreditProgramDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String creditPurposeType;

    private String realEstateType;

    private String include;

    private String exclude;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_program_type", nullable = false)
    private CreditProgramType creditProgramType;
}

