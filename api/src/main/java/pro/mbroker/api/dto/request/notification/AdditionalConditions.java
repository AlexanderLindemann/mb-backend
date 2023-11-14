package pro.mbroker.api.dto.request.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdditionalConditions {
    private String description; //Описание доп. условия
    private String responsible; //Ответственный, кто будет рассматривать и снимать доп. условие

    public AdditionalConditions () {}
}
