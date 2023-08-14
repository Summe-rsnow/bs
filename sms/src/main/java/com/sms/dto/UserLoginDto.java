package com.sms.dto;

import lombok.Data;

/**
 * @author sssnow
 */
@Data
public class UserLoginDto {
    private String username;
    private String password;
    private String verificationCode;
    private boolean rememberMe;
}
