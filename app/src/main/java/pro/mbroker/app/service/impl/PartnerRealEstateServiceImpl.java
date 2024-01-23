package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.api.dto.request.RealEstateRequest;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.PartnerType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.integration.cian.CianAPIClient;
import pro.mbroker.app.integration.cian.CiansRealEstate;
import pro.mbroker.app.integration.cian.response.BuilderDto;
import pro.mbroker.app.integration.cian.response.RealEstateCianResponse;
import pro.mbroker.app.mapper.RealEstateMapper;
import pro.mbroker.app.repository.PartnerRepository;
import pro.mbroker.app.repository.RealEstateRepository;
import pro.mbroker.app.repository.specification.RealEstateSpecification;
import pro.mbroker.app.service.CreditProgramService;
import pro.mbroker.app.service.PartnerRealEstateService;
import pro.mbroker.app.service.PartnerService;
import pro.mbroker.app.util.Pagination;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerRealEstateServiceImpl implements PartnerRealEstateService {
    private final PartnerService partnerService;
    private final RealEstateRepository realEstateRepository;
    private final RealEstateMapper realEstateMapper;
    private final PartnerRepository partnerRepository;
    private final CianAPIClient cianAPIClient;
    private final CreditProgramService creditProgramService;

    @Override
    @Transactional
    public RealEstate addRealEstate(UUID partnerId, RealEstateRequest request, Integer sdId) {
        RealEstate realEstate = realEstateMapper.toRealEstateMapper(request)
                .setPartner(partnerService.getPartner(partnerId));
        realEstate.setCreatedBy(sdId);
        realEstate.setUpdatedBy(sdId);
        RealEstate address = realEstateRepository.saveAndFlush(realEstate);
        return getRealEstate(address.getId());
    }

    @Override
    @Transactional
    public void deleteRealEstate(UUID realEstateId, Integer sdId) {
        RealEstate realEstate = getRealEstate(realEstateId);
        realEstate.setActive(false);
        realEstate.setUpdatedBy(sdId);
        realEstateRepository.save(realEstate);
    }

    @Override
    @Transactional
    public RealEstate updateRealEstate(UUID addressId, RealEstateRequest request, Integer sdId) {
        RealEstate realEstate = getRealEstate(addressId);
        realEstateMapper.updateRealEstateAddress(request, realEstate);
        realEstate.setUpdatedBy(sdId);
        return realEstateRepository.save(realEstate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RealEstate> getRealEstateByPartnerId(int page, int size, String sortBy, String sortOrder, UUID partnerId) {
        Pageable pageable = Pagination.createPageable(page, size, sortBy, sortOrder);
        Specification<RealEstate> specification = RealEstateSpecification.realEstateByPartnerIdAndIsActive(partnerId);
        Page<RealEstate> allByPartnerId = realEstateRepository.findAll(specification, pageable);
        return allByPartnerId.getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RealEstate> getCurrentRealEstate(int page, int size, String sortBy, String sortOrder, Integer organisationId) {
        Pageable pageable = Pagination.createPageable(page, size, sortBy, sortOrder);
        Partner partnerId = partnerRepository.findBySmartDealOrganizationId(organisationId)
                .orElseThrow(() -> new ItemNotFoundException(Partner.class, organisationId));
        Specification<RealEstate> specification = RealEstateSpecification.realEstateByPartnerIdAndIsActive(partnerId.getId());
        return realEstateRepository.findAll(specification, pageable).getContent();
    }

    @Transactional(readOnly = true)
    public RealEstate getRealEstate(UUID realEstateAddressId) {
        return realEstateRepository.findById(realEstateAddressId)
                .orElseThrow(() -> new ItemNotFoundException(RealEstate.class, realEstateAddressId));
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Override
    public void loadRealEstatesFromCian() {
        try {
            log.info("Запускаем выгрузку жк из циан ");
            RealEstateCianResponse response = cianAPIClient.getRealEstate();
            int totalBuildings = response.getNewBuildings().size();
            if (response != null) {
                log.info("Загружено из Циан ЖК в количестве: " + response.getNewBuildings().size() + ". Начинаем сохранение");
            }

            List<CiansRealEstate> successSavedBuildings = response.getNewBuildings()
                    .stream()
                    .peek(x -> {
                        try {
                            checkAndSavePartner(x);
                        } catch (Exception e) {
                            log.error("Не смогли загрузить Партнера name:{}, id:{}", x.getName(), x.getId(), e);
                        }
                    })
                    .collect(Collectors.toList());
            log.info("Из {} ЖК загруженных из Циан. Успешно сохранено {} ЖК ", totalBuildings, successSavedBuildings.size());

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Не смогли загрузить данные по ЖК из циан ");
        }
    }

    @Override
    public Page<RealEstate> findRealEstatesByName(Pageable pageable, String realEstateName) {
        Specification<RealEstate> specification = Specification
                .where(RealEstateSpecification.realEstateByNameLike(realEstateName))
                .and(RealEstateSpecification.isActive());
        return realEstateRepository.findAll(specification, pageable);
    }

    private void checkAndSavePartner(CiansRealEstate cianResponse) {
        if (Objects.nonNull(cianResponse.getBuilders())) {
            BuilderDto builder = cianResponse.getBuilders().get(0);//договорились с цианом что пока будем вытаскивать первого.

            var cianId = builder.getId();
            var name = builder.getName();
            if (cianId != null || name != null) {
                try {
                    Partner partner = partnerService.getPartnerByCianIdOrName(cianId, name);
                    if (partner != null) {
                        if (partner.getCianId() != null) {
                            partner.setCianId(cianId);
                            partner.setActive(true);
                            partnerService.saveOrUpdateParthner(partner);
                        }
                    } else {
                        PartnerRequest partnerRequest = buildPartnerRequest(cianResponse);
                        partner = partnerService.createPartner(partnerRequest, 6666);
                    }

                    checkAndSaveRealEstate(cianResponse, partner.getId());
                } catch (ItemNotFoundException e) {
                    log.error("Не смогли создать или обновить партнера: " + name);
                }
            }
        }
    }

    //TODO нужно утвердить какой нибудь сервисный cdId для того, чтобы получать ЖК без токена
    private void checkAndSaveRealEstate(CiansRealEstate response, UUID partnerId) {
        buildRealEstateRequest(response).forEach(cianRealEstate -> {
            RealEstate realEstate = getRealEstateByNameByPartnerId(partnerId,
                    cianRealEstate.getResidentialComplexName(),
                    cianRealEstate.getAddress());
            if (realEstate != null) {
                updateRealEstate(realEstate.getId(), cianRealEstate, 6333);
            } else {
                addRealEstate(partnerId, cianRealEstate, 6333);
            }
        });
    }

    private List<RealEstateRequest> buildRealEstateRequest(CiansRealEstate response) {
        RegionType regionType;
        RealEstateRequest request = new RealEstateRequest();
        request.setAddress(response.getFullAddress());
        request.setCianId(response.getId());
        if (response.getRegion().getName().contains("Ханты")) {
            regionType = RegionType.KHANTY_MANSIYSK;
        } else regionType = RegionType.getByName(response.getRegion().getName());
        request.setRegion(regionType);
        request.setResidentialComplexName(response.getName());

        return List.of(request);
    }

    private PartnerRequest buildPartnerRequest(CiansRealEstate response) {
        BuilderDto builder = response.getBuilders().get(0);//договорились с цианом брать 1го застройщика
        PartnerRequest partnerRequest = new PartnerRequest();
        partnerRequest.setName(builder.getName());
        partnerRequest.setCianId(builder.getId());

        partnerRequest.setRealEstateRequest(buildRealEstateRequest(response));
        partnerRequest.setRealEstateType(RealEstateType.getAll());
        partnerRequest.setType(PartnerType.DEVELOPER);
        partnerRequest.setBankCreditProgram(getAllCreditProgramIds());
        partnerRequest.setCreditPurposeType(CreditPurposeType.getAll());

        return partnerRequest;
    }

    private RealEstate getRealEstateByNameByPartnerId(UUID partnerId, String name, String address) {
        return realEstateRepository.findByResidentialComplexNameAndPartnerId(partnerId, name, address);
    }

    private List<UUID> getAllCreditProgramIds() {
        return creditProgramService.getAllCreditProgramIds();
    }
}
