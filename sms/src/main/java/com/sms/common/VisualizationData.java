package com.sms.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class VisualizationData implements Serializable {
    private Long Value;
    private String name;
}
