package pro.mbroker.api.enums;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EnumItemDescription {
    String code;
    String name;
    String description;
}
