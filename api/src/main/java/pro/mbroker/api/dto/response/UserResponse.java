package pro.mbroker.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.smartdeal.common.security.Permission;

import java.util.EnumSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private EnumSet<Permission> permissions;

}