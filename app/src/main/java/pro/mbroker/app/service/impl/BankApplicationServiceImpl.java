package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.repository.BankApplicationRepository;
import pro.mbroker.app.service.BankApplicationService;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankApplicationServiceImpl implements BankApplicationService {
    private final BankApplicationRepository bankApplicationRepository;

    @Override
    public BankApplication getBankApplicationById(UUID id) {
        return bankApplicationRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(BankApplication.class, id));
    }
}
