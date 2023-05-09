package pro.mbroker.app.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.PartnerApplication;

public class PartnerApplicationSpecification {
    public static Specification<PartnerApplication> isActive() {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isActive")));
    }

    public static Specification<PartnerApplication> partnerApplicationByPartnerAndIsActive(Partner partner) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(criteriaBuilder.equal(root.get("partner"), partner),
                        criteriaBuilder.isTrue(root.get("isActive")));
    }

}

