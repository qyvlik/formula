package io.github.qyvlik.formula.modules.formula.graph;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RateInfo {
    private String exchange;
    private String baseCurrency;
    private String quoteCurrency;
    private Boolean reverse;

    public RateInfo(String exchange, String baseCurrency, String quoteCurrency, Boolean reverse) {
        this.exchange = exchange;
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.reverse = reverse;
    }

    public RateInfo() {

    }
}
