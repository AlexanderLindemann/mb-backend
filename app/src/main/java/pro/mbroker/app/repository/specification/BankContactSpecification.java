package pro.mbroker.app.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankContact;

import javax.persistence.criteria.Join;
import java.util.UUID;

public class BankContactSpecification {
    public static Specification<BankContact> isActive() {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isActive")));
    }
    public static Specification<BankContact> bankContactByBankIdAndIsActive(UUID bankId) {
        return (root, query, criteriaBuilder) -> {
            Join<BankContact, Bank> bankJoin = root.join("bank");
            return criteriaBuilder.and(criteriaBuilder.equal(bankJoin.get("id"), bankId),
                    criteriaBuilder.isTrue(root.get("isActive")));
        };
    }

}

