package io.github.qyvlik.formula.modules.formula.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class FormulaVariable implements Serializable {
    private String name;
    private BigDecimal value;
    private Long timestamp;             // create-time, unit is ms
    private Long timeout;               // timeout, unit is ms

    public FormulaVariable() {
    }

    public FormulaVariable(FormulaVariable other) {
        this.name = other.getName();
        this.value = other.getValue();
        this.timestamp = other.getTimestamp();
        this.timeout = other.getTimeout();
    }

    public FormulaVariable(String name, BigDecimal value, Long timestamp, Long timeout) {
        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
        this.timeout = timeout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "(" + name + "=" + value + ")";
    }
}

