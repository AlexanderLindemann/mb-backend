package pro.mbroker.app.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AuthenticationTokenFilter extends OncePerRequestFilter {
    private static final int BEARER_PREFIX_LENGTH = 7;

    private final TokenExtractor tokenExtractor;

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
        int sdId = TokenExtractor.extractSdId(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(token);

        Jwt jwt = Jwt.withTokenValue(token)
                .header("alg", "RS384")
                .claim("sd_id", sdId)
                .build();

        return new JwtAuthenticationToken(jwt, authorities);
    }

    private List<SimpleGrantedAuthority> getAuthorities(String token) {
        List<String> permissions = tokenExtractor.getPermissions(token);

        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}

