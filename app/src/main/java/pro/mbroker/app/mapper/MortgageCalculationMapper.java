package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
    MortgageCalculation toMortgageCalculation(MortgageCalculationDto mortgageCalculationDto);
}