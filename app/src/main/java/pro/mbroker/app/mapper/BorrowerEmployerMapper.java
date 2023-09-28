package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import pro.mbroker.api.dto.EmployerDto;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BorrowerEmployer;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, uses = BankMapper.class)
public interface BorrowerEmployerMapper {

    default void updateBorrowerEmployerFromDto(EmployerDto dto, @MappingTarget BorrowerEmployer employer) {
        if (dto == null || employer == null) {
            return;
        }

        Optional.ofNullable(dto.getName()).ifPresent(employer::setName);
        Optional.ofNullable(dto.getInn()).ifPresent(employer::setInn);
        Optional.ofNullable(dto.getBranch()).ifPresent(employer::setBranch);
        Optional.ofNullable(dto.getNumberOfEmployees()).ifPresent(employer::setNumberOfEmployees);
        Optional.ofNullable(dto.getOrganizationAge()).ifPresent(employer::setOrganizationAge);
        Optional.ofNullable(dto.getPhone()).ifPresent(employer::setPhone);
        Optional.ofNullable(dto.getSite()).ifPresent(employer::setSite);
        Optional.ofNullable(dto.getWorkExperience()).ifPresent(employer::setWorkExperience);
        Optional.ofNullable(dto.getPosition()).ifPresent(employer::setPosition);
        Optional.ofNullable(dto.getAddress()).ifPresent(employer::setAddress);
        Optional.ofNullable(dto.getSalaryBanks()).ifPresent(uuidList -> {
            Set<Bank> banks = mapUuidListToSetOfBank(uuidList);
            employer.setSalaryBanks(banks);
        });
    }


    default Set<Bank> mapUuidListToSetOfBank(List<UUID> uuidList) {
        return uuidList.stream()
                .map(uuid -> {
                    Bank bank = new Bank();
                    bank.setId(uuid);
                    return bank;
                })
                .collect(Collectors.toSet());
    }

}

