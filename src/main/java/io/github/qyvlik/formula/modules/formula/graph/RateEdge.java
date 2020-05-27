package io.github.qyvlik.formula.modules.formula.graph;

import com.google.common.collect.Lists;

import java.util.Comparator;
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

    public static int weightOfExchange(String exchange) {
        switch (exchange.toLowerCase()) {
            case "huobipro":
                return 100;
            case "binance":
                return 90;
            case "okex":
                return 80;
            case "kraken":
                return 70;
            case "upbit":
                return 60;
            default:
                return 10;
        }
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
        List<RateInfo> unReverseList = Lists.newArrayList();
        List<RateInfo> reverseList = Lists.newArrayList();
        for (RateInfo take : this.getRates()) {
            if (take == null) {
                continue;
            }
            if (!take.getReverse()) {
                unReverseList.add(take);
            } else {
                reverseList.add(take);
            }
        }

        Comparator<RateInfo> comparator = new Comparator<RateInfo>() {
            @Override
            public int compare(RateInfo o1, RateInfo o2) {
                Integer o1Weight = weightOfExchange(o1.getExchange());
                Integer o2Weight = weightOfExchange(o2.getExchange());
                // DESC
                return o2Weight.compareTo(o1Weight);
            }
        };

        if (!unReverseList.isEmpty()) {
            unReverseList.sort(comparator);
            return unReverseList.get(0);
        }

        if (!reverseList.isEmpty()) {
            reverseList.sort(comparator);
            return reverseList.get(0);
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
