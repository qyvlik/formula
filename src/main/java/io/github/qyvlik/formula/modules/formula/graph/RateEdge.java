package io.github.qyvlik.formula.modules.formula.graph;

import com.google.common.collect.Lists;

import java.util.List;

public class RateEdge {
    private String baseCurrency;
    private String quoteCurrency;
    private Double weight;
    private Boolean reverse;
    private List<RateInfo> rates;

    public RateEdge() {
    }

    public RateEdge(String symbol, Double weight, Boolean reverse) {
        String[] symbolArray = symbol.split("_");
        this.baseCurrency = symbolArray[0];
        this.quoteCurrency = symbolArray[1];
        this.weight = weight;
        this.reverse = reverse;
        this.rates = Lists.newArrayList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RateEdge)) return false;

        RateEdge rateEdge = (RateEdge) o;

        if (getBaseCurrency() != null ? !getBaseCurrency().equals(rateEdge.getBaseCurrency()) : rateEdge.getBaseCurrency() != null)
            return false;
        return getQuoteCurrency() != null ? getQuoteCurrency().equals(rateEdge.getQuoteCurrency()) : rateEdge.getQuoteCurrency() == null;
    }

    @Override
    public int hashCode() {
        int result = getBaseCurrency() != null ? getBaseCurrency().hashCode() : 0;
        result = 31 * result + (getQuoteCurrency() != null ? getQuoteCurrency().hashCode() : 0);
        return result;
    }

    public RateInfo getBestRateInfo() {
        if (this.getRates() == null || this.getRates().isEmpty()) {
            return null;
        }
        // todo weight for exchange
        for (RateInfo take : this.getRates()) {
            if (take != null && !take.getReverse()) {
                return take;
            }
        }
        return this.getRates().get(0);
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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Boolean getReverse() {
        return reverse;
    }

    public void setReverse(Boolean reverse) {
        this.reverse = reverse;
    }

    public List<RateInfo> getRates() {
        return rates;
    }

    public void setRates(List<RateInfo> rates) {
        this.rates = rates;
    }

    @Override
    public String toString() {
        return "RateEdge{" +
                "baseCurrency='" + baseCurrency + '\'' +
                ", quoteCurrency='" + quoteCurrency + '\'' +
                ", rates=" + rates +
                '}';
    }
}
