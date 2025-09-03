package com.dabom.member.security.config;

import com.dabom.member.model.entity.MemberRole;
import com.dabom.member.security.filter.JwtAuthFilter;
import com.dabom.member.security.handler.CustomOAuth2AuthenticationFailureHandler;
import com.dabom.member.security.handler.OAuth2AuthenticationSuccessHandler;
import com.dabom.member.security.repository.StatelessAuthorizationRequestRepository;
import com.dabom.member.security.service.Oauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] AUTH_WHITE_LIST = {
            "/api/member/login",
            "/api/member/signup",
            "/api/member/exists/**",
            "/api/channel/board/**",
            "/api/chat/**",
            "/oauth2/authorization/**",
    };
    private static final String[] SWAGGER_LIST = {"/swagger-ui*/**", "/v3/api-docs/**", "/webjars/**"};

    private final AuthenticationConfiguration configuration;
    private final Oauth2UserService oauth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final CustomOAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5174",
                "http://localhost:5173"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter();
        http.oauth2Login(
                config -> {
                    config.userInfoEndpoint(
                            endpoint -> endpoint.userService(oauth2UserService)
                    );
                    config.authorizationEndpoint(auth ->
                            auth.authorizationRequestRepository(new StatelessAuthorizationRequestRepository()));
                    config.successHandler(oAuth2AuthenticationSuccessHandler);
                    config.failureHandler(oAuth2AuthenticationFailureHandler);
                }
        );

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(SWAGGER_LIST).permitAll()
                        .requestMatchers(AUTH_WHITE_LIST).permitAll()
                        .requestMatchers("/api/manager/**").hasRole(MemberRole.MANAGER.name())
                        .anyRequest().permitAll()
                )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
