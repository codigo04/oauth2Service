package com.unit.oauth2Service.security.filters;

import com.unit.oauth2Service.model.UserPrincipal;
import com.unit.oauth2Service.repository.UserRepository;
import com.unit.oauth2Service.security.service.AuthService;
import com.unit.oauth2Service.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class JwtAutenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthService authService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String tokenExtraido = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        //primera validacion
        //se comprueba si el token inviado no esta vacio y contiene la palabra Bearer
        if (ObjectUtils.isEmpty(tokenExtraido) || !StringUtils.startsWithIgnoreCase(tokenExtraido, "Bearer ")) {


            filterChain.doFilter(request, response);
            return;
        }


        jwt = tokenExtraido.substring(7);

        userEmail = jwtService.extractUserName(jwt);

        //segunda validacion validacion
        if (Objects.nonNull(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {

//            User user = new User(
//                    userEmail,
//                    "admin",
//                    true,
//                    true,
//                    true,
//                    true,
//                    List.of(new SimpleGrantedAuthority("ADMIN"))
//            );
//            UserPrincipal userPrincipal = new UserPrincipal();

            UserDetails userDetails = authService.loadUserByUsername(userEmail);

            if (jwtService.validateToken(jwt, userDetails)) {

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                securityContext.setAuthentication(authenticationToken);

                SecurityContextHolder.setContext(securityContext);
            }
        }

        filterChain.doFilter(request, response);
    }
}
