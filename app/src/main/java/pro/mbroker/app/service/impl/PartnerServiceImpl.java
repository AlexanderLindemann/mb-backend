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
import pro.mbroker.app.entity.PartnerContact;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.PartnerContactMapper;
import pro.mbroker.app.mapper.PartnerMapper;
import pro.mbroker.app.mapper.RealEstateMapper;
import pro.mbroker.app.repository.CreditProgramRepository;
import pro.mbroker.app.repository.PartnerApplicationRepository;
import pro.mbroker.app.repository.PartnerContactRepository;
import pro.mbroker.app.repository.PartnerRepository;
import pro.mbroker.app.repository.specification.PartnerSpecification;
import pro.mbroker.app.service.CreditProgramService;
import pro.mbroker.app.service.PartnerService;
import pro.mbroker.app.util.Pagination;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {
    private final CreditProgramService creditProgramService;
    private final PartnerRepository partnerRepository;
    private final PartnerContactRepository partnerContactRepository;
    private final PartnerApplicationRepository partnerApplicationRepository;
    private final CreditProgramRepository creditProgramRepository;
    private final PartnerMapper partnerMapper;
    private final RealEstateMapper realEstateMapper;
    private final PartnerContactMapper partnerContactMapper;

    @Override
    @Transactional
    public Partner createPartner(PartnerRequest request, Integer sdId) {
        List<RealEstate> realEstates = realEstateMapper.toRealEstateAddressList(request.getRealEstateRequest());
        realEstates.forEach(realEstate -> {
            realEstate.setUpdatedBy(sdId);
            realEstate.setCreatedBy(sdId);
        });
        Partner partner = partnerMapper.toPartnerMapper(request)
                .setCreditPrograms(creditProgramService.getProgramByCreditProgramIds(request.getBankCreditProgram()))
                .setRealEstates(realEstates);
        if (Objects.nonNull(partner.getPartnerContacts())) {
            partner.getPartnerContacts().forEach(contact -> {
                contact.setPartner(partner);
                contact.setUpdatedBy(sdId);
                contact.setCreatedBy(sdId);
            });
        }
        realEstates.forEach(address -> address.setPartner(partner));
        partner.setCreatedBy(sdId);
        partner.setUpdatedBy(sdId);
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
    public Partner updatePartnerById(UUID partnerId, PartnerRequest request, Integer sdId) {
        Partner partner = getPartner(partnerId);
        partner.setUpdatedBy(sdId);
        partnerMapper.updatePartnerFromRequest(request, partner);
        modifyPartnerContacts(request, partner, sdId);
        modifyRealEstates(request.getRealEstateRequest(), partner, sdId);
        modifyCreditPrograms(request.getBankCreditProgram(), partner);
        return partnerRepository.save(partner);
    }

    private void modifyPartnerContacts(PartnerRequest request, Partner partner, Integer sdId) {
        partnerContactRepository.deleteAll(partner.getPartnerContacts());
        if (Objects.nonNull(request.getContacts())) {
            List<PartnerContact> partnerContacts = request.getContacts().stream()
                    .map(contact -> {
                        PartnerContact partnerContact = partnerContactMapper.toPartnerContact(contact);
                        partnerContact.setPartner(partner);
                        partnerContact.setUpdatedBy(sdId);
                        partnerContact.setCreatedBy(sdId);
                        return partnerContact;
                    }).collect(Collectors.toList());
            partner.setPartnerContacts(partnerContacts);
        } else {
            partner.setPartnerContacts(null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Partner> getCurrentPartners(Integer organisationId) {
        Specification<Partner> specification = PartnerSpecification.partnerByOrganizationIdAndIsActive(organisationId);
        return partnerRepository.findAll(specification);
    }

    @Override
    @Transactional
    public void deletePartner(UUID partnerId, Integer sdId) {
        Partner partner = getPartner(partnerId);
        partner.setActive(false);
        partner.setUpdatedBy(sdId);
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
        return partnerRepository.findOne(specification).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Partner getIsActivePartner(UUID partnerId) {
        Specification<Partner> specification = PartnerSpecification.partnerByIdAndIsActive(partnerId);
        return partnerRepository.findOne(specification)
                .orElseThrow(() -> new ItemNotFoundException(Partner.class, partnerId));
    }

    private void modifyRealEstates(List<RealEstateRequest> estateRequests, Partner partner, Integer sdId) {
        List<RealEstate> realEstates = partner.getRealEstates();
        Set<UUID> currentRealEstateIds = realEstates.stream().map(RealEstate::getId).collect(Collectors.toSet());
        estateRequests.forEach(request -> {
            if (Objects.nonNull(request.getId())) {
                RealEstate realEstate = realEstates.stream()
                        .filter(estate -> estate.getId().equals(request.getId()))
                        .findFirst()
                        .orElseThrow(() -> new ItemNotFoundException(RealEstateRequest.class, request.getId()));
                realEstateMapper.updateRealEstateAddress(request, realEstate);
                realEstate.setUpdatedBy(sdId);
                currentRealEstateIds.remove(request.getId());
            } else {
                RealEstate realEstate = realEstateMapper.toRealEstateMapper(request);
                realEstate.setCreatedBy(sdId);
                realEstate.setUpdatedBy(sdId);
                realEstate.setPartner(partner);
                realEstates.add(realEstate);
            }
        });
        realEstates.forEach(realEstate -> {
            if (currentRealEstateIds.contains(realEstate.getId())) {
                realEstate.setActive(false);
                realEstate.setUpdatedBy(sdId);
            }
        });
    }

    private void modifyCreditPrograms(Set<UUID> requestPrograms, Partner partner) {
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

    @Override
    public Partner saveOrUpdatePartner(Partner partner) {
        return partnerRepository.save(partner);
    }

    @Override
    public List<Partner> getPartnersBySmartDealOrganizationId(Integer smartDealOrganizationId) {
        return partnerRepository.findBySmartDealOrganizationId(smartDealOrganizationId);
    }
}
