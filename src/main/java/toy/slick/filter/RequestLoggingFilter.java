package toy.slick.filter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.Collection;
import java.util.Set;

@Slf4j
public class RequestLoggingFilter extends CommonsRequestLoggingFilter {
    private final Collection<String> excludeUriCollection = Set.of("/actuator/health");

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return !excludeUriCollection.contains(request.getRequestURI());
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        log.info(message + "\n" +
                "> X-FORWARDED-FOR          : " + request.getHeader("X-Forwarded-For") + "\n" +
                "> Proxy-Client-IP          : " + request.getHeader("Proxy-Client-IP") + "\n" +
                "> WL-Proxy-Client-IP       : " + request.getHeader("WL-Proxy-Client-IP") + "\n" +
                "> HTTP_X_FORWARDED_FOR     : " + request.getHeader("HTTP_X_FORWARDED_FOR") + "\n" +
                "> HTTP_X_FORWARDED         : " + request.getHeader("HTTP_X_FORWARDED") + "\n" +
                "> HTTP_X_CLUSTER_CLIENT_IP : " + request.getHeader("HTTP_X_CLUSTER_CLIENT_IP") + "\n" +
                "> X-Real-IP                : " + request.getHeader("X-Real-IP") + "\n" +
                "> X-RealIP                 : " + request.getHeader("X-RealIP") + "\n" +
                "> HTTP_CLIENT_IP           : " + request.getHeader("HTTP_CLIENT_IP") + "\n" +
                "> HTTP_FORWARDED_FOR       : " + request.getHeader("HTTP_FORWARDED_FOR") + "\n" +
                "> HTTP_FORWARDED           : " + request.getHeader("HTTP_FORWARDED") + "\n" +
                "> HTTP_VIA                 : " + request.getHeader("HTTP_VIA") + "\n" +
                "> REMOTE_ADDR              : " + request.getHeader("REMOTE_ADDR") + "\n" +
                "> CF-Connecting-IP         : " + request.getHeader("CF-Connecting-IP") + "\n" +
                "> getRemoteAddr            : " + request.getRemoteAddr());
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        log.info("Payload : " + getMessagePayload(request));
    }
}
