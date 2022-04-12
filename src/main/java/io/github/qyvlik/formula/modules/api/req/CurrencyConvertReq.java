package io.github.qyvlik.formula.modules.api.req;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CurrencyConvertReq {
    /**
     * 原币种
     */
    @NotBlank
    @NotNull
    @Size(max = 64)
    private String source;
    /**
     * 目标币种
     */
    @NotBlank
    @NotNull
    @Size(max = 64)
    private String target;
    /**
     * 金额
     */
    @NotNull
    @Digits(integer = 18, fraction = 18)
    private BigDecimal amount;
    /**
     * 保留精度
     */
    private Integer scale;
    /**
     * 优先级-中间币种
     */
    @Size(max = 16)
    private List<@NotNull @NotBlank @Size(max = 64) String> middles;
    /**
     * 优先级-交易所
     */
    @Size(max = 16)
    private List<@NotNull @NotBlank @Size(max = 64) String> exchanges;
}
