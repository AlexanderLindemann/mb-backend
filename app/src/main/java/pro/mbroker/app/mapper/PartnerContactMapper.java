package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import pro.mbroker.api.dto.request.PartnerContactRequest;
import pro.mbroker.api.dto.response.PartnerContactResponse;
import pro.mbroker.api.enums.NotificationTrigger;
import pro.mbroker.app.entity.PartnerContact;
import pro.mbroker.app.util.Converter;

import java.util.List;

@Mapper
public interface PartnerContactMapper {

    @Mapping(target = "triggers", qualifiedByName = "stringToNotificationTriggerList")
    PartnerContactResponse toPartnerContactResponse(PartnerContact partnerContact);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "triggers", qualifiedByName = "notificationTriggersListToString")
    PartnerContact toPartnerContact(PartnerContactRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "triggers", qualifiedByName = "notificationTriggersListToString")
    void updatePartnerContact(PartnerContactRequest request, @MappingTarget PartnerContact partnerContact);

    @Named("notificationTriggersListToString")
    default String notificationTriggersListToString(List<NotificationTrigger> notificationTriggers) {
        return Converter.convertEnumListToString(notificationTriggers);
    }

    @Named("stringToNotificationTriggerList")
    default List<NotificationTrigger> stringToNotificationTriggerList(String str) {
        return Converter.convertStringListToEnumList(str, NotificationTrigger.class);
    }
}