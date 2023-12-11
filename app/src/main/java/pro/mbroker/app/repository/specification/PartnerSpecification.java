package pro.mbroker.app.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pro.mbroker.app.entity.Partner;

import java.util.UUID;

public class PartnerSpecification {
    public static Specification<Partner> isActive() {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isActive")));
    }

    public static Specification<Partner> partnerByIdAndIsActive(UUID partnerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(criteriaBuilder.equal(root.get("id"), partnerId),
                        criteriaBuilder.isTrue(root.get("isActive")));
    }

    public static Specification<Partner> partnerByOrganizationIdAndIsActive(int organizationId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(criteriaBuilder.equal(root.get("smartDealOrganizationId"), organizationId),
                        criteriaBuilder.isTrue(root.get("isActive")));
    }

    public static Specification<Partner> partnerByCianIdOrName(Integer cianId, String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.or(
                                criteriaBuilder.equal(root.get("cianId"), cianId),
                                criteriaBuilder.equal(root.get("name"), name)
                        ),
                        criteriaBuilder.isTrue(root.get("isActive"))
                );
    }
}

