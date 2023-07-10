package pro.mbroker.app.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pro.mbroker.api.dto.MortgageCalculationDto;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.MortgageCalculation;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper
public interface MortgageCalculationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "monthCreditTerm", ignore = true)
    @Mapping(target = "salaryBanks", ignore = true)
    MortgageCalculation toMortgageCalculation(MortgageCalculationDto mortgageCalculationDto);

    @Mapping(target = "creditTerm", ignore = true)
    @Mapping(target = "salaryBanks", expression = "java(toUuidList(mortgageCalculation.getSalaryBanks()))")
    MortgageCalculationDto toMortgageCalculationDto(MortgageCalculation mortgageCalculation);


    @AfterMapping
    default void calculateMonthCreditTermFromYears(MortgageCalculationDto mortgageCalculationDto, @MappingTarget MortgageCalculation mortgageCalculation) {
        if (mortgageCalculationDto.getCreditTerm() != null) {
            mortgageCalculation.setMonthCreditTerm(mortgageCalculationDto.getCreditTerm() * 12);
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "monthCreditTerm", ignore = true)
    @Mapping(target = "salaryBanks", ignore = true)
    void updateMortgageCalculationFromRequest(MortgageCalculationDto request, @MappingTarget MortgageCalculation mortgageCalculation);

    @AfterMapping
    default void calculateCreditTermInYears(@MappingTarget MortgageCalculationDto response, MortgageCalculation mortgageCalculation) {
        if (mortgageCalculation.getMonthCreditTerm() != null) {
            response.setCreditTerm(mortgageCalculation.getMonthCreditTerm() / 12);
        }
    }

    default List<UUID> toUuidList(List<Bank> banks) {
        if (banks == null) {
            return null;
        }
        return banks.stream().map(Bank::getId).collect(Collectors.toList());
    }
}
