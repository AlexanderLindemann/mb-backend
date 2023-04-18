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
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.RealEstateMapper;
import pro.mbroker.app.repository.RealEstateRepository;
import pro.mbroker.app.service.PartnerRealEstateService;
import pro.mbroker.app.service.PartnerService;
import pro.mbroker.app.util.Pagination;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerRealEstateServiceImpl implements PartnerRealEstateService {
    private final PartnerService partnerService;
    private final RealEstateRepository realEstateRepository;
    private final RealEstateMapper realEstateMapper;

    @Override
    @Transactional
    public RealEstate addRealEstateAddress(UUID partnerId, RealEstateRequest request) {
        RealEstate realEstate = realEstateMapper.toRealEstateMapper(request)
                .setPartner(partnerService.getPartner(partnerId));
        RealEstate address = realEstateRepository.save(realEstate);
        realEstateRepository.flush();
        return getRealEstateAddress(address.getId());
    }

    @Override
    @Transactional
    public void deleteRealEstateAddress(UUID addressId) {
        RealEstate realEstate = getRealEstateAddress(addressId);
        realEstateRepository.delete(realEstate);
    }

    @Override
    public RealEstate updateRealEstateAddress(UUID addressId, RealEstateRequest request) {
        RealEstate realEstate = getRealEstateAddress(addressId);
        realEstateMapper.updateRealEstateAddress(request, realEstate);
        return realEstateRepository.save(realEstate);
    }

    @Override
    public List<RealEstate> getRealEstateAddressByPartnerId(Pagination pagination, UUID partnerId) {
        Sort sort = Sort.by(Sort.Direction.fromString(pagination.getSortOrder()), pagination.getSortBy());
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(), sort);
        Page<RealEstate> allByPartnerId = realEstateRepository.findAllByPartnerId(partnerId, pageable);
        return allByPartnerId.getContent();
    }

    @Transactional(readOnly = true)
    public RealEstate getRealEstateAddress(UUID realEstateAddressId) {
        return realEstateRepository.findById(realEstateAddressId)
                .orElseThrow(() -> new ItemNotFoundException(RealEstate.class, realEstateAddressId));
    }
}
