package br.com.fiap.conectasocial.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApiSecurityErrorHandler apiErrorHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          ApiSecurityErrorHandler apiErrorHandler) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.apiErrorHandler = apiErrorHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of("*"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", cfg);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        RequestMatcher apiMatcher = request ->
                request.getRequestURI().startsWith(request.getContextPath() + "/api/");

        http
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                    // ---- público ----
                    .requestMatchers(
                            "/login",
                            "/registrar",
                            "/recuperar-senha",
                            "/redefinir-senha",
                            "/api/auth/**",
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/css/**",
                            "/js/**",
                            "/webjars/**",
                            "/images/**",
                            "/favicon.ico"
                    ).permitAll()

                    // ---- API: regras por papel (a ordem importa) ----
                    .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/campanhas/minhas").hasRole("ONG")
                    .requestMatchers(HttpMethod.GET, "/api/campanhas", "/api/campanhas/*").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/campanhas").hasRole("ONG")
                    .requestMatchers(HttpMethod.PUT, "/api/campanhas/*").hasRole("ONG")
                    .requestMatchers(HttpMethod.DELETE, "/api/campanhas/*").hasRole("ONG")
                    .requestMatchers(HttpMethod.GET, "/api/campanhas/*/interesses").hasRole("ONG")
                    .requestMatchers(HttpMethod.POST, "/api/campanhas/*/interesses")
                            .hasAnyRole("VOLUNTARIO", "BENEFICIARIO")
                    .requestMatchers(HttpMethod.GET, "/api/interesses/meus")
                            .hasAnyRole("VOLUNTARIO", "BENEFICIARIO")
                    .requestMatchers("/api/ia/**").hasAnyRole("VOLUNTARIO", "BENEFICIARIO")
                    .requestMatchers("/ia/**").hasAnyRole("VOLUNTARIO", "BENEFICIARIO")

                    // ---- telas web (já existiam) ----
                    .requestMatchers("/", "/home").authenticated()
                    .requestMatchers("/ong/campanhas/**").hasRole("ONG")
                    .requestMatchers("/ong/interesses/**").hasRole("ONG")
                    .requestMatchers("/campanhas/*/interesse").hasAnyRole("VOLUNTARIO", "BENEFICIARIO")
                    .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                    .defaultAuthenticationEntryPointFor(apiErrorHandler, apiMatcher)
                    .defaultAccessDeniedHandlerFor(apiErrorHandler, apiMatcher)
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .userDetailsService(userDetailsService)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
