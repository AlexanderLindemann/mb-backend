package pro.mbroker.app.util;

import pro.mbroker.app.entity.underwriting.Underwriting;
import pro.mbroker.app.entity.underwriting.UnderwritingDecision;
import pro.mbroker.app.entity.underwriting.UnderwritingError;

import java.util.Arrays;
import java.util.Objects;

public class UnderwritingComparator {
    public static boolean underwritingIsChanged(Underwriting old, Underwriting updated) {
        if (old == null && updated == null) {
            return false;
        }

        if (old == null && updated != null) {
            return true;
        }

        if (decisionIsChanged(old.getUnderwritingDecision(), updated.getUnderwritingDecision())) {
            return true;
        }

        if (!Objects.equals(old.getAdditionalConditionsStep(), updated.getAdditionalConditionsStep()) ||
                !Objects.equals(old.getAdditionalConditionsDescription(), updated.getAdditionalConditionsDescription()) ||
                !Objects.equals(old.getAdditionalConditionsResponsible(), updated.getAdditionalConditionsResponsible())) {
            return true;
        }

        if (!Objects.equals(old.getPositiveDecisionDocumentType(), updated.getPositiveDecisionDocumentType()) ||
                !Objects.equals(old.getPositiveDecisionDocumentName(), updated.getPositiveDecisionDocumentName()) ||
                !Arrays.equals(old.getPositiveDecisionDocumentData(), updated.getPositiveDecisionDocumentData()) ||
                !Objects.equals(old.getPositiveDecisionExtension(), updated.getPositiveDecisionExtension())) {
            return true;
        }

        if (!Objects.equals(old.getUnderwritingReportDocumentType(), updated.getUnderwritingReportDocumentType()) ||
                !Objects.equals(old.getUnderwritingReportDocumentName(), updated.getUnderwritingReportDocumentName()) ||
                !Arrays.equals(old.getUnderwritingReportDocumentData(), updated.getUnderwritingReportDocumentData()) ||
                !Objects.equals(old.getUnderwritingReportExtension(), updated.getUnderwritingReportExtension())) {
            return true;
        }

        if (old.getUnderwritingError() == null && updated.getUnderwritingError() == null) {
            return false;
        }

        if (errorIsChanged(old.getUnderwritingError(), updated.getUnderwritingError())) {
            return true;
        }

        return false;
    }

    public static boolean decisionIsChanged(UnderwritingDecision old, UnderwritingDecision updated) {
        if (old == null && updated == null) {
            return false;
        }

        if (old == null && updated != null) {
            return true;
        }

        return !Objects.equals(old.getStatus(), updated.getStatus())
                || !Objects.equals(old.getDescription(), updated.getDescription())
                || !Objects.equals(old.getCreditAmount(), updated.getCreditAmount())
                || Double.compare(old.getInterestRate(), updated.getInterestRate()) != 0
                || !Objects.equals(old.getCreditTermYears(), updated.getCreditTermYears())
                || old.getApprovedSum() != updated.getApprovedSum()
                || old.getAnnuity() != updated.getAnnuity()
                || !Objects.equals(old.getEndDate(), updated.getEndDate())
                || !Objects.equals(old.getCreditProgramName(), updated.getCreditProgramName());
    }

    public static boolean errorIsChanged(UnderwritingError old, UnderwritingError updated) {
        if (old == null && updated == null) {
            return false;
        }

        if (old == null && updated != null) {
            return true;
        }

        return !Objects.equals(old.getCode(), updated.getCode())
                || !Objects.equals(old.getMessage(), updated.getMessage())
                || !Objects.equals(old.getSource(), updated.getSource())
                || !Objects.equals(old.getSourceDescription(), updated.getSourceDescription())
                || !Objects.equals(old.getAdditionalInfo(), updated.getAdditionalInfo());
    }
}
