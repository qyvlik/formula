package io.github.qyvlik.formula.modules.formula.service;

import io.github.qyvlik.formula.modules.formula.cmd.CurrencyConvertCmd;
import io.github.qyvlik.formula.modules.formula.model.CurrencyConvertResultData;

public interface CurrencyConverter {

    CurrencyConvertResultData currencyConvert(CurrencyConvertCmd cmd);
}
