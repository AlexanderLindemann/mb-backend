package pro.mbroker.app.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.*;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

public class BankApplicationSpecification {

    public static Specification<BankApplication> firstnameLike(String firstName) {
        return (root, query, criteriaBuilder) -> {
            Join<BankApplication, BorrowerProfile> borrowerProfileJoin = root.join("mainBorrower");
            return firstName == null ? null : criteriaBuilder
                    .like(borrowerProfileJoin.get("firstName"), "%" + firstName + "%");
        };
    }

    public static Specification<BankApplication> middlenameLike(String middleName) {
        return (root, query, criteriaBuilder) -> {
            Join<BankApplication, BorrowerProfile> borrowerProfileJoin = root.join("mainBorrower");
            return middleName == null ? null : criteriaBuilder
                    .like(borrowerProfileJoin.get("middleName"), "%" + middleName + "%");
        };
    }

    public static Specification<BankApplication> lastnameLike(String lastName) {
        return (root, query, criteriaBuilder) -> {
            Join<BankApplication, BorrowerProfile> borrowerProfileJoin = root.join("mainBorrower");
            return lastName == null ? null : criteriaBuilder
                    .like(borrowerProfileJoin.get("lastName"), "%" + lastName + "%");
        };
    }

    public static Specification<BankApplication> phoneNumberLike(String phoneNumber) {
        return (root, query, criteriaBuilder) -> {
            Join<BankApplication, BorrowerProfile> borrowerProfileJoin = root.join("mainBorrower");
            return phoneNumber == null ? null : criteriaBuilder
                    .like(borrowerProfileJoin.get("phoneNumber"), "%" + phoneNumber + "%");
        };
    }

    public static Specification<BankApplication> bankNameLike(String bankName) {
        return (root, query, criteriaBuilder) -> {
            Join<BankApplication, CreditProgram> creditProgramJoin = root.join("creditProgram");
            Join<CreditProgram, Bank> bankJoin = creditProgramJoin.join("bank");
            return bankName == null ? null : criteriaBuilder.like(bankJoin.get("name"), "%" + bankName + "%");
        };
    }

    public static Specification<BankApplication> residentialComplexNameLike(String residentialComplexName) {
        return (root, query, criteriaBuilder) -> {
            Join<BankApplication, PartnerApplication> partnerApplicationJoin = root.join("partnerApplication");
            Join<PartnerApplication, RealEstate> realEstateJoin = partnerApplicationJoin.join("realEstate");
          return residentialComplexName == null ? null : criteriaBuilder
                  .like(realEstateJoin.get("residentialComplexName"), "%" + residentialComplexName + "%");
        };
    }

    public static Specification<BankApplication> regionTypeEqual(RegionType regionType) {
        return (root, query, criteriaBuilder) -> {
            Join<BankApplication, PartnerApplication> partnerApplicationJoin = root.join("partnerApplication");
            Join<PartnerApplication, RealEstate> realEstateJoin = partnerApplicationJoin.join("realEstate");
            return regionType == null ? null : criteriaBuilder
                    .equal(realEstateJoin.get("region"), regionType);
        };
    }

    public static Specification<BankApplication> applicationStatusEqual(BankApplicationStatus applicationStatus) {
        return (root, query, criteriaBuilder) -> applicationStatus == null ? null : criteriaBuilder
                .equal(root.get("bankApplicationStatus"), applicationStatus);
    }

    public static Specification<BankApplication> combineSearch(String firstName,
                                                               String middleName,
                                                               String lastName,
                                                               String phoneNumber,
                                                               String residentialComplexName,
                                                               RegionType region,
                                                               String bankName,
                                                               BankApplicationStatus applicationStatus) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (firstName != null) {
                predicate = criteriaBuilder.and(predicate,
                        firstnameLike(firstName).toPredicate(root, query, criteriaBuilder));
            }

            if (middleName != null) {
                predicate = criteriaBuilder.and(predicate,
                        middlenameLike(middleName).toPredicate(root, query, criteriaBuilder));
            }

            if (lastName != null) {
                predicate = criteriaBuilder.and(predicate,
                        lastnameLike(lastName).toPredicate(root, query, criteriaBuilder));
            }

            if (phoneNumber != null) {
                predicate = criteriaBuilder.and(predicate,
                        phoneNumberLike(phoneNumber).toPredicate(root, query, criteriaBuilder));
            }

            if (residentialComplexName != null) {
                predicate = criteriaBuilder.and(predicate,
                        residentialComplexNameLike(residentialComplexName).toPredicate(root, query, criteriaBuilder));
            }

            if (region != null) {
                predicate = criteriaBuilder.and(predicate,
                        regionTypeEqual(region).toPredicate(root, query, criteriaBuilder));
            }

            if (bankName != null) {
                predicate = criteriaBuilder.and(predicate,
                        bankNameLike(bankName).toPredicate(root, query, criteriaBuilder));
            }

            if (applicationStatus != null) {
                predicate = criteriaBuilder.and(predicate,
                        applicationStatusEqual(applicationStatus).toPredicate(root, query, criteriaBuilder));
            }

            return predicate;
        };
    }

}
