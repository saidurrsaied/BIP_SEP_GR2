package com.parking.config; // Pas dit aan naar jouw package

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Voor nu even gehardcodeerd voor het gemak
    private final String frontendUrl = "http://localhost:5173";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Schakel CORS in (gebruikt de bean onderaan)
                .cors(cors -> {})

                // 2. CSRF: We gebruiken een cookie die SvelteKit kan lezen
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/users/login", "/api/users/register")
                )

                // 3. Geen standaard inlogschermen of basic auth
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 4. Toegangsregels
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/login", "/api/users/register", "/api/zones/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 5. Sessiebeheer (Zorg dat dit NIET stateless is!)
                .sessionManagement(session -> session
                        .maximumSessions(1)
                )

                // 6. Error handling: Stuur 401 terug i.p.v. een redirect naar een loginpagina
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                // 7. Logout
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/users/logout", "POST"))
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpStatus.OK.value()))
                );

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // Verplicht voor SessionID/Cookies
        config.setAllowedOrigins(List.of(frontendUrl));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN", "Accept"));

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
