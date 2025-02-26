package com.unit.oauth2Service.security.config;


import com.unit.oauth2Service.security.filters.JwtAutenticationFilter;
import com.unit.oauth2Service.security.oauth2.AccessTokenResConverter;
import com.unit.oauth2Service.security.oauth2.AuthSuccessHandler;
import com.unit.oauth2Service.security.oauth2.OAuth2ReqResolver;
import com.unit.oauth2Service.security.service.AuthService;
import com.unit.oauth2Service.security.service.impl.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.RestClient;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String OAuth2BaseURI = "/oauth2/authorize";
    private static final String OAuth2RedirectionEndPoint = "/oauth2/callback/*";

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private JwtAutenticationFilter jwtAutenticationFilter;

    @Autowired
    private AuthSuccessHandler authSuccessHandler;


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults());
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });
        http.authorizeHttpRequests(auth -> {
            auth .requestMatchers("/", "/auth/**", "/oauth2/**").permitAll();
            auth .requestMatchers("/error").permitAll();
            auth.requestMatchers("/private/user/**").authenticated();
            auth.requestMatchers("/auth/libre/**").authenticated();
            auth.requestMatchers("/api/profile/**").hasAnyRole("ADMIN", "USER");
            auth.requestMatchers("/api/user/**").hasAnyRole("ADMIN", "USER");
            auth.requestMatchers("/api/role/**").hasAnyRole("ADMIN");
            auth.requestMatchers("/api/permission/**").hasAnyRole("ADMIN");
            auth.anyRequest().denyAll();
        });
        http.oauth2Login(oauth2 -> {

            oauth2.authorizationEndpoint(c -> {
                c.baseUri(OAuth2BaseURI);
                //c.authorizationRequestRepository(cookieAuthReqRepo);
                c.authorizationRequestResolver(
                        new OAuth2ReqResolver(clientRegistrationRepository, OAuth2BaseURI));
            });

            oauth2.redirectionEndpoint(
                    c -> c.baseUri(OAuth2RedirectionEndPoint));
            oauth2.userInfoEndpoint(
                    c -> c.userService(authService));
           // oauth2.tokenEndpoint();

            oauth2.successHandler(authSuccessHandler);

            oauth2.failureHandler((request, response, exception) -> {
                System.out.println("Error en OAuth2: " + exception.getMessage());
                response.sendRedirect("/login?error");
            });
        });
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint())
        );
        http.authenticationProvider(provider(authService));

        http.addFilterBefore(jwtAutenticationFilter, BasicAuthenticationFilter.class);

        return http.build();
    }



    //metodo que se encarga de intercambiar un código de autorización por un token de acceso en el flujo de OAuth2
    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient() {

        // Crea un conversor de respuesta de token que transformará la respuesta del servidor OAuth2
        OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();

        // Configura un conversor personalizado para transformar la respuesta en un formato adecuado
        tokenResponseHttpMessageConverter.setAccessTokenResponseConverter(new AccessTokenResConverter());


        // Crea un cliente que se encargará de intercambiar el código de autorización por un token de acceso
        RestClientAuthorizationCodeTokenResponseClient client = new RestClientAuthorizationCodeTokenResponseClient();

        // Configura el cliente HTTP que se usará para hacer la solicitud al servidor OAuth2
        client.setRestClient(RestClient.builder()
                // Define los conversores de mensajes que manejarán las solicitudes y respuestas HTTP
                .messageConverters(List.of(
                        new FormHttpMessageConverter(), // Permite enviar datos en formato application/x-www-form-urlencoded
                        tokenResponseHttpMessageConverter // Convierte la respuesta del token de OAuth2
                ))
                // Maneja errores específicos de OAuth2 en la respuesta HTTP
                .defaultStatusHandler(new OAuth2ErrorResponseErrorHandler())
                .build());

        // Retorna el cliente configurado para ser usado en el flujo de OAuth2
        return client;
    }




    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response,
                accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("403 Forbidden Access: " + accessDeniedException.getMessage());

        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new Http403ForbiddenEntryPoint();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }



    @Bean
   public AuthenticationProvider provider(AuthService authService) {
       DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
       provider.setPasswordEncoder(new BCryptPasswordEncoder());
       provider.setUserDetailsService((UserDetailsService) authService);
        return provider;
    }

    // Configuración de CORS
    private UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5174", "http://localhost:5173", "http://localhost:5175"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
