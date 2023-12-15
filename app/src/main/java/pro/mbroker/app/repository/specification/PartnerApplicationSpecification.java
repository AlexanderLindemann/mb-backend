package pro.mbroker.app.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.entity.RealEstate;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PartnerApplicationSpecification {

    public static Specification<PartnerApplication> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    public static Specification<PartnerApplication> fullNameLike(String fullName) {
        return (root, query, criteriaBuilder) -> {
            if (fullName == null || fullName.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String[] nameParts = fullName.trim().toLowerCase().split("\\s+");
            List<Predicate> predicates = new ArrayList<>();

            Join<PartnerApplication, BorrowerProfile> borrowerProfileJoin = root.join("borrowerProfiles", JoinType.INNER);

            for (String part : nameParts) {
                String likePattern = "%" + part + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(borrowerProfileJoin.get("firstName")), likePattern));
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(borrowerProfileJoin.get("lastName")), likePattern));
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(borrowerProfileJoin.get("middleName")), likePattern));
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<PartnerApplication> partnerIdEquals(UUID partnerId) {
        return (root, query, criteriaBuilder) ->
                partnerId == null ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("partner").get("id"), partnerId);
    }

    public static Specification<PartnerApplication> applicationNumberEquals(Integer applicationNumber) {
        return ((root, query, criteriaBuilder) -> {
            Join<PartnerApplication, BankApplication> bankApplicationJoin = root.join("bankApplications", JoinType.INNER);
            return criteriaBuilder.equal(bankApplicationJoin.get("applicationNumber"), applicationNumber);
        });
    }

    public static Specification<PartnerApplication> createdByEquals(Integer createdBy) {
        return (root, query, criteriaBuilder) ->
                createdBy == null ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("createdBy"), createdBy);
    }

    public static Specification<PartnerApplication> phoneNumberLike(String phoneNumber) {
        return (root, query, criteriaBuilder) -> {
            Join<PartnerApplication, BorrowerProfile> borrowerProfileJoin = root.join("borrowerProfiles");
            return phoneNumber == null ? null : criteriaBuilder
                    .like(borrowerProfileJoin.get("phoneNumber"), "%" + phoneNumber + "%");
        };
    }

    public static Specification<PartnerApplication> createdAtGreaterThanOrEqual(LocalDateTime startDate) {
        return (root, query, criteriaBuilder) ->
                startDate == null ? criteriaBuilder.conjunction() :
                        criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate);
    }

    public static Specification<PartnerApplication> createdAtLessThanOrEqual(LocalDateTime endDate) {
        return (root, query, criteriaBuilder) ->
                endDate == null ? criteriaBuilder.conjunction() :
                        criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate);
    }

    public static Specification<PartnerApplication> joinBankApplications() {
        return (root, query, criteriaBuilder) -> {
            root.join("bankApplications", JoinType.INNER);
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<PartnerApplication> joinBank() {
        return (root, query, criteriaBuilder) -> {
            Join<PartnerApplication, BankApplication> bankApplicationJoin = root.join("bankApplications", JoinType.INNER);
            Join<BankApplication, CreditProgram> creditProgramJoin = bankApplicationJoin.join("creditProgram", JoinType.INNER);
            creditProgramJoin.join("bank", JoinType.INNER);
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<PartnerApplication> joinCreditProgram() {
        return (root, query, criteriaBuilder) -> {
            Join<PartnerApplication, BankApplication> bankApplicationJoin = root.join("bankApplications", JoinType.INNER);
            bankApplicationJoin.join("creditProgram", JoinType.INNER);
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<PartnerApplication> realEstateIdEquals(UUID realEstateId) {
        return ((root, query, criteriaBuilder) -> {
            Join<PartnerApplication, RealEstate> realEstateJoin = root.join("realEstate", JoinType.INNER);
            return criteriaBuilder.equal(realEstateJoin.get("id"), realEstateId);
        });
    }

    public static Specification<PartnerApplication> regionEquals(RegionType region) {
        return ((root, query, criteriaBuilder) -> {
            Join<PartnerApplication, RealEstate> realEstateJoin = root.join("realEstate", JoinType.INNER);
            return criteriaBuilder.equal(realEstateJoin.get("region"), region);
        });
    }

    public static Specification<PartnerApplication> bankIdEquals(UUID bankId) {
        return ((root, query, criteriaBuilder) -> {
            Join<PartnerApplication, BankApplication> bankApplicationJoin = root.join("bankApplications", JoinType.INNER);
            Join<BankApplication, CreditProgram> creditProgramJoin = bankApplicationJoin.join("creditProgram", JoinType.INNER);
            Join<CreditProgram, Bank> bankJoin = creditProgramJoin.join("bank", JoinType.INNER);
            return criteriaBuilder.equal(bankJoin.get("id"), bankId);
        });
    }

    public static Specification<PartnerApplication> applicationStatusEquals(BankApplicationStatus status) {
        return ((root, query, criteriaBuilder) -> {
            Join<PartnerApplication, BankApplication> bankApplicationJoin = root.join("bankApplications", JoinType.INNER);
            return criteriaBuilder.equal(bankApplicationJoin.get("bankApplicationStatus"), status);
        });
    }

    public static Specification<PartnerApplication> buildSearchSpecification(
            LocalDateTime startDate,
            LocalDateTime endDate,
            String phoneNumber,
            String fullName,
            Integer applicationNumber,
            UUID realEstateId,
            RegionType region,
            UUID bankId,
            BankApplicationStatus applicationStatus,
            Boolean isActive) {

        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            root.join("bankApplications", JoinType.INNER);
            query.distinct(true);

            if (startDate != null) {
                predicate = criteriaBuilder.and(predicate, createdAtGreaterThanOrEqual(startDate).toPredicate(root, query, criteriaBuilder));
            }
            if (endDate != null) {
                predicate = criteriaBuilder.and(predicate, createdAtLessThanOrEqual(endDate).toPredicate(root, query, criteriaBuilder));
            }
            if (isActive != null) {
                predicate = criteriaBuilder.and(predicate, isActive(isActive).toPredicate(root, query, criteriaBuilder));
            }
            if (phoneNumber != null) {
                predicate = criteriaBuilder.and(predicate, phoneNumberLike(phoneNumber).toPredicate(root, query, criteriaBuilder));
            }
            if (fullName != null) {
                predicate = criteriaBuilder.and(predicate, fullNameLike(fullName.toLowerCase(Locale.ROOT)).toPredicate(root, query, criteriaBuilder));
            }
            if (applicationNumber != null) {
                predicate = criteriaBuilder.and(predicate, applicationNumberEquals(applicationNumber).toPredicate(root, query, criteriaBuilder));
            }
            if (realEstateId != null) {
                predicate = criteriaBuilder.and(predicate, realEstateIdEquals(realEstateId).toPredicate(root, query, criteriaBuilder));
            }
            if (region != null) {
                predicate = criteriaBuilder.and(predicate, regionEquals(region).toPredicate(root, query, criteriaBuilder));
            }
            if (bankId != null) {
                predicate = criteriaBuilder.and(predicate, bankIdEquals(bankId).toPredicate(root, query, criteriaBuilder));
            }
            if (applicationStatus != null) {
                predicate = criteriaBuilder.and(predicate, applicationStatusEquals(applicationStatus).toPredicate(root, query, criteriaBuilder));
            }
            return predicate;
        };
    }

}

