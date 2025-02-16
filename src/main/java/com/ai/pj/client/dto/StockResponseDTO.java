package com.ai.pj.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResponseDTO {
    @JsonProperty("predicted_value")
    private Double predictedValue;
    @JsonProperty("safety_stock")
    private Double safetyStock;
    @JsonProperty("proper_stock")
    private Double properStock;
    @JsonProperty("precaution_comment")
    private String precautionComment;
}
