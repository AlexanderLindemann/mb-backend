package pro.mbroker.app.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pro.mbroker.api.dto.MortgageCalculationDto;
import pro.mbroker.app.entity.MortgageCalculation;

@Mapper
public interface MortgageCalculationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "monthCreditTerm", ignore = true)
    MortgageCalculation toMortgageCalculation(MortgageCalculationDto mortgageCalculationDto);
    @Mapping(target = "creditTerm", ignore = true)
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
    void updateMortgageCalculationFromRequest(MortgageCalculationDto request, @MappingTarget MortgageCalculation mortgageCalculation);

    @AfterMapping
    default void calculateCreditTermInYears(@MappingTarget MortgageCalculationDto response, MortgageCalculation mortgageCalculation) {
        if (mortgageCalculation.getMonthCreditTerm() != null) {
            response.setCreditTerm(mortgageCalculation.getMonthCreditTerm() / 12);
        }
    }
}