package pro.mbroker.api.enums;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class EnumDescription {

    String code;

    List<EnumItemDescription> values;

}
