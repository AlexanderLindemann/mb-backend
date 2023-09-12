package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.RegionType;

@Getter
@Setter
public class PartnerFilter {
    String firstName;
    String middleName;
    String lastName;
    String phoneNumber;
    String residentialComplexName;
    RegionType region;
    String bankName;
    BankApplicationStatus applicationStatus;
    String sortBy;
    String sortDirection;
}
