package com.sms.dto;

import lombok.Data;

@Data
public class SetNoticeDto {
    private Integer[] grants;
    private Integer hours;
    private String title;
    private String text;
}
