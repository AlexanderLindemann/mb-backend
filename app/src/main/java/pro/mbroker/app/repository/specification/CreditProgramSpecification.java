package pro.mbroker.app.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.CreditProgram;

import javax.persistence.criteria.Join;
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
}

