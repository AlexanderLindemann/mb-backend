package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.api.dto.request.RealEstateRequest;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.PartnerMapper;
import pro.mbroker.app.mapper.RealEstateMapper;
import pro.mbroker.app.repository.CreditProgramRepository;
import pro.mbroker.app.repository.PartnerApplicationRepository;
import pro.mbroker.app.repository.PartnerRepository;
import pro.mbroker.app.repository.specification.PartnerSpecification;
import pro.mbroker.app.service.CreditProgramService;
import pro.mbroker.app.service.PartnerService;
import pro.mbroker.app.util.Pagination;
import pro.mbroker.app.util.TokenExtractor;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {
    private final CreditProgramService creditProgramService;
    private final CurrentUserService currentUserService;
    private final PartnerRepository partnerRepository;
    private final PartnerApplicationRepository partnerApplicationRepository;
    private final CreditProgramRepository creditProgramRepository;
    private final PartnerMapper partnerMapper;
    private final RealEstateMapper realEstateMapper;

    @Override
    @Transactional
    public Partner createPartner(PartnerRequest request) {
        List<RealEstate> realEstates = realEstateMapper.toRealEstateAddressList(request.getRealEstateRequest());
        Partner partner = partnerMapper.toPartnerMapper(request)
                .setCreditPrograms(creditProgramService.getProgramByCreditProgramIds(request.getBankCreditProgram()))
                .setRealEstates(realEstates);
        realEstates.forEach(address -> address.setPartner(partner));
        return partnerRepository.save(partner);
    }

    @Override
    @Transactional(readOnly = true)
    public Partner getPartner(UUID partnerId) {
        return partnerRepository.findById(partnerId)
                .orElseThrow(() -> new ItemNotFoundException(Partner.class, partnerId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Partner> getAllPartner(int page, int size, String sortBy, String sortOrder) {
        Specification<Partner> specification = PartnerSpecification.isActive();
        Pageable pageable = Pagination.createPageable(page, size, sortBy, sortOrder);
        return partnerRepository.findAll(specification, pageable).getContent();
    }

    @Override
    @Transactional
    public Partner updatePartnerById(UUID partnerId, PartnerRequest request) {
        Partner partner = getPartner(partnerId);
        partnerMapper.updatePartnerFromRequest(request, partner);
        modifyRealEstates(request.getRealEstateRequest(), partner);
        modifyCreditPrograms(request.getBankCreditProgram(), partner);
        return partnerRepository.save(partner);
    }

    @Override
    @Transactional(readOnly = true)
    public Partner getCurrentPartner() {
        int organizationId = TokenExtractor
                .extractSdCurrentOrganizationId(currentUserService.getCurrentUserToken());
        Specification<Partner> specification = PartnerSpecification.partnerByOrganizationIdAndIsActive(organizationId);
        return partnerRepository.findOne(specification)
                .orElseThrow(() -> new ItemNotFoundException(Partner.class, "organization_Id:" + organizationId));
    }

    @Override
    @Transactional
    public void deletePartner(UUID partnerId) {
        Partner partner = getPartner(partnerId);
        partner.setActive(false);
        partnerRepository.save(partner);
    }

    @Override
    @Transactional(readOnly = true)
    public Partner getPartnerByPartnerApplicationId(UUID partnerApplicationId) {
        PartnerApplication partnerApplication = partnerApplicationRepository.findById(partnerApplicationId)
                .orElseThrow(() -> new ItemNotFoundException(PartnerApplication.class, partnerApplicationId));
        return partnerApplication.getPartner();
    }

    @Override
    public Partner getPartnerByCianIdOrName(Integer cianId, String name) {
        Specification<Partner> specification = PartnerSpecification.partnerByCianIdOrName(cianId, name);
        return partnerRepository.findOne(specification)
                .orElseThrow(() -> new ItemNotFoundException(Partner.class, List.of(cianId, name)));
    }

    @Override
    @Transactional(readOnly = true)
    public Partner getIsActivePartner(UUID partnerId) {
        Specification<Partner> specification = PartnerSpecification.partnerByIdAndIsActive(partnerId);
        return partnerRepository.findOne(specification)
                .orElseThrow(() -> new ItemNotFoundException(Partner.class, partnerId));
    }

    private void modifyRealEstates(List<RealEstateRequest> estateRequests, Partner partner) {
        List<RealEstate> realEstates = partner.getRealEstates();
        Set<UUID> currentRealEstateIds = realEstates.stream().map(RealEstate::getId).collect(Collectors.toSet());
        estateRequests.forEach(request -> {
            if (Objects.nonNull(request.getId())) {
                RealEstate realEstate = realEstates.stream()
                        .filter(estate -> estate.getId().equals(request.getId()))
                        .findFirst()
                        .orElseThrow(() -> new ItemNotFoundException(RealEstateRequest.class, request.getId()));
                realEstateMapper.updateRealEstateAddress(request, realEstate);
                currentRealEstateIds.remove(request.getId());
            } else {
                RealEstate realEstate = realEstateMapper.toRealEstateMapper(request);
                realEstate.setPartner(partner);
                realEstates.add(realEstate);
            }
        });
        realEstates.forEach(realEstate -> {
            if (currentRealEstateIds.contains(realEstate.getId())) {
                realEstate.setActive(false);
            }
        });
    }

    private void modifyCreditPrograms(List<UUID> requestPrograms, Partner partner) {
        Set<UUID> currentProgramIds = partner.getCreditPrograms().stream()
                .map(CreditProgram::getId)
                .collect(Collectors.toSet());
        List<CreditProgram> programsToAdd = new ArrayList<>();
        for (UUID programId : requestPrograms) {
            if (currentProgramIds.contains(programId)) {
                currentProgramIds.remove(programId);
            } else {
                CreditProgram program = creditProgramRepository.findById(programId)
                        .orElseThrow(() -> new ItemNotFoundException(CreditProgram.class, programId));
                programsToAdd.add(program);
            }
        }

        partner.getCreditPrograms().removeIf(program -> currentProgramIds.contains(program.getId()));
        partner.getCreditPrograms().addAll(programsToAdd);
    }

    public Optional<Partner> findPartnerByCianId(Integer cianId) {
        return partnerRepository.findByCianId(cianId);
    }

    @Override
    public Partner updateCianId(UUID partnerId, Integer cianId) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Партнер с указанным ID не найден"));
        partner.setCianId(cianId);
        return partnerRepository.save(partner);
    }
}
