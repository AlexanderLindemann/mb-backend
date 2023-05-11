package pro.mbroker.app.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pro.mbroker.app.entity.Bank;

import java.util.UUID;

public class BankSpecification {
    public static Specification<Bank> isActive() {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isActive")));
    }

    public static Specification<Bank> bankByIdAndIsActive(UUID bankId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(criteriaBuilder.equal(root.get("id"), bankId),
                        criteriaBuilder.isTrue(root.get("isActive")));
    }
}

