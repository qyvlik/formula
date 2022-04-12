package io.github.qyvlik.formula.modules.formula.cmd;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CurrencyConvertCmd {
    /**
     * 原币种
     */
    private String source;
    /**
     * 目标币种
     */
    private String target;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 保留精度
     */
    private Integer scale;
    /**
     * 优先级-中间币种
     */
    private List<String> middles;
    /**
     * 优先级-交易所
     */
    private List<String> exchanges;
}
