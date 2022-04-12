package io.github.qyvlik.formula.modules.formula.model;

import com.google.common.collect.Lists;
import io.github.qyvlik.formula.modules.formula.cmd.CurrencyConvertCmd;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
public class CurrencyConvertResultData {
    /**
     * 原币种
     */
    private String source;
    /**
     * 目标币种
     */
    private String target;
    /**
     * 折算结果
     */
    private BigDecimal result;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 折算路径
     */
    private List<ProcessingPrice> path;

    private static String toNumStr(BigDecimal num) {
        if (num == null) {
            return "null";
        }
        return num.stripTrailingZeros().toPlainString();
    }

    public String toString() {
        if (result == null) {
            return "";
        }
        String amountStr = toNumStr(amount);
        String resultStr = toNumStr(result);
        List<String> array = Lists.newArrayList();
        for (ProcessingPrice priceVO : path) {
            String priceStr = priceVO.getPrice().stripTrailingZeros().toPlainString();
            String pair = priceVO.getMarket().getSymbol();
            String last = priceVO.getMarket().getPrice().stripTrailingZeros().toPlainString();
            array.add(priceVO.getSource() + "->" + priceVO.getTarget() + "(" + priceStr + ", " + pair + ":" + last + ")");
        }
        return amountStr + " " + source + " = " + resultStr + " " + target + ", " + String.join(" -> ", array);
    }

    public static CurrencyConvertResultData success(CurrencyConvertCmd cmd, BigDecimal price, List<ProcessingPrice> path) {
        CurrencyConvertResultData vo = new CurrencyConvertResultData();
        vo.setSource(cmd.getSource());
        vo.setTarget(cmd.getTarget());
        vo.setAmount(cmd.getAmount());
        vo.setPrice(price);
        BigDecimal result = price.multiply(cmd.getAmount()).setScale(cmd.getScale(), RoundingMode.DOWN);
        vo.setResult(result);
        vo.setPath(path);
        return vo;
    }


    public static CurrencyConvertResultData failure(CurrencyConvertCmd cmd) {
        CurrencyConvertResultData vo = new CurrencyConvertResultData();
        vo.setSource(cmd.getSource());
        vo.setTarget(cmd.getTarget());
        vo.setAmount(cmd.getAmount());
        vo.setPrice(null);
        vo.setResult(null);
        vo.setPath(null);
        return vo;
    }

}
