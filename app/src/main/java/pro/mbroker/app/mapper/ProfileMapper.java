package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pro.mbroker.api.dto.ProfileListItem;
import pro.mbroker.app.model.profile.Profile;

@Mapper
public interface ProfileMapper {

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "number", source = "id")
    @Mapping(target = "fio", source = "baseInfo.fio")
    @Mapping(target = "propertyPrice", source = "baseInfo.propertyPrice")
    @Mapping(target = "mortgageSum", source = "baseInfo.mortgageSum")
    @Mapping(target = "firstPay", source = "baseInfo.firstPay")
    @Mapping(target = "durationMonths", source = "baseInfo.durationMonths")
    @Mapping(target = "type", source = "baseInfo.type")
    ProfileListItem map(Profile profile);

}
