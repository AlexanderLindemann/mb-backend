package pro.mbroker.app.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.RealEstate;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
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

    public static Specification<RealEstate> realEstateByNameLike(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            } else {
                String likePattern = "%" + name.trim().toLowerCase() + "%";
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("residentialComplexName")), likePattern);
            }
        };
    }

    public static Specification<RealEstate> realEstateByPartnerIdAndIsActive(UUID partnerId) {
        return (root, query, criteriaBuilder) -> {
            Join<RealEstate, Partner> partnerJoin = root.join("partner");
            return criteriaBuilder.and(criteriaBuilder.equal(partnerJoin.get("id"), partnerId),
                    criteriaBuilder.isTrue(root.get("isActive")));
        };
    }

    public static Specification<RealEstate> realEstateByPartnerIdsAndIsActive(List<UUID> partnerIds) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            Join<RealEstate, Partner> partnerJoin = root.join("partner");
            Predicate partnerIdIn = partnerJoin.get("id").in(partnerIds);
            Predicate isActive = criteriaBuilder.isTrue(root.get("isActive"));
            return criteriaBuilder.and(partnerIdIn, isActive);
        });
    }
}

