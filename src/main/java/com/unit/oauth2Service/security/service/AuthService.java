package com.unit.oauth2Service.security.service;

import com.unit.oauth2Service.dto.request.AuthRequestDTO;
import com.unit.oauth2Service.dto.response.AuthResponseDTO;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;

public interface AuthService  extends UserDetailsService  {
    AuthResponseDTO login(AuthRequestDTO requestDTO) throws BadRequestException;
    AuthResponseDTO register(AuthRequestDTO requestDTO);


}
