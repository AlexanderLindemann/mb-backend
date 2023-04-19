package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.RealEstateRequest;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.RealEstateMapper;
import pro.mbroker.app.repository.PartnerRepository;
import pro.mbroker.app.repository.RealEstateRepository;
import pro.mbroker.app.service.PartnerRealEstateService;
import pro.mbroker.app.service.PartnerService;
import pro.mbroker.app.util.Pagination;
import pro.mbroker.app.util.TokenExtractor;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerRealEstateServiceImpl implements PartnerRealEstateService {
    private final PartnerService partnerService;
    private final RealEstateRepository realEstateRepository;
    private final RealEstateMapper realEstateMapper;
    private final CurrentUserService currentUserService;
    private final PartnerRepository partnerRepository;

    @Override
    @Transactional
    public RealEstate addRealEstate(UUID partnerId, RealEstateRequest request) {
        RealEstate realEstate = realEstateMapper.toRealEstateMapper(request)
                .setPartner(partnerService.getPartner(partnerId));
        RealEstate address = realEstateRepository.save(realEstate);
        realEstateRepository.flush();
        return getRealEstate(address.getId());
    }

    @Override
    @Transactional
    public void deleteRealEstate(UUID addressId) {
        RealEstate realEstate = getRealEstate(addressId);
        realEstateRepository.delete(realEstate);
    }

    @Override
    public RealEstate updateRealEstate(UUID addressId, RealEstateRequest request) {
        RealEstate realEstate = getRealEstate(addressId);
        realEstateMapper.updateRealEstateAddress(request, realEstate);
        return realEstateRepository.save(realEstate);
    }

    @Override
    public List<RealEstate> getRealEstateByPartnerId(Pagination pagination, UUID partnerId) {
        Sort sort = Sort.by(Sort.Direction.fromString(pagination.getSortOrder()), pagination.getSortBy());
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(), sort);
        Page<RealEstate> allByPartnerId = realEstateRepository.findAllByPartnerId(partnerId, pageable);
        return allByPartnerId.getContent();
    }

    @Override
    public List<RealEstate> getCurrentRealEstate(Pagination pagination) {
        Sort sort = Sort.by(Sort.Direction.fromString(pagination.getSortOrder()), pagination.getSortBy());
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(), sort);
        int organizationId = TokenExtractor
                .extractSdCurrentOrganizationId(currentUserService.getCurrentUserToken());
        Partner partner = partnerRepository.findBySmartDealOrganizationId(organizationId)
                .orElseThrow(() -> new ItemNotFoundException(Partner.class, String.valueOf(organizationId)));
        return realEstateRepository.findAllByPartnerId(partner.getId(), pageable).getContent();
    }

    @Transactional(readOnly = true)
    public RealEstate getRealEstate(UUID realEstateAddressId) {
        return realEstateRepository.findById(realEstateAddressId)
                .orElseThrow(() -> new ItemNotFoundException(RealEstate.class, realEstateAddressId));
    }
}
