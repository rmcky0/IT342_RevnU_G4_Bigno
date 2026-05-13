package com.revnu.backend.shared.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.revnu.backend.shared.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            OAuth2SuccessHandler oAuth2SuccessHandler,
            OAuth2FailureHandler oAuth2FailureHandler,
            ClientRegistrationRepository clientRegistrationRepository,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.oAuth2FailureHandler = oAuth2FailureHandler;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    private OAuth2AuthorizationRequestResolver accountSwitchingResolver() {
        DefaultOAuth2AuthorizationRequestResolver base
                = new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository, "/oauth2/authorization");

        base.setAuthorizationRequestCustomizer(customizer -> customizer
                .additionalParameters(params -> {
                    Map<String, Object> extra = new LinkedHashMap<>(params);
                    extra.put("prompt", "select_account");
                    params.clear();
                    params.putAll(extra);
                })
        );
        return base;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(frontendUrl));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/revnu/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/revnu/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/revnu/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/revnu/auth/link-google").permitAll()
                .requestMatchers(HttpMethod.GET, "/revnu/files/**").permitAll()
                .requestMatchers("/revnu/settings/**").authenticated()
                .requestMatchers("/revnu/auth/me").authenticated()
                .requestMatchers("/revnu/sales/**").authenticated()
                .requestMatchers("/revnu/expenses/**").authenticated()
                .requestMatchers("/revnu/categories/**").authenticated()
                .requestMatchers("/revnu/dashboard/**").authenticated()
                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login/**", "/oauth2/**", "/error").permitAll()
                .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                .loginPage(frontendUrl + "/login")
                .authorizationEndpoint(ep -> ep
                .authorizationRequestResolver(accountSwitchingResolver())
                )
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)
                );

        return http.build();
    }
}
