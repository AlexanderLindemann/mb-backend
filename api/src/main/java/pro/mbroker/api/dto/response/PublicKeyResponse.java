package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PublicKeyResponse {
    private List<Key> keys;

    @Getter
    @Setter
    public static class Key {
        private String kty;
        private String n;
        private String e;
    }
}

