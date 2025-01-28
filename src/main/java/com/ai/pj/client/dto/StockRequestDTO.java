package com.ai.pj.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockRequestDTO {
    private String date;
    private String location;
    private String categoryName;
}
