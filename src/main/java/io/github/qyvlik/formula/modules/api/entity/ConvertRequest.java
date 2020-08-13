package io.github.qyvlik.formula.modules.api.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ConvertRequest {
    private String from;
    private String to;
    private BigDecimal value;
}
