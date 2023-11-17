package pro.mbroker.app.mapper.underwriting;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pro.mbroker.api.dto.request.notification.AdditionalConditions;
import pro.mbroker.api.dto.request.notification.PositiveDecision;
import pro.mbroker.api.dto.request.notification.UnderwritingReport;
import pro.mbroker.api.dto.request.notification.UnderwritingResponse;
import pro.mbroker.app.entity.underwriting.Underwriting;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Mapper(uses = {UnderwritingDecisionMapper.class, UnderwritingErrorMapper.class})
public interface UnderwritingMapper {

    @Mapping(target = "decision", source = "underwritingDecision")
    @Mapping(target = "additionalConditionsStep", source = "additionalConditionsStep")
    @Mapping(target = "additionalConditions", expression = "java(toAdditionalConditions(underwriting))" )
    @Mapping(target = "positiveDecision", expression = "java(toPositiveDecision(underwriting))")
    @Mapping(target = "underwritingReport", expression = "java(toUnderwritingReport(underwriting))")
    @Mapping(target = "error", source = "underwritingError")
    @Mapping(target = "opportunityId", source = "opportunityId")
    UnderwritingResponse toUnderwritingResponse(Underwriting underwriting);

    @Mapping(target = "underwritingDecision", source = "decision")
    @Mapping(target = "additionalConditionsStep", source = "additionalConditionsStep")
    @Mapping(target = "additionalConditionsDescription", source = "additionalConditions.description")
    @Mapping(target = "additionalConditionsResponsible", source = "additionalConditions.responsible")

    @Mapping(target = "positiveDecisionDocumentType", source = "positiveDecision.documentType")
    @Mapping(target = "positiveDecisionDocumentName", source = "positiveDecision.documentName")
    @Mapping(target = "positiveDecisionDocumentData", source = "positiveDecision.documentData", qualifiedByName = "stringToBytes")
    @Mapping(target = "positiveDecisionExtension", source = "positiveDecision.extension")

    @Mapping(target = "underwritingReportDocumentType", source = "underwritingReport.documentType")
    @Mapping(target = "underwritingReportDocumentName", source = "underwritingReport.documentName")
    @Mapping(target = "underwritingReportDocumentData", source = "underwritingReport.documentData", qualifiedByName = "stringToBytes")
    @Mapping(target = "underwritingReportExtension", source = "underwritingReport.extension")

    @Mapping(target = "underwritingError", source = "error")
    @Mapping(target = "opportunityId", source = "opportunityId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    Underwriting toUnderwriting(UnderwritingResponse response);

    default PositiveDecision toPositiveDecision(Underwriting underwriting) {
        PositiveDecision positiveDecision = new PositiveDecision();
        positiveDecision.setDocumentType(underwriting.getPositiveDecisionDocumentType());
        positiveDecision.setDocumentName(underwriting.getPositiveDecisionDocumentName());
        positiveDecision.setExtension(underwriting.getPositiveDecisionExtension());
        positiveDecision.setDocumentData(Arrays.toString(underwriting.getPositiveDecisionDocumentData()));

        return positiveDecision;
    }

    default UnderwritingReport toUnderwritingReport(Underwriting underwriting) {
        UnderwritingReport underwritingReport = new UnderwritingReport();
        underwritingReport.setExtension(underwriting.getUnderwritingReportExtension());
        underwritingReport.setDocumentType(underwriting.getUnderwritingReportDocumentType());
        underwritingReport.setDocumentName(underwriting.getUnderwritingReportDocumentName());
        underwritingReport.setDocumentData(Arrays.toString(underwriting.getUnderwritingReportDocumentData()));

        return underwritingReport;
    }

    default AdditionalConditions toAdditionalConditions(Underwriting underwriting) {
        AdditionalConditions additionalConditions = new AdditionalConditions();
        additionalConditions.setDescription(underwriting.getAdditionalConditionsDescription());
        additionalConditions.setResponsible(underwriting.getAdditionalConditionsResponsible());
        return additionalConditions;
    }

    @Named("stringToBytes")
    static byte[] stringToBytes(String value) {
        return value != null ? value.getBytes(StandardCharsets.UTF_8) : null;
    }
}
