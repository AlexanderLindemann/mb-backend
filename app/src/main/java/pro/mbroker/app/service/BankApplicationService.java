package pro.mbroker.app.service;

import pro.mbroker.app.entity.BankApplication;

import java.util.UUID;

public interface BankApplicationService {

    BankApplication getBankApplicationById(UUID id);

}
