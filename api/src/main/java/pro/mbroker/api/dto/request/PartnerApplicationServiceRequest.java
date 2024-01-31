package pro.mbroker.api.dto.request;

import lombok.Data;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.RegionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class PartnerApplicationServiceRequest {
    private int page;
    private int size;
    private String sortBy;
    private String sortOrder;
    private String fullName;
    private String phoneNumber;
    private String tokenPhoneNumber;
    private Integer applicationNumber;
    private UUID realEstateId;
    private RegionType region;
    private UUID bankId;
    private BankApplicationStatus applicationStatus;
    private Boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer sdId;
    private Integer organisationId;
    List<String> permissions;
}

