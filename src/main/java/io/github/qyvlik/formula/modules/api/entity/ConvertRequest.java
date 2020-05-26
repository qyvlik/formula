package io.github.qyvlik.formula.modules.api.entity;

import java.math.BigDecimal;

public class ConvertRequest {
    private String from;
    private String to;
    private BigDecimal value;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
