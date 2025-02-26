package com.unit.oauth2Service.model;



import com.unit.oauth2Service.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class UserPrincipal implements OAuth2User, UserDetails {

    private  UUID id;
    private UUID profileId;
    private UUID advertiserId;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;


    public UserPrincipal(UUID id, UUID profileId, UUID advertiserId, String username, String password, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        this.id = id;
        this.profileId = profileId;
        this.advertiserId = advertiserId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.attributes = attributes;
    }


    public UserPrincipal(UserEntity userEntity, Map<String, Object> attributes) {
        this.id = userEntity.getId();
      //  this.profileId = user.getProfile() != null ? user.getProfile().getId() : null;
      //  this.advertiserId = user.getAdvertiser() != null ? user.getAdvertiser().getId() : null;
        this.username = userEntity.getUsername();
        this.password = userEntity.getPassword();
        this.authorities = userEntity.getAuthorities();
        this.attributes = attributes;
    }


    public UserPrincipal() {
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return username;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public UUID getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(UUID advertiserId) {
        this.advertiserId = advertiserId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
