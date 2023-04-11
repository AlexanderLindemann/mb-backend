package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.RealEstateAddress;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.PartnerMapper;
import pro.mbroker.app.mapper.RealEstateAddressMapper;
import pro.mbroker.app.repository.PartnerRepository;
import pro.mbroker.app.service.BankService;
import pro.mbroker.app.service.CreditProgramService;
import pro.mbroker.app.service.PartnerService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {
    private final BankService bankService;
    private final CreditProgramService creditProgramService;
    private final PartnerRepository partnerRepository;
    private final PartnerMapper partnerMapper;
    private final RealEstateAddressMapper realEstateAddressMapper;

    @Override
    public Partner createPartner(PartnerRequest request) {
        List<RealEstateAddress> realEstateAddresses = realEstateAddressMapper.toRealEstateAddressList(request.getRealEstateAddressRequest());

        Partner partner = partnerMapper.toPartnerMapper(request)
                .setBank(bankService.getBankById(request.getBankId()))
                .setCreditPrograms(creditProgramService.getProgramByCreditProgramIds(request.getBankCreditProgram()))
                .setRealEstateAddress(realEstateAddresses);

        realEstateAddresses.forEach(address -> address.setPartner(partner));
        return partnerRepository.save(partner);
    }

    public Partner getPartner(UUID partnerId) {
        return partnerRepository.findById(partnerId)
                .orElseThrow(() -> new ItemNotFoundException(Partner.class, partnerId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Partner> getAllPartner(int page, int size, String sortBy, String sortOrder) {
        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return partnerRepository.findAllWithBankBy(pageable);
    }

}
