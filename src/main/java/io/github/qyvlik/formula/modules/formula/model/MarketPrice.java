package io.github.qyvlik.formula.modules.formula.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarketPrice {
    /**
     * 交易所
     */
    private String exchange;
    /**
     * 交易代码
     */
    private String code;
    /**
     * 交易对
     */
    private String symbol;
    /**
     * 基础币种
     */
    private String base;
    /**
     * 计价币种
     */
    private String quote;
    /**
     * 最新价格, todo last
     */
    private BigDecimal price;
    /**
     * 最新价格时间
     */
    private Long timestamp;
}
