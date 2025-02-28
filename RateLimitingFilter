import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RateLimitingFilter implements Filter {

    private final ConcurrentHashMap<String, Long> requestCounts = new ConcurrentHashMap<>();
    private final long THRESHOLD = 100; // Max requests per minute
    private final long TIME_WINDOW = TimeUnit.MINUTES.toMillis(1);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = httpRequest.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        requestCounts.merge(clientIp, currentTime, (oldTime, newTime) -> {
            if (newTime - oldTime > TIME_WINDOW) {
                return newTime;
            } else {
                return oldTime;
            }
        });

        long requestCount = requestCounts.values().stream()
                .filter(time -> currentTime - time <= TIME_WINDOW)
                .count();

        if (requestCount > THRESHOLD) {
            httpResponse.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}
