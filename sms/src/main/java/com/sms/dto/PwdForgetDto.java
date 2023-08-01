package com.sms.dto;

import lombok.Data;

@Data
public class PwdForgetDto {
    private String username;
    private String verificationCode;
    private String newPwd;
}
