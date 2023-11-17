package pro.mbroker.app.entity.underwriting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.app.entity.BaseEntity;

import javax.persistence.CascadeType;
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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_id", referencedColumnName = "id")
    private UnderwritingDecision underwritingDecision;

    @Column(name = "additional_conditions_step")
    private String additionalConditionsStep;

    @Column(name = "additional_conditions_description")
    private String additionalConditionsDescription; //Описание доп. условия

    @Column(name = "additional_conditions_responsible")
    private String  additionalConditionsResponsible; //Ответственный, кто будет рассматривать и снимать доп. условие


    @Column(name = "positive_decision_document_type")
    private Integer positiveDecisionDocumentType;

    @Column(name = "positive_decision_document_name")
    private String positiveDecisionDocumentName;

    @Column(name = "positive_decision_document_data")
    private byte[] positiveDecisionDocumentData;

    @Column(name = "positive_decision_extension")
    private String positiveDecisionExtension;

    @Column(name = "underwriting_report_document_type")
    private Integer underwritingReportDocumentType;

    @Column(name = "underwriting_report_document_name")
    private String underwritingReportDocumentName;

    @Column(name = "underwriting_report_document_data")
    private byte[] underwritingReportDocumentData;

    @Column(name = "underwriting_report_extension")
    private String underwritingReportExtension;


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "underwriting_error_id", referencedColumnName = "id")
    private UnderwritingError underwritingError;

    @Column(name = "opportunity_id")
    private String opportunityId;

}
