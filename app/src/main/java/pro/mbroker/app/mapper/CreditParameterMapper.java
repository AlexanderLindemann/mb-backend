package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import pro.mbroker.api.dto.request.CreditParameterResponse;
import pro.mbroker.app.entity.CreditParameter;

@Mapper(config = ProgramMapperConfig.class, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CreditParameterMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isMaternalCapital", expression = "java(creditParameterResponse.getIsMaternalCapital() != null ? creditParameterResponse.getIsMaternalCapital() : Boolean.FALSE)")
    CreditParameter toCreditParameterMapper(CreditParameterResponse creditParameterResponse);

    CreditParameterResponse toCreditParameterResponseMapper(CreditParameter creditParameterRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isMaternalCapital", expression = "java(creditParameterResponse.getIsMaternalCapital() != null ? creditParameterResponse.getIsMaternalCapital() : Boolean.FALSE)")
    void updateCreditParameter(CreditParameterResponse creditParameterResponse, @MappingTarget CreditParameter creditParameter);
}

