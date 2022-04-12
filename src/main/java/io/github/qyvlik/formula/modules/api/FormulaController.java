package io.github.qyvlik.formula.modules.api;

import com.google.common.collect.Lists;
import io.github.qyvlik.formula.common.base.Code;
import io.github.qyvlik.formula.common.base.Result;
import io.github.qyvlik.formula.modules.api.req.CalculateFormulaReq;
import io.github.qyvlik.formula.modules.api.req.CurrencyConvertReq;
import io.github.qyvlik.formula.modules.api.req.UpdateMarketPriceReq;
import io.github.qyvlik.formula.modules.formula.cmd.CurrencyConvertCmd;
import io.github.qyvlik.formula.modules.formula.model.CalculateContext;
import io.github.qyvlik.formula.modules.formula.model.CalculateResultData;
import io.github.qyvlik.formula.modules.formula.model.CurrencyConvertResultData;
import io.github.qyvlik.formula.modules.formula.model.MarketPrice;
import io.github.qyvlik.formula.modules.formula.service.CurrencyConverter;
import io.github.qyvlik.formula.modules.formula.service.VariableService;
import io.github.qyvlik.formula.modules.formula.service.impl.FormulaCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Slf4j
@RestController
public class FormulaController {

    @Autowired
    private VariableService variableService;
    @Autowired
    private CurrencyConverter currencyConverter;

    @PostMapping(value = "api/v1/formula/market-price/update")
    public Result<Long> updateMarketPrice(@RequestBody UpdateMarketPriceReq req) {
        MarketPrice marketPrice = new MarketPrice();
        BeanUtils.copyProperties(req, marketPrice);
        variableService.updateMarketPrice(marketPrice);
        return Result.success(null);
    }

    @GetMapping(value = "api/v1/formula/market-price/list")
    public Result<List<MarketPrice>> getMarketPriceList() {
        return null;
    }

    @GetMapping(value = "api/v1/formula/market-price/info")
    public Result<MarketPrice> getMarketPriceInfo(@RequestParam @NotBlank @Size(max = 64) String exchange,
                                                  @RequestParam @NotBlank @Size(max = 64) String base,
                                                  @RequestParam @NotBlank @Size(max = 64) String quote) {
        MarketPrice marketPrice = variableService.getMarketPriceByExchangePriority(
                base, quote, Lists.newArrayList(exchange));
        if (marketPrice != null) {
            return Result.success(marketPrice);
        }
        return Result.failure(Code.NOT_FOUND);
    }

    @PostMapping(value = "api/v1/formula/calculate")
    public Result<CalculateResultData> formulaCalculate(@RequestBody CalculateFormulaReq req) {
        FormulaCalculator calculator = new FormulaCalculator();
        CalculateContext context = new CalculateContext(variableService);
        CalculateResultData resultData = calculator.calculate(req.getFormula(), context);
        return Result.success(resultData);
    }

    @PostMapping(value = "api/v1/formula/convert")
    public Result<CurrencyConvertResultData> convert(@RequestBody CurrencyConvertReq req) {
        CurrencyConvertCmd cmd = new CurrencyConvertCmd();
        BeanUtils.copyProperties(req, cmd);
        cmd.setMiddles(req.getMiddles());
        cmd.setExchanges(req.getExchanges());
        CurrencyConvertResultData resultData = currencyConverter.currencyConvert(cmd);
        if (resultData.getResult() == null) {
            return Result.failure(Code.NOT_FOUND);
        }
        return Result.success(resultData);
    }
}
