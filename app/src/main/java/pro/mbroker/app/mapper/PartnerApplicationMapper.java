package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import pro.mbroker.api.dto.PartnerApplicationDto;
import pro.mbroker.app.entity.PartnerApplication;

import java.util.List;

@Mapper(uses = {RealEstateMapper.class, BorrowerApplicationMapper.class})
public interface PartnerApplicationMapper {

    PartnerApplicationDto toDto(PartnerApplication partnerApplication);

    List<PartnerApplicationDto> toDtoList(List<PartnerApplication> partnerApplications);

}