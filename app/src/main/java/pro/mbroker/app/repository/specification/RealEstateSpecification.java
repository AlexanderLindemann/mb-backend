package pro.mbroker.app.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.RealEstate;

import javax.persistence.criteria.Join;
import java.util.List;
import java.util.UUID;

public class RealEstateSpecification {
    public static Specification<RealEstate> isActive() {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isActive")));
    }

    public static Specification<RealEstate> realEstateByIdAndIsActive(UUID realEstate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(criteriaBuilder.equal(root.get("id"), realEstate),
                        criteriaBuilder.isTrue(root.get("isActive")));
    }

    public static Specification<RealEstate> realEstateByPartnerIdAndIsActive(UUID partnerId) {
        return (root, query, criteriaBuilder) -> {
            Join<RealEstate, Partner> partnerJoin = root.join("partner");
            return criteriaBuilder.and(criteriaBuilder.equal(partnerJoin.get("id"), partnerId),
                    criteriaBuilder.isTrue(root.get("isActive")));
        };
    }
    public static Specification<RealEstate> realEstateByPartnerIdsAndIsActive(List<UUID> partnerIds) {
        return (root, query, criteriaBuilder) -> {
            Join<RealEstate, Partner> partnerJoin = root.join("partner");
            return criteriaBuilder.and(partnerJoin.get("id").in(partnerIds),
                    criteriaBuilder.isTrue(root.get("isActive")));
        };
    }

}

