package pro.mbroker.app.util;

import org.springframework.stereotype.Component;
import pro.mbroker.api.dto.response.PublicKeyResponse;
import pro.mbroker.app.feign.PublicKeyClient;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Component
public class PublicKeyCacheService {
    private RSAPublicKey cachedPublicKey;
    private final PublicKeyClient publicKeyClient;

    public PublicKeyCacheService(PublicKeyClient publicKeyClient) {
        this.publicKeyClient = publicKeyClient;
    }

    public RSAPublicKey getPublicKey() {
        if (cachedPublicKey == null) {
            updateCachedPublicKey();
        }
        return cachedPublicKey;
    }

    public void updateCachedPublicKey() {
        PublicKeyResponse response = publicKeyClient.getPublicKey();

        if (response != null && !response.getKeys().isEmpty()) {
            String modulus = response.getKeys().get(0).getN();
            String exponent = response.getKeys().get(0).getE();
            cachedPublicKey = convertToPublicKey(modulus, exponent);
        } else {
            throw new IllegalStateException("Не удалось получить публичный ключ от удаленного API");
        }
    }

    private RSAPublicKey convertToPublicKey(String modulus, String exponent) {
        try {
            BigInteger modulusInt = new BigInteger(1, Base64.getUrlDecoder().decode(modulus));
            BigInteger exponentInt = new BigInteger(1, Base64.getUrlDecoder().decode(exponent));
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulusInt, exponentInt);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Невозможно создать RSAPublicKey", e);
        }
    }
}

