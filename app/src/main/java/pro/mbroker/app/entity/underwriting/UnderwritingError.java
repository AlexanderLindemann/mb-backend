package pro.mbroker.app.entity.underwriting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.app.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Underwriting_error")
public class UnderwritingError extends BaseEntity {

    @Column(name = "code")
    private String code;

    @Column(name = "message")
    private String message;

    @Column(name = "source")
    private String source;

    @Column(name = "source_description")
    private String sourceDescription;

    @Column(name = "additional_info")
    private String additionalInfo;
}
