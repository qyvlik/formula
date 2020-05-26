package io.github.qyvlik.formula.modules.formula.graph;

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

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public Boolean getReverse() {
        return reverse;
    }

    public void setReverse(Boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public String toString() {
        return "RateInfo{" +
                "exchange='" + exchange + '\'' +
                ", baseCurrency='" + baseCurrency + '\'' +
                ", quoteCurrency='" + quoteCurrency + '\'' +
                ", reverse=" + reverse +
                '}';
    }
}
