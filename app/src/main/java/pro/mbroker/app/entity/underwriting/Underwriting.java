package pro.mbroker.app.entity.underwriting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.app.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "underwriting")
public class Underwriting extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_id", referencedColumnName = "id")
    private UnderwritingDecision underwritingDecision;

    @Column(name = "additional_conditions_step")
    private String additionalConditionsStep;

    @Column(name = "additional_conditions_description")
    private String additionalConditionsDescription; //Описание доп. условия

    @Column(name = "additional_conditions_responsible")
    private String  additionalConditionsResponsible; //Ответственный, кто будет рассматривать и снимать доп. условие


    @Column(name = "positive_decision_document_type")
    private Integer PositiveDecisionDocumentType;

    @Column(name = "positive_decision_document_name")
    private String PositiveDecisionDocumentName;

    @Column(name = "positive_decision_document_data")
    private String PositiveDecisionDocumentData;

    @Column(name = "positive_decision_extension")
    private String PositiveDecisionExtension;


    @Column(name = "underwriting_report_document_type")
    private Integer UnderwritingReportDocumentType;

    @Column(name = "underwriting_report_document_name")
    private String UnderwritingReportDocumentName;

    @Column(name = "underwriting_report_document_data")
    private String UnderwritingReportDocumentData;

    @Column(name = "underwriting_report_extension")
    private String UnderwritingReportExtension;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_id", referencedColumnName = "id")
    private UnderwritingError underwritingError;

    @Column(name = "opportunity_id")
    private String opportunityId;

}
