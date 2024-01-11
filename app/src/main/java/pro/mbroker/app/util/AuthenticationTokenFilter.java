package pro.mbroker.app.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class AuthenticationTokenFilter extends OncePerRequestFilter {
    private static final int BEARER_PREFIX_LENGTH = 7;

    private final PublicKeyCacheService publicKeyCacheService;
    private final TokenExtractor tokenExtractor;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {   //TODO когда авторизация будет в gateway
        String path = request.getServletPath();                                               // эта штука будет не актуально. Сейчас убрал проверку,
        return "/public/v1/bank_application/update-statuses".equals(path);                    // потому что у нас notification каждый час дергает метод и там не будет токена
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(BEARER_PREFIX_LENGTH);
            Authentication authentication = getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(String token) {
        try {
            validateToken(token);
            List<SimpleGrantedAuthority> authorities = getAuthorities(token);
            int sdId = getSdId(token, authorities);
            return createJwtAuthenticationToken(token, sdId, authorities);
        } catch (JwtException e) {
            log.info("Попытка обновить кешированный публичный ключ для токена");
            publicKeyCacheService.updateCachedPublicKey();
            validateToken(token);
            List<SimpleGrantedAuthority> authorities = getAuthorities(token);
            int sdId = getSdId(token, authorities);
            return createJwtAuthenticationToken(token, sdId, authorities);
        }
    }

    private static int getSdId(String token, List<SimpleGrantedAuthority> authorities) {
        return authorities.stream().anyMatch(authority -> authority.getAuthority().contains("MB_CABINET_ACCESS"))
                ? 0
                : TokenExtractor.extractSdId(token);
    }

    private JwtAuthenticationToken createJwtAuthenticationToken(String token, int sdId, List<SimpleGrantedAuthority> authorities) {
        Jwt jwt = Jwt.withTokenValue(token)
                .header("alg", "RS384")
                .claim("sd_id", sdId)
                .build();
        return new JwtAuthenticationToken(jwt, authorities);
    }

    private void validateToken(String token) {
        RSAPublicKey publicKey = publicKeyCacheService.getPublicKey();
        JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(publicKey)
                .signatureAlgorithm(SignatureAlgorithm.RS384)
                .build();
        jwtDecoder.decode(token);
    }

    private List<SimpleGrantedAuthority> getAuthorities(String token) {
        List<String> permissions = tokenExtractor.getPermissions(token);

        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}