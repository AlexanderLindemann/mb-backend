package pro.mbroker.app.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.request.PartnerApplicationServiceRequest;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.Insurance;
import pro.mbroker.api.enums.PartnerApplicationStatus;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.TestData;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BaseEntity;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.util.Converter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class PartnerApplicationServiceTest extends BaseServiceTest {
    @Autowired
    private PartnerApplicationService partnerApplicationService;
    @Autowired
    private TestData testData;

    @Test
    public void testFilterApplicationStatusPartnerApplicationWithAdminPermit1() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setApplicationStatus(BankApplicationStatus.DATA_NO_ENTERED)
                .setOrganisationId(2633));
        Set<UUID> expectedIds = Set.of(
                UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"),
                UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa"));
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertTrue(actualIds.containsAll(expectedIds), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .flatMap(pa -> pa.getBankApplications()
                        .stream().map(BankApplication::getBankApplicationStatus))
                .anyMatch(status -> status.equals(BankApplicationStatus.DATA_NO_ENTERED)));
    }

    @Test
    public void testCombineFilterPartnerApplicationWithAdminPermit() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setIsActive(true)
                .setPhoneNumber("218")
                .setFullName("Ivan")
                .setApplicationNumber(10003)
                .setRealEstateId(UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002"))
                .setBankId(UUID.fromString("0c371042-d848-11ed-afa1-0242ac120002"))
                .setRegion(RegionType.VORONEZH)
                .setApplicationStatus(BankApplicationStatus.DATA_NO_ENTERED)
                .setOrganisationId(2633));
        UUID expectedId = UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa");
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertTrue(actualIds.contains(expectedId), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .flatMap(pa -> pa.getBankApplications()
                        .stream().map(BankApplication::getBankApplicationStatus))
                .anyMatch(status -> status.equals(BankApplicationStatus.DATA_NO_ENTERED)));
        assertTrue(partnerApplications.getContent().stream()
                .allMatch(BaseEntity::isActive));
        assertTrue(partnerApplications.getContent().stream()
                .map(PartnerApplication::getBorrowerProfiles)
                .anyMatch(ba -> ba.stream().anyMatch(borrowerProfile -> borrowerProfile.getPhoneNumber().contains("218"))));
        assertTrue(partnerApplications.getContent().stream()
                .flatMap(pa -> pa.getBorrowerProfiles().stream())
                .anyMatch(bp -> {
                    String fullName = bp.getFirstName() + " " +
                            (bp.getMiddleName() != null ? bp.getMiddleName() + " " : "") +
                            bp.getLastName();
                    return fullName.contains("Ivan");
                }), "At least one BorrowerProfile should contain the full name 'Ivan'");
        assertTrue(partnerApplications.getContent().stream()
                .flatMap(pa -> pa.getBankApplications().stream())
                .anyMatch(bp -> {
                    Integer applicationNumber = bp.getApplicationNumber();
                    return applicationNumber == 10003;
                }), "At least one bankApplication should contain the applicationNumber 10001");
        assertTrue(partnerApplications.getContent().stream()
                .map(PartnerApplication::getRealEstate)
                .allMatch(realEstate -> {
                    UUID realEstateId = realEstate.getId();
                    return realEstateId.equals(UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002"));
                }));
        assertTrue(partnerApplications.getContent().stream()
                .flatMap(pa -> pa.getBankApplications().stream()
                        .map(BankApplication::getCreditProgram)
                        .map(CreditProgram::getBank)
                        .map(Bank::getId)
                ).anyMatch(id -> id.equals(UUID.fromString("0c371042-d848-11ed-afa1-0242ac120002"))));
        assertTrue(partnerApplications.getContent().stream()
                .map(PartnerApplication::getRealEstate)
                .allMatch(realEstate -> {
                    RegionType region = realEstate.getRegion();
                    return region.equals(RegionType.VORONEZH);
                }));
        assertTrue(partnerApplications.getContent().stream()
                .flatMap(pa -> pa.getBankApplications()
                        .stream().map(BankApplication::getBankApplicationStatus))
                .anyMatch(status -> status.equals(BankApplicationStatus.DATA_NO_ENTERED)));
    }

    @Test
    public void testFilterApplicationStatusPartnerApplicationWithAdminPermit2() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setApplicationStatus(BankApplicationStatus.READY_TO_SENDING)
                .setOrganisationId(2633));
        UUID expectedId = UUID.fromString("d573aeb5-ea8d-48e7-989c-82ce17e16ac2");
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertTrue(actualIds.contains(expectedId), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .flatMap(pa -> pa.getBankApplications()
                        .stream().map(BankApplication::getBankApplicationStatus))
                .anyMatch(status -> status.equals(BankApplicationStatus.READY_TO_SENDING)));
    }

    @Test
    public void testFilterRegionPartnerApplicationWithAdminPermit1() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setRegion(RegionType.VORONEZH)
                .setOrganisationId(2633));
        Set<UUID> expectedIds = Set.of(
                UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"),
                UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa"));
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertTrue(actualIds.containsAll(expectedIds), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .map(PartnerApplication::getRealEstate)
                .allMatch(realEstate -> {
                    RegionType region = realEstate.getRegion();
                    return region.equals(RegionType.VORONEZH);
                }));
    }

    @Test
    public void testFilterRegionPartnerApplicationWithAdminPermit2() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setRegion(RegionType.CHUKOTKA)
                .setOrganisationId(2633));
        UUID expectedId = UUID.fromString("f09e9c57-e110-4f55-9124-9fafba8a441c");
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertTrue(actualIds.contains(expectedId), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .map(PartnerApplication::getRealEstate)
                .allMatch(realEstate -> {
                    RegionType region = realEstate.getRegion();
                    return region.equals(RegionType.CHUKOTKA);
                }));
    }

    @Test
    public void testFilterRegionPartnerApplicationWithAdminPermit3() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setRegion(RegionType.MOSCOW)
                .setOrganisationId(2633));
        UUID expectedId = UUID.fromString("d573aeb5-ea8d-48e7-989c-82ce17e16ac2");
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertTrue(actualIds.contains(expectedId), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .map(PartnerApplication::getRealEstate)
                .allMatch(realEstate -> {
                    RegionType region = realEstate.getRegion();
                    return region.equals(RegionType.MOSCOW);
                }));
    }

    @Test
    public void testFilterBankIdPartnerApplicationWithAdminPermit() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setBankId(UUID.fromString("0c371042-d848-11ed-afa1-0242ac120002"))
                .setOrganisationId(2633));
        Set<UUID> expectedIds = Set.of(
                UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"),
                UUID.fromString("f09e9c57-e110-4f55-9124-9fafba8a441c"),
                UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa"));
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertTrue(actualIds.containsAll(expectedIds), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .flatMap(pa -> pa.getBankApplications().stream()
                        .map(BankApplication::getCreditProgram)
                        .map(CreditProgram::getBank)
                        .map(Bank::getId)
                ).anyMatch(id -> id.equals(UUID.fromString("0c371042-d848-11ed-afa1-0242ac120002"))));
    }

    @Test
    public void testFilterRealEstateIdPartnerApplicationWithAdminPermit1() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setRealEstateId(UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002"))
                .setOrganisationId(2633));
        Set<UUID> expectedIds = Set.of(
                UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"),
                UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa"));
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertTrue(actualIds.containsAll(expectedIds), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .map(PartnerApplication::getRealEstate)
                .allMatch(realEstate -> {
                    UUID realEstateId = realEstate.getId();
                    return realEstateId.equals(UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002"));
                }));
    }

    @Test
    public void testFilterRealEstateIdPartnerApplicationWithAdminPermit2() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setRealEstateId(UUID.fromString("51faca54-d930-11ed-afa1-0242ac120002"))
                .setOrganisationId(2633));
        UUID expectedId = UUID.fromString("f09e9c57-e110-4f55-9124-9fafba8a441c");
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(1, actualIds.size());
        assertTrue(actualIds.contains(expectedId), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .map(PartnerApplication::getRealEstate)
                .allMatch(realEstate -> {
                    UUID realEstateId = realEstate.getId();
                    return realEstateId.equals(UUID.fromString("51faca54-d930-11ed-afa1-0242ac120002"));
                }));
    }

    @Test
    public void testFilterRealEstateIdPartnerApplicationWithAdminPermit3() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setRealEstateId(UUID.fromString("bd80be0e-f2db-11ed-a05b-0242ac120003"))
                .setOrganisationId(2633));
        UUID expectedId = UUID.fromString("d573aeb5-ea8d-48e7-989c-82ce17e16ac2");
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(1, actualIds.size());
        assertTrue(actualIds.contains(expectedId), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .map(PartnerApplication::getRealEstate)
                .allMatch(realEstate -> {
                    UUID realEstateId = realEstate.getId();
                    return realEstateId.equals(UUID.fromString("bd80be0e-f2db-11ed-a05b-0242ac120003"));
                }));
    }

    @Test
    public void testFilterApplicationNumberPartnerApplicationWithAdminPermit1() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setApplicationNumber(10001)
                .setOrganisationId(2633));
        UUID expectedId = UUID.fromString("d573aeb5-ea8d-48e7-989c-82ce17e16ac2");
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(1, actualIds.size());
        assertTrue(actualIds.contains(expectedId), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .flatMap(pa -> pa.getBankApplications().stream())
                .anyMatch(bp -> {
                    Integer applicationNumber = bp.getApplicationNumber();
                    return applicationNumber == 10001;
                }), "At least one bankApplication should contain the applicationNumber 10001");
    }

    @Test
    public void testFilterApplicationNumberPartnerApplicationWithAdminPermit2() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setApplicationNumber(10002)
                .setOrganisationId(2633));
        UUID expectedId = UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34");
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(1, actualIds.size());
        assertTrue(actualIds.contains(expectedId), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .flatMap(pa -> pa.getBankApplications().stream())
                .anyMatch(bp -> {
                    Integer applicationNumber = bp.getApplicationNumber();
                    return applicationNumber == 10002;
                }), "At least one bankApplication should contain the applicationNumber 10002");
    }

    @Test
    public void testFilterApplicationNumberPartnerApplicationWithAdminPermit3() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setApplicationNumber(0)
                .setOrganisationId(2633));
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(0, actualIds.size());
    }

    @Test
    public void testFilterFullNamePartnerApplicationWithAdminPermit() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setFullName("Ivan")
                .setOrganisationId(2633));
        Set<UUID> expectedIds = Set.of(
                UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"),
                UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa"));
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(2, actualIds.size());
        assertTrue(actualIds.containsAll(expectedIds), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .flatMap(pa -> pa.getBorrowerProfiles().stream())
                .anyMatch(bp -> {
                    String fullName = bp.getFirstName() + " " +
                            (bp.getMiddleName() != null ? bp.getMiddleName() + " " : "") +
                            bp.getLastName();
                    return fullName.contains("Ivan");
                }), "At least one BorrowerProfile should contain the full name 'Ivan'");
    }

    @Test
    public void testFilterFullNamePartnerApplicationWithAdminPermit2() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setFullName("Ivanov Perviy")
                .setOrganisationId(2633));
        UUID expectedId = UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34");
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(1, actualIds.size());
        assertTrue(actualIds.contains(expectedId), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .flatMap(pa -> pa.getBorrowerProfiles().stream())
                .anyMatch(bp -> {
                    String fullName = bp.getFirstName() + " " +
                            (bp.getMiddleName() != null ? bp.getMiddleName() + " " : "") +
                            bp.getLastName();
                    return fullName.contains("Ivanov Perviy");
                }), "At least one BorrowerProfile should contain the full name 'Ivanov Perviy'");
    }

    @Test
    public void testFilterFullNamePartnerApplicationWithAdminPermit3() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setFullName("     Ivan    PeTROV")
                .setOrganisationId(2633));
        UUID expectedId = UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa");
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(1, actualIds.size());
        assertTrue(actualIds.contains(expectedId), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .flatMap(pa -> pa.getBorrowerProfiles().stream())
                .anyMatch(bp -> {
                    String fullName = bp.getFirstName() + " " +
                            (bp.getMiddleName() != null ? bp.getMiddleName() + " " : "") +
                            bp.getLastName();
                    return fullName.contains("Ivan Petrov");
                }), "At least one BorrowerProfile should contain the full name 'Ivan Petrov'");
    }

    @Test
    public void testFilterFullNamePartnerApplicationWithAdminPermit4() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setFullName("Ivan Petrov Petrovich     ")
                .setOrganisationId(2633));
        UUID expectedId = UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa");
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(1, actualIds.size());
        assertTrue(actualIds.contains(expectedId), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .flatMap(pa -> pa.getBorrowerProfiles().stream())
                .anyMatch(bp -> {
                    String fullName = bp.getFirstName() + " " +
                            (bp.getMiddleName() != null ? bp.getMiddleName() + " " : "") +
                            bp.getLastName();
                    return fullName.contains("Ivan Petrov");
                }), "At least one BorrowerProfile should contain the full name 'Ivan Petrov'");
    }

    @Test
    public void getPartnerApplicationWithAdminPermit() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setOrganisationId(2633));
        Set<UUID> expectedIds = Set.of(
                UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"),
                UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa"),
                UUID.fromString("f09e9c57-e110-4f55-9124-9fafba8a441c"),
                UUID.fromString("d573aeb5-ea8d-48e7-989c-82ce17e16ac2"));
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(4, actualIds.size());
        assertTrue(actualIds.containsAll(expectedIds), "Actual IDs should contain all the expected IDs");
    }

    @Test
    public void getPartnerApplicationWithReadOrganizationPermissions() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_REQUEST_READ_ORGANIZATION"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setOrganisationId(2633));
        Set<UUID> expectedIds = Set.of(
                UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"),
                UUID.fromString("d573aeb5-ea8d-48e7-989c-82ce17e16ac2"));
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(2, actualIds.size());
        assertTrue(actualIds.containsAll(expectedIds), "Actual IDs should contain all the expected IDs");
    }

    @Test
    public void getPartnerApplicationWithReadOwnPermissions() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_REQUEST_READ_OWN"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setOrganisationId(2633));
        Set<UUID> expectedIds = Set.of(
                UUID.fromString("f09e9c57-e110-4f55-9124-9fafba8a441c"),
                UUID.fromString("d573aeb5-ea8d-48e7-989c-82ce17e16ac2"));
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(2, actualIds.size());
        assertTrue(actualIds.containsAll(expectedIds), "Actual IDs should contain all the expected IDs");
    }

    @Test
    public void getPartnerApplicationWithCabinetPermissions() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("SD_MOBILE_INTERACTION"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setTokenPhoneNumber("9999999999"));
        UUID expectedIds = UUID.fromString("f09e9c57-e110-4f55-9124-9fafba8a441c");
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(1, actualIds.size());
        assertTrue(actualIds.contains(expectedIds), "Actual IDs should contain all the expected IDs");
    }

    @Test
    public void getPartnerApplicationWithAdminPermitAndIsActive() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setIsActive(true)
                .setOrganisationId(2633));
        Set<UUID> expectedIds = Set.of(
                UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"),
                UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa"),
                UUID.fromString("d573aeb5-ea8d-48e7-989c-82ce17e16ac2"));
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(3, actualIds.size());
        assertTrue(actualIds.containsAll(expectedIds), "Actual IDs should contain all the expected IDs");
    }

    @Test
    public void testFilterPhoneNumberPartnerApplicationWithAdminPermit1() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setPhoneNumber("432")
                .setOrganisationId(2633));
        UUID expectedId = UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34");
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(1, actualIds.size());
        assertTrue(actualIds.contains(expectedId), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .map(PartnerApplication::getBorrowerProfiles)
                .anyMatch(ba -> ba.stream().anyMatch(borrowerProfile -> borrowerProfile.getPhoneNumber().contains("432"))));
    }

    @Test
    public void testFilterPhoneNumberPartnerApplicationWithAdminPermit2() {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(new PartnerApplicationServiceRequest()
                .setPermissions(List.of("MB_ADMIN_ACCESS"))
                .setSortOrder("asc")
                .setSortBy("updatedAt")
                .setSize(10)
                .setPage(0)
                .setSdId(2962)
                .setPhoneNumber("218")
                .setOrganisationId(2633));
        Set<UUID> expectedIds = Set.of(UUID.fromString("d573aeb5-ea8d-48e7-989c-82ce17e16ac2"),
                UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa"));
        Set<UUID> actualIds = partnerApplications.getContent().stream()
                .map(PartnerApplication::getId)
                .collect(Collectors.toSet());
        assertEquals(2, actualIds.size());
        assertTrue(actualIds.containsAll(expectedIds), "Actual IDs should contain all the expected IDs");
        assertTrue(partnerApplications.getContent().stream()
                .map(PartnerApplication::getBorrowerProfiles)
                .anyMatch(ba -> ba.stream().anyMatch(borrowerProfile -> borrowerProfile.getPhoneNumber().contains("218"))));
    }

    @Test
    public void testCreatePartnerApplication() {
        PartnerApplication partnerApplication = partnerApplicationService.createPartnerApplication(testData.getPartnerApplicationRequest(), 1234);
        assertEquals(partnerApplication.getPartner().getId(), UUID.fromString("5fec2326-d92e-11ed-afa1-0242ac120002"));
        assertEquals(partnerApplication.getCreditPurposeType(), CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION);
        assertEquals(partnerApplication.getPartnerApplicationStatus(), PartnerApplicationStatus.UPLOADING_DOCS);
        assertEquals(Converter.convertStringListToEnumList(partnerApplication.getRealEstateTypes(), RealEstateType.class), List.of(RealEstateType.APARTMENT, RealEstateType.ROOM));
        assertEquals(partnerApplication.getRealEstate().getId(), UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002"));
        assertThat(partnerApplication.getBankApplications().size(), is(2));
        assertThat(partnerApplication.getBorrowerProfiles().size(), is(1));
    }

    @Test
    public void testUpdatePartnerApplication() {
        PartnerApplication partnerApplicationBefore =
                partnerApplicationService.getPartnerApplication(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"));
        LocalDateTime updateAtBefore = partnerApplicationBefore.getUpdatedAt();
        PartnerApplicationRequest partnerApplicationNew = testData.getPartnerApplicationRequest()
                .setInsurances(List.of(Insurance.LIFE_INSURANCE, Insurance.TITLE_INSURANCE));
        partnerApplicationNew.getMainBorrower()
                .setId(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003"));
        PartnerApplication partnerApplication =
                partnerApplicationService.updatePartnerApplication(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"), partnerApplicationNew, 1234);
        assertEquals(partnerApplication.getPartner().getId(), UUID.fromString("5fec2326-d92e-11ed-afa1-0242ac120002"));
        assertEquals(partnerApplication.getCreditPurposeType(), CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION);
        assertEquals(partnerApplication.getPartnerApplicationStatus(), PartnerApplicationStatus.UPLOADING_DOCS);
        assertEquals(Converter.convertStringListToEnumList(partnerApplication.getRealEstateTypes(), RealEstateType.class), List.of(RealEstateType.APARTMENT, RealEstateType.ROOM));
        assertEquals(partnerApplication.getRealEstate().getId(), UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002"));
        assertThat(partnerApplication.getBankApplications().size(), is(2));
        assertThat(partnerApplication.getBorrowerProfiles().size(), is(1));
        assertTrue(partnerApplication.getUpdatedAt().isAfter(updateAtBefore));
    }

    @Test
    public void testShortUpdatePartnerApplication() {
        PartnerApplication partnerApplicationBefore =
                partnerApplicationService.getPartnerApplication(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"));
        LocalDateTime updateAtBefore = partnerApplicationBefore.getUpdatedAt();
        PartnerApplication partnerApplicationAfterUpdate =
                partnerApplicationService.updatePartnerApplication(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"),
                        testData.getShortPartnerApplicationRequest(), 1234);
        assertNotEquals(partnerApplicationAfterUpdate.getUpdatedAt(), updateAtBefore);

        assertEquals(partnerApplicationAfterUpdate.getInsurances(), "LIFE_INSURANCE,TITLE_INSURANCE");
        assertEquals(partnerApplicationAfterUpdate.getPaymentSource(), "APARTMENT_SALE,MILITARY_MORTGAGE");
        assertEquals(partnerApplicationAfterUpdate.getMaternalCapitalAmount(), BigDecimal.valueOf(120000));
        assertEquals(partnerApplicationAfterUpdate.getSubsidyAmount(), BigDecimal.valueOf(30000));

        assertEquals(partnerApplicationBefore.getCreditPurposeType(), partnerApplicationAfterUpdate.getCreditPurposeType());
        assertEquals(partnerApplicationBefore.getPartnerApplicationStatus(), partnerApplicationAfterUpdate.getPartnerApplicationStatus());
        assertEquals(partnerApplicationBefore.getRealEstateTypes(), partnerApplicationAfterUpdate.getRealEstateTypes());
        assertEquals(partnerApplicationBefore.getRealEstate(), partnerApplicationAfterUpdate.getRealEstate());
        assertEquals(partnerApplicationBefore.getBankApplications(), partnerApplicationAfterUpdate.getBankApplications());
        assertEquals(partnerApplicationBefore.getBorrowerProfiles(), partnerApplicationAfterUpdate.getBorrowerProfiles());
        assertEquals(partnerApplicationBefore.getMortgageCalculation(), partnerApplicationAfterUpdate.getMortgageCalculation());
        assertEquals(partnerApplicationBefore.getCreatedAt(), partnerApplicationAfterUpdate.getCreatedAt());
        assertEquals(partnerApplicationBefore.getCreatedBy(), partnerApplicationAfterUpdate.getCreatedBy());
        assertEquals(partnerApplicationBefore.isActive(), partnerApplicationAfterUpdate.isActive());

        PartnerApplication partnerApplicationAfterUpdateWithNullValues =
                partnerApplicationService.updatePartnerApplication(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"),
                        testData.getShortPartnerApplicationRequestWithNullValues(), 1234);

        assertNull(partnerApplicationAfterUpdateWithNullValues.getInsurances());
        assertNull(partnerApplicationAfterUpdateWithNullValues.getPaymentSource());
        assertEquals(partnerApplicationAfterUpdateWithNullValues.getMaternalCapitalAmount(), BigDecimal.valueOf(0));
        assertEquals(partnerApplicationAfterUpdateWithNullValues.getSubsidyAmount(), BigDecimal.valueOf(0));
        assertEquals(partnerApplicationBefore.getCreditPurposeType(), partnerApplicationAfterUpdateWithNullValues.getCreditPurposeType());
        assertEquals(partnerApplicationBefore.getPartnerApplicationStatus(), partnerApplicationAfterUpdateWithNullValues.getPartnerApplicationStatus());
        assertEquals(partnerApplicationBefore.getRealEstateTypes(), partnerApplicationAfterUpdateWithNullValues.getRealEstateTypes());
        assertEquals(partnerApplicationBefore.getRealEstate(), partnerApplicationAfterUpdateWithNullValues.getRealEstate());
        assertEquals(partnerApplicationBefore.getBankApplications(), partnerApplicationAfterUpdateWithNullValues.getBankApplications());
        assertEquals(partnerApplicationBefore.getBorrowerProfiles(), partnerApplicationAfterUpdateWithNullValues.getBorrowerProfiles());
        assertEquals(partnerApplicationBefore.getMortgageCalculation(), partnerApplicationAfterUpdateWithNullValues.getMortgageCalculation());
        assertEquals(partnerApplicationBefore.getCreatedAt(), partnerApplicationAfterUpdateWithNullValues.getCreatedAt());
        assertEquals(partnerApplicationBefore.getCreatedBy(), partnerApplicationAfterUpdateWithNullValues.getCreatedBy());
        assertEquals(partnerApplicationBefore.isActive(), partnerApplicationAfterUpdateWithNullValues.isActive());
    }

    @Test
    public void testGetPartnerApplication() {
        LocalDateTime now = LocalDateTime.now();
        PartnerApplication partnerApplication =
                partnerApplicationService.getPartnerApplication(UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa"));
        assertTrue(partnerApplication.getUpdatedAt().isBefore(now));
        assertEquals(partnerApplication.getPartner().getId(), UUID.fromString("dce73f3e-f2db-11ed-a05b-0242ac120003"));
        assertEquals(partnerApplication.getCreditPurposeType(), CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION);
        assertEquals(Converter.convertStringListToEnumList(partnerApplication.getRealEstateTypes(), RealEstateType.class), List.of(RealEstateType.APARTMENT));
        assertEquals(partnerApplication.getRealEstate().getId(), UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002"));
        assertThat(partnerApplication.getBankApplications().size(), is(1));
        assertThat(partnerApplication.getBorrowerProfiles().size(), is(1));
    }

    @Test
    public void testChangeBankApplication() {
        PartnerApplication partnerApplication =
                partnerApplicationService.disableBankApplication(UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa"), UUID.fromString("8222cb80-d928-11ed-afa1-0242ac120002"), 1234);
        assertThat(partnerApplication.getBankApplications().get(0).isActive(), is(false));
        partnerApplicationService.enableBankApplication(UUID.fromString("7addcbef-c1e0-4de1-adeb-377f864efcfa"), testData.getBankApplicationUpdateRequest(), 1234);
        assertThat(partnerApplication.getBankApplications().get(0).isActive(), is(true));
    }
}
