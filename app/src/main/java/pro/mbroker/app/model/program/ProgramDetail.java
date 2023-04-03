package pro.mbroker.app.model.program;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "program_detail")
public class ProgramDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String creditPurposeType;

    private String realEstateType;

    private String include;

    private String exclude;

}

