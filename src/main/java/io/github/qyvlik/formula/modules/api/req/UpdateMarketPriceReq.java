package io.github.qyvlik.formula.modules.api.req;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class UpdateMarketPriceReq {
    /**
     * 交易所
     */
    @NotBlank
    @NotNull
    @Size(max = 64)
    private String exchange;
    /**
     * 交易代码
     */
    @NotBlank
    @NotNull
    @Size(max = 128)
    private String code;
    /**
     * 基础币种
     */
    @NotBlank
    @NotNull
    @Size(max = 64)
    private String base;
    /**
     * 计价币种
     */
    @NotBlank
    @NotNull
    @Size(max = 64)
    private String quote;
    /**
     * 最新价格, todo last
     */
    @NotNull
    @DecimalMin("0")
    private BigDecimal price;
    /**
     * 最新价格时间
     */
    @NotNull
    private Long timestamp;
}
