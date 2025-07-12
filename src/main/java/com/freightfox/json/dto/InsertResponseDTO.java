package com.freightfox.json.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InsertResponseDTO {
    private String message;
    private String dataset;
    private long recordId;
}
