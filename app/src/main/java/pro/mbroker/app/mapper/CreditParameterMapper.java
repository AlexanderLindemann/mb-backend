package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pro.mbroker.api.dto.request.CreditParameterResponse;
import pro.mbroker.app.entity.CreditParameter;

@Mapper(config = ProgramMapperConfig.class)
public interface CreditParameterMapper {
    @Mapping(target = "id", ignore = true)
    CreditParameter toCreditParameterMapper(CreditParameterResponse creditParameterResponse);

    CreditParameterResponse toCreditParameterResponseMapper(CreditParameter creditParameterRequest);

    @Mapping(target = "id", ignore = true)
    void updateCreditParameter(CreditParameterResponse creditParameterResponse, @MappingTarget CreditParameter creditParameter);

}

