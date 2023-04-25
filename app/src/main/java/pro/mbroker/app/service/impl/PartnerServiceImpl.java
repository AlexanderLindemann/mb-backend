package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.PartnerMapper;
import pro.mbroker.app.mapper.RealEstateMapper;
import pro.mbroker.app.repository.PartnerRepository;
import pro.mbroker.app.service.CreditProgramService;
import pro.mbroker.app.service.PartnerService;
import pro.mbroker.app.util.Pagination;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {
    private final CreditProgramService creditProgramService;
    private final PartnerRepository partnerRepository;
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
        return partnerRepository.findAll(Pagination.createPageable(page, size, sortBy, sortOrder)).getContent();
    }

}
