package com.example.ec.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginDto {
    @NotNull
    private String username;

    @NotNull
    private String password;

    private String firstName;

    private String lastName;

    public LoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
