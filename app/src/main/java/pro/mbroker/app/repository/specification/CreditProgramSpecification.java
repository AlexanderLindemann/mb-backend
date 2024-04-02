package pro.mbroker.app.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pro.mbroker.api.dto.request.CreditProgramServiceRequest;
import pro.mbroker.api.enums.CreditProgramType;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.CreditProgram;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
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

    public static Specification<CreditProgram> withActiveBank() {
        return (root, query, criteriaBuilder) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("bank", JoinType.LEFT);
            } else {
                root.join("bank", JoinType.LEFT);
            }
            Predicate programActive = criteriaBuilder.isTrue(root.get("isActive"));
            Predicate bankActive = criteriaBuilder.isTrue(root.get("bank").get("isActive"));
            return criteriaBuilder.and(programActive, bankActive);
        };
    }

    public static Specification<CreditProgram> withBankNameLike(String name) {
        return (root, query, cb) -> name != null
                ? cb.like(cb.lower(root.get("programName")), "%" + name.toLowerCase() + "%")
                : null;
    }

    public static Specification<CreditProgram> withBankId(Set<UUID> bankId) {
        return ((root, criteriaQuery, criteriaBuilder) -> Objects.nonNull(bankId)
                ? root.get("bank").get("id").in(bankId)
                : null);
    }

    public static Specification<CreditProgram> withCreditProgramType(Set<CreditProgramType> creditProgramTypes) {
        return ((root, criteriaQuery, criteriaBuilder) -> Objects.nonNull(creditProgramTypes)
                ? root.get("type").in(creditProgramTypes)
                : null);
    }

    public static Specification<CreditProgram> buildSpecification(CreditProgramServiceRequest request) {
        return Specification.where(isActive(true))
                .and(Objects.nonNull(request.getName()) ? withBankNameLike(request.getName()) : null)
                .and(Objects.nonNull(request.getCreditProgramTypes()) ? withCreditProgramType(request.getCreditProgramTypes()) : null);
    }
}

