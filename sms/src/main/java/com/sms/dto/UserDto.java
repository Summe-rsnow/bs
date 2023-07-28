package com.sms.dto;

import lombok.Data;

@Data
public class UserDto {
    String username;
    String password;
    String verificationCode;
}
