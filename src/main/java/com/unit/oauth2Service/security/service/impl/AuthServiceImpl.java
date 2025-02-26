package com.unit.oauth2Service.security.service.impl;

import com.unit.oauth2Service.entity.UserEntity;
import com.unit.oauth2Service.model.UserPrincipal;
import com.unit.oauth2Service.dto.request.AuthRequestDTO;
import com.unit.oauth2Service.dto.response.AuthResponseDTO;
import com.unit.oauth2Service.repository.RolRepository;
import com.unit.oauth2Service.repository.UserRepository;
import com.unit.oauth2Service.security.oauth2.userInfo.OAuth2UserInfo;
import com.unit.oauth2Service.security.oauth2.userInfo.OAuth2UserInfoFactory;
import com.unit.oauth2Service.security.service.AuthService;
import com.unit.oauth2Service.security.service.JwtService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
public class AuthServiceImpl extends DefaultOAuth2UserService implements AuthService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository;


    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(@Lazy AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO requestDTO) throws BadRequestException {

        Authentication authentication = authenticationManager.authenticate(
                this.authenticate(requestDTO.getUsername(), requestDTO.getPassword())
        );

        //autenticamosal contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new AuthResponseDTO(jwtService.generateToken((UserDetails) authentication.getPrincipal()));
    }

    public Authentication authenticate(String username, String password) throws BadRequestException {
        UserDetails userDetails = loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadRequestException("Invalid Username or Password");
        }

        if (!new BCryptPasswordEncoder().matches(password, userDetails.getPassword())) {
            throw new BadRequestException("Invalid Password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());


    }

    @Override
    public AuthResponseDTO register(AuthRequestDTO requestDTO) {

        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        userRepository.save(new UserEntity(
                requestDTO.getUsername(),
                new BCryptPasswordEncoder().encode("admin"),
                true,
                true,
                true,
                true,
                false,
                false,
                Set.of(rolRepository.findByRoleName("USER"))));

        try {
            return login(requestDTO);
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            return processOAuth2User(userRequest, super.loadUser(userRequest));
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(
                    "The email does not match any account");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserPrincipal(userRepository.findByUsername(username), null);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest request, OAuth2User user) {

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(request.getClientRegistration().getRegistrationId(), user.getAttributes());

        System.out.println(user.getName());
        System.out.println(user.getAttributes());

        if (userInfo.getEmail() == null || userInfo.getEmail().isBlank()) {
            return null;  // O manejar la excepción según el caso
        }

        if (!userRepository.existsByUsername(userInfo.getEmail())) {
            //si no existe
            UserEntity userEntity = userRepository.save(new UserEntity(
                    userInfo.getEmail(),
                    new BCryptPasswordEncoder().encode("google"),
                    true,
                    true,
                    true,
                    true,
                    userInfo.getEmailVerified(),
                    true,
                    Set.of(rolRepository.findByRoleName("USER"))
            ));

            return new UserPrincipal(userEntity, user.getAttributes());
        }

        //si existe guardamos a la bd
        return new UserPrincipal(userRepository.findByUsername(userInfo.getEmail()), user.getAttributes());
    }


}
