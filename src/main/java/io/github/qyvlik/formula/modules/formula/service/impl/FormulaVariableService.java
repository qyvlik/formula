package io.github.qyvlik.formula.modules.formula.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class FormulaVariableService {

    public final String FORMULA_VARIABLE_PREFIX = "formula.var:";
    public final String FORMULA_VARIABLE_NAMES = "formula.names";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Map<String, FormulaVariable> getFormulaVariableMap(Set<String> variableNames) {
        Set<String> fullVariableNames = Sets.newHashSet();
        for (String name : variableNames) {
            fullVariableNames.add(FORMULA_VARIABLE_PREFIX + name);
        }

        Map<String, FormulaVariable> variableMap = Maps.newHashMap();

        for (String fullVariableName : fullVariableNames) {
            // multiGet 对于 redis-cluster 不适用
            String value = redisTemplate.opsForValue().get(fullVariableName);
            if (StringUtils.isBlank(value)) {
                continue;
            }
            FormulaVariable formulaVariable = JSON.parseObject(value).toJavaObject(FormulaVariable.class);
            variableMap.put(formulaVariable.getName(), formulaVariable);
        }

        return variableMap;
    }

    public FormulaVariable getFormulaVariable(String variableName) {
        String value = redisTemplate.opsForValue().get(FORMULA_VARIABLE_PREFIX + variableName);
        return JSON.parseObject(value).toJavaObject(FormulaVariable.class);
    }

    public void updateFormulaVariable(FormulaVariable formulaVariable) {
        formulaVariable.setName(formulaVariable.getName().toLowerCase());                   // to lowercase

        String fullKey = FORMULA_VARIABLE_PREFIX + formulaVariable.getName();
        redisTemplate.opsForValue().set(fullKey, JSON.toJSONString(formulaVariable));
        redisTemplate.opsForSet().add(FORMULA_VARIABLE_NAMES, formulaVariable.getName());
    }

    public void deleteFormulaVariable(String variableName) {
        variableName = variableName.toLowerCase();

        redisTemplate.delete(FORMULA_VARIABLE_PREFIX + variableName);
        redisTemplate.opsForSet().remove(FORMULA_VARIABLE_NAMES, variableName);
    }

    public Set<String> getAllVariableNames() {
        return redisTemplate.opsForSet().members(FORMULA_VARIABLE_NAMES);
    }

}
