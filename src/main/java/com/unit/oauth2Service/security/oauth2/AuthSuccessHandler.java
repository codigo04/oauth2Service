package com.unit.oauth2Service.security.oauth2;

import com.unit.oauth2Service.dto.request.AuthRequestDTO;
import com.unit.oauth2Service.dto.response.AuthResponseDTO;
import com.unit.oauth2Service.security.service.AuthService;
import com.unit.oauth2Service.security.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;
@Component
public class AuthSuccessHandler  extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthService authService;


    private Optional<String> redirectURI =  Optional.of("http://localhost:8080/auth/dashboard");


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        if (!isAuthorizedRedirectURI(redirectURI.get())) {//http://localhost:8080/auth/dashboard
            throw new AccessDeniedException("Unauthorized Redirect URI");
        }


        String targetUrl = redirectURI.orElse(getDefaultTargetUrl());

        // Obtiene el usuario autenticado y genera el token
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

       // AuthResponseDTO  respuesta = authService.registerAuth2(new AuthRequestDTO(oAuth2User.getAttribute("email"),""));

        User user = new User(
                oAuth2User.getAttribute("email"),
                "password", true,
                true,
                true,
                true,
                Collections.emptyList()
        );

       String token = jwtService.generateToken(user);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);

        //  repository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectURI(String uri) {
        URI clientRedirectURI = URI.create(uri);

//        return appProperties.getAuthorizedRedirectURIs().stream().anyMatch(authURI -> {
//
//            URI authorizedURI = URI.create(authURI);
//
//            return authorizedURI.getHost().equalsIgnoreCase(clientRedirectURI.getHost()) &&
//
//                    authorizedURI.getPort() == clientRedirectURI.getPort();
//        });


        return true;
    }
}
