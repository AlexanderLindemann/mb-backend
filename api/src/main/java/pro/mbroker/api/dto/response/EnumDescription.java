package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class EnumDescription {

    private String code;

    private String name;

    private String description;

    private List<EnumItemDescription> values = new ArrayList<>();

}
