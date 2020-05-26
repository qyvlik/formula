package io.github.qyvlik.formula.modules.formula.graph;

import java.util.List;

public class RateEdge {
    private String baseCurrency;
    private String quoteCurrency;
    private List<RateInfo> rates;

    public RateEdge() {
    }

    public RateEdge(String baseCurrency, String quoteCurrency, List<RateInfo> rates) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.rates = rates;
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
