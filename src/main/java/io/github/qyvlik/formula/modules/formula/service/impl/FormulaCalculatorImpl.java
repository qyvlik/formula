package io.github.qyvlik.formula.modules.formula.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import io.github.qyvlik.formula.modules.formula.entity.FormulaResult;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import io.github.qyvlik.formula.modules.formula.graph.RateEdge;
import io.github.qyvlik.formula.modules.formula.graph.RateInfo;
import io.github.qyvlik.formula.modules.formula.service.FormulaCalculator;
import io.github.qyvlik.formula.modules.formula.service.FormulaVariableService;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FormulaCalculatorImpl implements FormulaCalculator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();

    private final List<String> blackKeywords = ImmutableList.of(
            "while", "if", "for", "switch", "case",
            "try", "catch", "throw", "with",
            "function", "new", "delete",
            "var", "true", "false",

            "|", "&", "^", "!", "{", "}", "[", "]", "\"", "'", "\\", "=", ":", ";",

            "this",

            "length", "name", "apply", "bind", "call", "caller", "constructor", "hasOwnProperty",
            "isPrototypeOf", "propertyIsEnumerable", "valueOf",
            "__defineSetter__", "__defineSetter__", "__lookupGetter__", "__lookupSetter__", "__proto__",
            "toLocaleString", "toString",

            "join",

            "prototype", "global",
            "Array", "Number", "String", "Function", "Object",
            "Java", "java", "console", "eval",

            "print", "load", "loadWithNewGlobal", "javax.script", "javax",
            "script", "exit", "quit",

            "__FILE__", "__DIR__", "__LINE__",
            "undefined", "NaN", "Infinity", "arguments",
            "Math"
    );

    private final ThreadLocal<ScriptEngine> engineThreadLocal = new ThreadLocal<ScriptEngine>() {
        private final List<String> whiteVariableNames = ImmutableList.of("__FILE__", "__DIR__", "__LINE__",
                "undefined", "NaN", "Infinity", "arguments",
                "Math");

        @Override
        public ScriptEngine initialValue() {
            ScriptEngine engine = factory.getScriptEngine("-strict", "--no-java", "--no-syntax-extensions");

            ScriptObjectMirror engineBindings = (ScriptObjectMirror)
                    engine.getBindings(SimpleScriptContext.ENGINE_SCOPE);

            Set<String> variableNames = Sets.newHashSet(engineBindings.getOwnKeys(true));

            for (String variable : variableNames) {
                if (whiteVariableNames.contains(variable)) {
                    continue;
                }
                engineBindings.remove(variable);
            }

            return engine;
        }
    };

    private Map<String, String> aliasMap;
    private FormulaVariableService formulaVariableService;

    public Map<String, String> getAliasMap() {
        return aliasMap;
    }

    public void setAliasMap(Map<String, String> aliasMap) {
        this.aliasMap = aliasMap;
    }

    public FormulaVariableService getFormulaVariableService() {
        return formulaVariableService;
    }

    public void setFormulaVariableService(FormulaVariableService formulaVariableService) {
        this.formulaVariableService = formulaVariableService;
    }

    private ScriptEngine createScriptEngine() {
        return engineThreadLocal.get();
    }

    @Override
    public FormulaResult calculate(String formula) {
        if (StringUtils.isBlank(formula)) {
            throw new RuntimeException("calculate formula failure : formula is blank");
        }

        // 全部转小写，并去除空白字符
        formula = formula.toLowerCase().replaceAll("\\s+", "");


        StopWatch stopWatch = new StopWatch("calculate");

        stopWatch.start("replaceVariable");
        formula = replaceVariable(formula, getAliasMap());
        stopWatch.stop();

        Set<String> variableNames = getVariableNamesFromFormula(formula);

        for (String variable : variableNames) {
            validateVariable(formula, variable);
        }

        stopWatch.start("getFormulaVariableMap");
        Map<String, FormulaVariable> variableMap =
                formulaVariableService.getFormulaVariableMap(variableNames);
        stopWatch.stop();

        stopWatch.start("getScriptEngine");
        ScriptEngine engine = createScriptEngine();
        stopWatch.stop();

        stopWatch.start("eval");
        FormulaResult formulaResult = FormulaEval.eval(formula, engine, variableMap);
        stopWatch.stop();

        if (stopWatch.getTotalTimeMillis() > 100) {
            logger.debug("calculate : formula:{} {}", formula, stopWatch.prettyPrint());
        }

        formulaResult.setCost(stopWatch.getTotalTimeMillis());

        return formulaResult;
    }

    /**
     * small weight is shortest
     *
     * @param currency
     * @return
     */
    private double getWeight(String currency) {
        switch (currency.toUpperCase()) {
            case "USDT":
                return 100;
            case "BTC":
                return 200;
            case "ETH":
                return 300;
            case "KRW":
                return 400;
            case "HT":
                return 500;
            case "BNB":
                return 500;
            case "OKB":
                return 500;
            default:
                return 1000;
        }
    }

    @Override
    public FormulaResult convert(String from, String to, BigDecimal fromValue) {
        from = from.toLowerCase();
        to = to.toLowerCase();

        StopWatch stopWatch = new StopWatch("convert");

        stopWatch.start("getAllVariableNames");
        Set<String> names = formulaVariableService.getAllVariableNames();
        stopWatch.stop();

        stopWatch.start("getShortestPath");
        Map<String, RateEdge> edgeMap = Maps.newHashMap();

        for (String name : names) {
            name = name.toLowerCase();

            String[] nameArray = name.split("_");
            if (nameArray.length != 3) {
                continue;
            }
            // fiat rate
            if (name.contains("_in_")) {

                final String baseCurrency = nameArray[0];
                // in is nameArray[1]
                final String quoteCurrency = nameArray[2];

                if (!from.equals(baseCurrency) && !from.equals(quoteCurrency)
                        && !to.equals(baseCurrency) && !to.equals(quoteCurrency)) {
                    continue;
                }

                final double baseCurrencyWeight = getWeight(baseCurrency);
                final double quoteCurrencyWeight = getWeight(quoteCurrency);
                final double weight = Math.min(baseCurrencyWeight, quoteCurrencyWeight);

                {
                    String key = baseCurrency + "_" + quoteCurrency;
                    RateEdge rateEdge = edgeMap.computeIfAbsent(key,
                            k -> new RateEdge(key, weight, false));
                    RateInfo rateInfo = new RateInfo("FIAT_RATE", baseCurrency, quoteCurrency, false);
                    rateEdge.getRates().add(rateInfo);
                    rateEdge.setReverse(false);         // 只要有正向数据，就设置为 false
                }

                {
                    String reverseKey = quoteCurrency + "_" + baseCurrency;
                    RateEdge rateEdge = edgeMap.computeIfAbsent(reverseKey,
                            k -> new RateEdge(reverseKey, weight, true));
                    RateInfo rateInfo = new RateInfo("FIAT_RATE", quoteCurrency, baseCurrency, true);
                    rateEdge.getRates().add(rateInfo);
                }

            } else {
                String exchange = nameArray[0];
                String baseCurrency = nameArray[1];
                String quoteCurrency = nameArray[2];

                if (!from.equals(baseCurrency) && !from.equals(quoteCurrency)
                        && !to.equals(baseCurrency) && !to.equals(quoteCurrency)) {
                    continue;
                }

                final double baseCurrencyWeight = getWeight(baseCurrency);
                final double quoteCurrencyWeight = getWeight(quoteCurrency);
                final double weight = Math.min(baseCurrencyWeight, quoteCurrencyWeight);

                {
                    String key = baseCurrency + "_" + quoteCurrency;
                    RateEdge rateEdge = edgeMap.computeIfAbsent(key,
                            k -> new RateEdge(key, weight, false));
                    RateInfo rateInfo = new RateInfo(exchange, baseCurrency, quoteCurrency, false);
                    rateEdge.getRates().add(rateInfo);
                    rateEdge.setReverse(false);         // 只要有正向数据，就设置为 false
                }

                {
                    String reverseKey = quoteCurrency + "_" + baseCurrency;
                    RateEdge rateEdge = edgeMap.computeIfAbsent(reverseKey,
                            k -> new RateEdge(reverseKey, weight, false));
                    RateInfo rateInfo = new RateInfo(exchange, quoteCurrency, baseCurrency, true);
                    rateEdge.getRates().add(rateInfo);
                }
            }
        }
        DirectedGraph<String, RateEdge> graph = new DirectedOrderedSparseMultigraph<>();
        for (Map.Entry<String, RateEdge> entry : edgeMap.entrySet()) {
            RateEdge rateEdge = entry.getValue();
            graph.addEdge(
                    rateEdge,
                    rateEdge.getBaseCurrency(), rateEdge.getQuoteCurrency());
        }
        DijkstraShortestPath<String, RateEdge> decimalDijkstraShortestPath
                = new DijkstraShortestPath<>(graph, new Function<RateEdge, Number>() {
            @Override
            public Number apply(@Nullable RateEdge rateEdge) {
                // small weight is shortest
                return rateEdge.getWeight();
            }
        });
        List<RateEdge> path = decimalDijkstraShortestPath.getPath(from, to);

        stopWatch.stop();               // stop getShortestPath


        FormulaResult result = new FormulaResult();

        if (path == null || path.isEmpty()) {
            result.setCost(stopWatch.getTotalTimeMillis());
            result.setFormula(from + " -> " + to + ", fromValue:" + fromValue);
            result.setResult("");
            return result;
        }

        stopWatch.start("getFormulaVariableMap");
        Set<String> variableNames = Sets.newHashSet();
        List<RateInfo> rateInfoList = Lists.newArrayList();
        for (RateEdge edge : path) {
            RateInfo rateInfo = edge.getBestRateInfo();          // 获取一个最佳的汇率
            if (rateInfo == null) {
                throw new RuntimeException("convert failure : rateInfo is null");
            }

            if (rateInfo.getExchange().equals("FIAT_RATE")) {
                if (!rateInfo.getReverse()) {
                    variableNames.add(rateInfo.getBaseCurrency() + "_in_" + rateInfo.getQuoteCurrency());
                } else {
                    variableNames.add(rateInfo.getQuoteCurrency() + "_in_" + rateInfo.getBaseCurrency());
                }
            } else {
                if (!rateInfo.getReverse()) {
                    variableNames.add(rateInfo.getExchange() + "_" + rateInfo.getBaseCurrency() + "_" + rateInfo.getQuoteCurrency());
                } else {
                    variableNames.add(rateInfo.getExchange() + "_" + rateInfo.getQuoteCurrency() + "_" + rateInfo.getBaseCurrency());
                }
            }
            rateInfoList.add(rateInfo);
        }
        Map<String, FormulaVariable> variableMap = formulaVariableService.getFormulaVariableMap(variableNames);
        stopWatch.stop();           // stop getFormulaVariableMap


        stopWatch.start("convert");
        StringBuffer formulaBuffer = new StringBuffer(fromValue.toPlainString());
        BigDecimal resultValue = fromValue;
        for (int i = 0; i < rateInfoList.size(); i++) {
            RateInfo rateInfo = rateInfoList.get(i);
            String key = "";
            if (rateInfo.getExchange().equals("FIAT_RATE")) {
                if (!rateInfo.getReverse()) {
                    key = rateInfo.getBaseCurrency() + "_in_" + rateInfo.getQuoteCurrency();
                } else {
                    key = rateInfo.getQuoteCurrency() + "_in_" + rateInfo.getBaseCurrency();
                }
            } else {
                if (!rateInfo.getReverse()) {
                    key = rateInfo.getExchange()
                            + "_" + rateInfo.getBaseCurrency() + "_" + rateInfo.getQuoteCurrency();
                } else {
                    key = rateInfo.getExchange()
                            + "_" + rateInfo.getQuoteCurrency() + "_" + rateInfo.getBaseCurrency();
                }
            }
            FormulaVariable variable = variableMap.get(key);
            if (variable == null) {
                throw new RuntimeException("key not in variableMap, key :" + key);
            }

            if (!rateInfo.getReverse()) {
                formulaBuffer.append(" * ").append(key);
                resultValue = resultValue.multiply(variable.getValue());
            } else {
                formulaBuffer.append(" / ").append(key);
                resultValue = resultValue.divide(variable.getValue(), 12, BigDecimal.ROUND_DOWN);
            }
        }
        stopWatch.stop();           // stop convert

        result.setCost(stopWatch.getTotalTimeMillis());
        result.setResult(resultValue.setScale(12, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString());
        result.setFormula(formulaBuffer.toString());
        result.setContext(variableMap);

        return result;
    }

    private Set<String> getVariableNamesFromFormula(String formula) {
        String[] variables = formula.split("\\+|\\-|\\*|\\/|%|\\(|\\)|,");
        Set<String> names = Sets.newHashSet();
        for (String variableName : variables) {
            if (StringUtils.isBlank(variableName)) {
                continue;
            }
            if (variableName.startsWith("math.")) {
                continue;
            }
            if (variableName.startsWith("Math.")) {
                continue;
            }
            if (isNumeric(variableName)) {
                continue;
            }
            if (StringUtils.isNumeric(variableName.substring(0, 1))) {
                continue;
            }
            names.add(variableName);
        }
        return names;
    }

    private boolean isNumeric(String strNum) {
        try {
            new BigDecimal(strNum);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String replaceVariable(String formulaScript, Map<String, String> variableAliasMap) {
        // 替换数学公式
        formulaScript = handleScript(formulaScript);

        if (variableAliasMap == null || variableAliasMap.isEmpty()) {
            return formulaScript;
        }

        // 替换别名
        // from: okex_,  such as `okex_xxx_xxx`
        // to:   okex3_, such as `okex3_xxx_xxx`
        for (Map.Entry<String, String> entry : variableAliasMap.entrySet()) {
            formulaScript = formulaScript.replaceAll(entry.getKey(), entry.getValue());
        }

        return formulaScript;
    }

    private String handleScript(String script) {
        script = script.replaceAll("max\\(", "Math.max\\(");
        script = script.replaceAll("min\\(", "Math.min\\(");
        script = script.replaceAll("round\\(", "Math.round\\(");
        script = script.replaceAll("floor\\(", "Math.floor\\(");
        script = script.replaceAll("ceil\\(", "Math.ceil\\(");
        script = script.replaceAll("abs\\(", "Math.abs\\(");
        return script;
    }

    private void validateVariable(String formula, String variable) {
        for (String keyword : blackKeywords) {
            if (variable.equalsIgnoreCase(keyword.toLowerCase())) {
                throw new RuntimeException("validateVariable failure : formula:" + formula
                        + ", variable `" + variable
                        + "` contains black keyword: `" + keyword + "`");
            }
        }
    }
}
