package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.RealEstateAddressRequest;
import pro.mbroker.app.entity.RealEstateAddress;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.RealEstateAddressMapper;
import pro.mbroker.app.repository.RealEstateAddressRepository;
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
    private final RealEstateAddressRepository realEstateAddressRepository;
    private final RealEstateAddressMapper realEstateAddressMapper;

    @Override
    @Transactional
    public RealEstateAddress addRealEstateAddress(UUID partnerId, RealEstateAddressRequest request) {
        RealEstateAddress realEstateAddress = realEstateAddressMapper.toRealEstateAddressMapper(request)
                .setPartner(partnerService.getPartner(partnerId));
        RealEstateAddress address = realEstateAddressRepository.save(realEstateAddress);
        realEstateAddressRepository.flush();
        return getRealEstateAddress(address.getId());
    }

    @Override
    @Transactional
    public void deleteRealEstateAddress(UUID addressId) {
        RealEstateAddress realEstateAddress = getRealEstateAddress(addressId);
        realEstateAddressRepository.delete(realEstateAddress);
    }

    @Override
    public RealEstateAddress updateRealEstateAddress(UUID addressId, RealEstateAddressRequest request) {
        RealEstateAddress realEstateAddress = getRealEstateAddress(addressId);
        realEstateAddressMapper.updateRealEstateAddress(request, realEstateAddress);
        return realEstateAddressRepository.save(realEstateAddress);
    }

    @Override
    public List<RealEstateAddress> getRealEstateAddressByPartnerId(Pagination pagination, UUID partnerId) {
        Sort sort = Sort.by(Sort.Direction.fromString(pagination.getSortOrder()), pagination.getSortBy());
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(), sort);
        Page<RealEstateAddress> allByPartnerId = realEstateAddressRepository.findAllByPartnerId(partnerId, pageable);
        return allByPartnerId.getContent();
    }

    @Transactional(readOnly = true)
    public RealEstateAddress getRealEstateAddress(UUID realEstateAddressId) {
        return realEstateAddressRepository.findById(realEstateAddressId)
                .orElseThrow(() -> new ItemNotFoundException(RealEstateAddress.class, realEstateAddressId));
    }
}
