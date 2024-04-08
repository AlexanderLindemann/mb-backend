package pro.mbroker.app.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pro.mbroker.app.entity.PartnerContact;

import java.util.UUID;

public class PartnerContactSpecification {
    public static Specification<PartnerContact> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    public static Specification<PartnerContact> partnerContactByPartnerId(UUID partnerId) {
        return ((root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.join("partner").get("id"), partnerId));
    }
}