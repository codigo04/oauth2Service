package com.unit.oauth2Service.dto.request;

public class AuthRequestDTO {

    //@NotBlank(message = "Email is required.")
   // @Email(message = "Email must be valid.")
    private String username;

    ///@NotBlank(message = "Password is required.")
   // @Size(min = 8, max = 200,message = "The password must be at least 8 to 200 characters.")
    private String password;


    public AuthRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
