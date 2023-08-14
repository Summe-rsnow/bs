package com.sms.dto;

import lombok.Data;

/**
 * @author sssnow
 */
@Data
public class PwdForgetDto {
    private String username;
    private String verificationCode;
    private String newPwd;
}
