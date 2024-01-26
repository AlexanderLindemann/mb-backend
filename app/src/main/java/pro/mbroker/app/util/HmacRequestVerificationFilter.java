package pro.mbroker.app.util;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class HmacRequestVerificationFilter extends OncePerRequestFilter {

    private static final String HMAC_HEADER = "X-HMAC-Hash";
    private static final String TIMESTAMP_HEADER = "X-Timestamp";

    //TODO спрятать в application.yaml после тестирования
    private static final String SECRET_KEY = "fa12943adbfa3c25";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("/actuator/health".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        String hmacHeader = wrappedRequest.getHeader(HMAC_HEADER);
        String timestamp = wrappedRequest.getHeader(TIMESTAMP_HEADER);
        String dataToSign = (timestamp != null) ? timestamp : new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
        String calculatedHmac = generateHMAC(dataToSign, SECRET_KEY);
        if (!calculatedHmac.equals(hmacHeader)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid HMAC Hash");
            return;
        }
        filterChain.doFilter(wrappedRequest, response);
    }

    private String generateHMAC(String data, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC", e);
        }
    }
}

