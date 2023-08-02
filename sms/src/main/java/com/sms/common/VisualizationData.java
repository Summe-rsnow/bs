package com.sms.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VisualizationData implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long value;
    private String name;
}
