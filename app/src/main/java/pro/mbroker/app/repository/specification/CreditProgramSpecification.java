package pro.mbroker.app.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pro.mbroker.api.dto.request.CreditProgramServiceRequest;
import pro.mbroker.api.enums.CreditProgramType;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.CreditProgramDetail;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class CreditProgramSpecification {
    public static Specification<CreditProgram> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    public static Specification<CreditProgram> creditProgramByBankIdAndIsActive(UUID bankId) {
        return (root, query, criteriaBuilder) -> {
            Join<CreditProgram, Bank> bankJoin = root.join("bank");
            return criteriaBuilder.and(criteriaBuilder.equal(bankJoin.get("id"), bankId),
                    criteriaBuilder.isTrue(root.get("isActive")));
        };
    }

    public static Specification<CreditProgram> creditProgramByIdAndIsActive(UUID creditProgramId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(criteriaBuilder.equal(root.get("id"), creditProgramId),
                        criteriaBuilder.isTrue(root.get("isActive")));
    }

    public static Specification<CreditProgram> withBankNameLike(String name) {
        return (root, query, cb) -> name != null
                ? cb.like(cb.lower(root.get("programName")), "%" + name.toLowerCase() + "%")
                : null;
    }

    public static Specification<CreditProgram> withBankId(Set<UUID> bankId) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            Join<CreditProgram, Bank> bankJoin = root.join("bank");
            return bankJoin.get("id").in(bankId);
        });
    }

    public static Specification<CreditProgram> withCianId(Integer cianId) {
        return ((root, criteriaQuery, criteriaBuilder) -> Objects.nonNull(cianId)
                ? criteriaBuilder.equal(root.get("cianId"), cianId)
                : null);
    }

    public static Specification<CreditProgram> withCreditProgramType(Set<CreditProgramType> creditProgramTypes) {
        return (root, query, cb) -> {
            if (Objects.isNull(creditProgramTypes)) return null;
            Join<CreditProgram, CreditProgramDetail> detailJoin = root.join("creditProgramDetail");
            return detailJoin.get("creditProgramType").in(creditProgramTypes);
        };
    }

    public static Specification<CreditProgram> withCreditPurposeType(Set<CreditPurposeType> creditProgramTypes) {
        return (root, query, cb) -> {
            if (creditProgramTypes == null || creditProgramTypes.isEmpty()) {
                return null;
            }
            Join<CreditProgram, CreditProgramDetail> detailJoin = root.join("creditProgramDetail");
            Predicate predicate = cb.disjunction();
            for (CreditPurposeType creditPurposeType : creditProgramTypes) {
                predicate = cb.or(predicate, cb.like(cb.function("CONCAT",
                        String.class, cb.literal("%,"), detailJoin.get("creditPurposeType"),
                        cb.literal(",%")), "%," + creditPurposeType.getValue() + ",%"));
            }
            return predicate;
        };
    }

    private static Specification<CreditProgram> withRealEstateTypes(Set<RealEstateType> realEstateTypes) {
        return (root, query, cb) -> {
            if (realEstateTypes == null || realEstateTypes.isEmpty()) {
                return null;
            }
            Join<CreditProgram, CreditProgramDetail> detailJoin = root.join("creditProgramDetail");
            Predicate predicate = cb.disjunction();
            for (RealEstateType realEstateType : realEstateTypes) {
                predicate = cb.or(predicate, cb.like(cb.function("CONCAT",
                        String.class, cb.literal("%,"), detailJoin.get("realEstateType"),
                        cb.literal(",%")), "%," + realEstateType.getValue() + ",%"));
            }
            return predicate;
        };
    }

    public static Specification<CreditProgram> withIncludedRegion(Set<RegionType> includedRegions) {
        return (root, query, cb) -> {
            if (includedRegions == null || includedRegions.isEmpty()) {
                return null;
            }
            Join<CreditProgram, CreditProgramDetail> detailJoin = root.join("creditProgramDetail");
            Predicate predicate = cb.disjunction();
            for (RegionType region : includedRegions) {
                String regionPattern = "%" + region.name() + "%";
                predicate = cb.or(predicate, cb.like(cb.function("CONCAT",
                        String.class, cb.literal(","), detailJoin.get("include"),
                        cb.literal(",")), regionPattern));
            }
            return predicate;
        };
    }

    public static Specification<CreditProgram> buildSpecification(CreditProgramServiceRequest request) {
        return Specification.where(isActive(true))
                .and(Objects.nonNull(request.getCreditPurposeTypes()) ? withCreditPurposeType(request.getCreditPurposeTypes()) : null)
                .and(Objects.nonNull(request.getBanks()) ? withBankId(request.getBanks()) : null)
                .and(Objects.nonNull(request.getCianId()) ? withCianId(request.getCianId()) : null)
                .and(Objects.nonNull(request.getRegions()) ? withIncludedRegion(request.getRegions()) : null)
                .and(Objects.nonNull(request.getName()) ? withBankNameLike(request.getName()) : null)
                .and(Objects.nonNull(request.getRealEstateTypes()) ? withRealEstateTypes(request.getRealEstateTypes()) : null)
                .and(Objects.nonNull(request.getCreditProgramTypes()) ? withCreditProgramType(request.getCreditProgramTypes()) : null);
    }
}