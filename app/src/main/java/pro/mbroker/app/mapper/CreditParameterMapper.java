package pro.mbroker.app.mapper;

import org.mapstruct.*;
import pro.mbroker.api.dto.request.CreditParameterRequest;
import pro.mbroker.app.model.program.CreditParameter;

@Mapper
public interface CreditParameterMapper {
    @Mapping(target = "id", ignore = true)
    CreditParameter toCreditParameterMapper(CreditParameterRequest creditParameterRequest);

    CreditParameterRequest toCreditParameterRequestMapper(CreditParameter creditParameterRequest);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCreditParameter(CreditParameterRequest creditParameterRequest, @MappingTarget CreditParameter creditParameter);

}

