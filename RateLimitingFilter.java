import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitingWebFilter implements WebFilter {

    private final ConcurrentHashMap<String, Long> requestCounts = new ConcurrentHashMap<>();
    private final long THRESHOLD = 100; // Max requests per minute
    private final long TIME_WINDOW = TimeUnit.MINUTES.toMillis(1);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
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
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return Mono.empty();
        }

        return chain.filter(exchange);
    }
}
