package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import pro.mbroker.api.dto.request.BorrowerEmployerRequest;
import pro.mbroker.app.entity.BorrowerEmployer;

import java.util.Optional;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, uses = BankMapper.class)
public interface BorrowerEmployerMapper {

    default void updateBorrowerEmployerFromDto(BorrowerEmployerRequest dto, @MappingTarget BorrowerEmployer employer) {
        if (dto == null || employer == null) {
            return;
        }

        Optional.ofNullable(dto.getName()).ifPresent(employer::setName);
        Optional.ofNullable(dto.getTin()).ifPresent(employer::setTin);
        Optional.ofNullable(dto.getBranch()).ifPresent(employer::setBranch);
        Optional.ofNullable(dto.getNumberOfEmployees()).ifPresent(employer::setNumberOfEmployees);
        Optional.ofNullable(dto.getOrganizationAge()).ifPresent(employer::setOrganizationAge);
        Optional.ofNullable(dto.getPhone()).ifPresent(employer::setPhone);
        Optional.ofNullable(dto.getSite()).ifPresent(employer::setSite);
        Optional.ofNullable(dto.getWorkExperience()).ifPresent(employer::setWorkExperience);
        Optional.ofNullable(dto.getPosition()).ifPresent(employer::setPosition);
        Optional.ofNullable(dto.getAddress()).ifPresent(employer::setAddress);

    }
}
