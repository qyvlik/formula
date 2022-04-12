package io.github.qyvlik.formula.modules.formula.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ProcessingPrice {
    private MarketPrice market;
    private String source;
    private String target;
    private BigDecimal price;
}
