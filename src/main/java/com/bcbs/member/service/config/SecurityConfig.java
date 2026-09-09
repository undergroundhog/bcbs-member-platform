package com.bcbs.member.service.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(CorsUtils::isPreFlightRequest)
                                .permitAll()

                        .requestMatchers(EndpointRequest.to("health", "info"))
                        .permitAll()
//                        .requestMatchers("api/v1/members/**")
//                        .authenticated()

                                .requestMatchers(EndpointRequest.to("metrics", "prometheus"))
                                .hasRole("ACTUATOR")

                                .requestMatchers(EndpointRequest.toAnyEndpoint())
                                .denyAll()

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/v1/members/**"
                                )
                                .hasAnyRole("MEMBER_READ", "MEMBER_ADMIN")


                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/v1/members/**"
                                )
                                .hasRole("MEMBER_ADMIN")


                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/api/v1/members/**"
                                )
                                .hasRole("MEMBER_ADMIN")


                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/v1/members/**"
                                )
                                .hasRole("MEMBER_ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .httpBasic(httpBasic -> {});
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:3000")
        );

        configuration.setAllowedMethods(
                List.of("GET","POST","PUT","DELETE","OPTIONS")
        );


        configuration.setAllowedHeaders(
                List.of("Authorization", "Content-Type", "X-Request-Id")
        );

        configuration.setExposedHeaders(
                List.of("X-Request-Id")
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/api/**",
                configuration
        );

        return source;
    }
}
