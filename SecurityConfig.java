import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            // ...existing code...
            .addFilterBefore(rateLimitingWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            // ...existing code...
            .authorizeExchange()
            // ...existing code...
            .anyExchange().authenticated()
            .and()
            .httpBasic()
            .and()
            .formLogin();
        return http.build();
    }

    @Bean
    public WebFilter rateLimitingWebFilter() {
        return new RateLimitingWebFilter();
    }
}
