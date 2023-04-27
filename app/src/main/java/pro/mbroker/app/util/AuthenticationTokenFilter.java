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
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private final TokenExtractor tokenExtractor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Authentication authentication = getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
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
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        List<String> permissions = tokenExtractor.getPermissions(token);

        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }

        return authorities;
    }
}

