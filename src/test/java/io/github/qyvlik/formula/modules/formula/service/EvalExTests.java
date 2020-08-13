package io.github.qyvlik.formula.modules.formula.service;

import com.google.common.collect.Sets;
import com.udojava.evalex.Expression;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class EvalExTests {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void test_getUsedVariables() {
        Set<String> variableSet = Sets.newHashSet();
        variableSet.add("huobipro_ht_usdt");
        variableSet.add("okex_okb_usdt");

        Expression expression = new Expression("1 * huobipro_ht_usdt / okex_okb_usdt + okex_okb_usdt");
        List<String> variables = expression.getUsedVariables();
        logger.info("variables:{}", variables);
        for (String var : variables) {
            Assert.assertTrue(variableSet.contains(var));
        }
    }


    @Test
    public void test_getDeclaredFunctions() {
        Set<String> variableSet = Sets.newHashSet();
        variableSet.add("huobipro_ht_usdt");
        variableSet.add("okex_okb_usdt");

        Expression expression = new Expression("quit ()");
        Set<String> declaredFunctions = expression.getDeclaredFunctions();
        logger.info("declaredFunctions:{}", declaredFunctions);
//        for (String var : declaredFunctions) {
//            Assert.assertTrue(variableSet.contains(var));
//        }

        expression.eval();
    }
}
