package com.sms.entity;

import lombok.Data;

/**
 * @author sssnow
 */
@Data
public class Notice {
    private Integer[] grants;
    private Integer hours;
    private String title;
    private String text;
}
