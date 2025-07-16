package org.jcr.generadorpreguntasjava.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.jcr.generadorpreguntasjava.infrastructure.security.config.CsrfProperties;
import org.jcr.generadorpreguntasjava.infrastructure.security.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración de seguridad con OAuth2 y JWT.
 * Implementa autenticación basada en tokens JWT con cookies seguras.
 * Configura CORS y CSRF para trabajar con frontend React.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    private final CsrfProperties csrfProperties;
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    private final OAuth2FailureHandler oAuth2FailureHandler;
    
    /**
     * Configuración de seguridad principal.
     * Activa por defecto en perfiles dev y prod.
     */
    @Bean
    @Profile("!test")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Configurar CSRF según configuración
                .csrf(csrf -> {
                    if (csrfProperties.isEnabled()) {
                        if ("cookie".equalsIgnoreCase(csrfProperties.getTokenRepository())) {
                            CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
                            repo.setCookieName(csrfProperties.getCookie().getName());
                            repo.setSecure(csrfProperties.getCookie().isSecure());
                            // No hay método directo para sameSite, eso se debería setear vía header

                            csrf.csrfTokenRepository(repo)
                                    .ignoringRequestMatchers("/auth/**", "/oauth2/**", "/login/oauth2/**");
                        }
                    } else {
                        csrf.disable();
                    }
                })
            
            // Configurar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configurar manejo de sesiones (stateless para JWT)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configurar autorización
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos de autenticación
                .requestMatchers("/auth/**").permitAll()
                
                // Endpoints públicos de OAuth2
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers("/login/oauth2/**").permitAll()
                
                // Endpoints públicos de la aplicación
                .requestMatchers("/v1/**").permitAll() // Por ahora permitir todo
                .requestMatchers("/v1/health").permitAll()
                .requestMatchers("/h2-console/**").permitAll() // Para desarrollo con H2
                
                // Requerir autenticación para el resto
                .anyRequest().authenticated()
            )
            
            // Configurar OAuth2 Login
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/auth/oauth2/success", true)
                .failureUrl("/auth/oauth2/failure")
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)
            )
            
            // Configurar headers de seguridad
            .headers(headers -> headers
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // Permitir H2 Console
                    .contentTypeOptions(Customizer.withDefaults())
                    .httpStrictTransportSecurity(hsts -> hsts
                            .includeSubDomains(true)
                            .maxAgeInSeconds(31536000)
                    )
            )
            // Agregar filtro JWT antes del filtro de autenticación por defecto
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * Configuración de seguridad para testing.
     * Desactiva toda la seguridad cuando el perfil activo es test.
     */
    @Bean
    @Profile("test")
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

    /**
     * Configuración de CORS para permitir peticiones desde el frontend React.
     * Habilita cookies cross-origin para el intercambio de tokens.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With",
                "Accept", "Origin", "Access-Control-Request-Method",
                "Access-Control-Request-Headers", "X-XSRF-TOKEN"
        ));
        configuration.setAllowCredentials(true); // Habilitar cookies cross-origin
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
    
    /**
     * Codificador de contraseñas para el sistema de autenticación.
     * Usa BCrypt para hash seguro de contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
