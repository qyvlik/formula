package io.github.qyvlik.formula.modules.formula.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import io.github.qyvlik.formula.modules.formula.service.FormulaVariableService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service("formulaVariableService")
public class FormulaVariableServiceImpl implements FormulaVariableService {

    public final String FORMULA_VARIABLE_PREFIX = "formula.var:";
    public final String FORMULA_VARIABLE_NAMES = "formula.names";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Map<String, FormulaVariable> getFormulaVariableMap(Set<String> variableNames) {
        Set<String> fullVariableNames = Sets.newHashSet();
        for (String name : variableNames) {
            fullVariableNames.add(FORMULA_VARIABLE_PREFIX + name);
        }

        long currentTimeMillis = System.currentTimeMillis();

        Map<String, FormulaVariable> variableMap = Maps.newHashMap();

        for (String fullVariableName : fullVariableNames) {
            // multiGet not work in redis-cluster
            String value = redisTemplate.opsForValue().get(fullVariableName);
            if (StringUtils.isBlank(value)) {
                throw new RuntimeException("variable "
                        + fullVariableName.replaceAll(FORMULA_VARIABLE_PREFIX, "") + " not exist");
            }
            FormulaVariable formulaVariable = JSON.parseObject(value).toJavaObject(FormulaVariable.class);

            // variable is timeout
            if (formulaVariable.getTimestamp() + formulaVariable.getTimeout() < currentTimeMillis) {
                throw new RuntimeException("variable "
                        + fullVariableName.replaceAll(FORMULA_VARIABLE_PREFIX, "") + " expired");
            }

            variableMap.put(formulaVariable.getName(), formulaVariable);
        }

        return variableMap;
    }

    @Override
    public FormulaVariable getFormulaVariable(String variableName) {
        String value = redisTemplate.opsForValue().get(FORMULA_VARIABLE_PREFIX + variableName);
        return JSON.parseObject(value).toJavaObject(FormulaVariable.class);
    }

    @Override
    public void updateFormulaVariable(FormulaVariable formulaVariable) {
        formulaVariable.setName(formulaVariable.getName().toLowerCase());                   // to lowercase

        String fullKey = FORMULA_VARIABLE_PREFIX + formulaVariable.getName();
        redisTemplate.opsForValue().set(fullKey, JSON.toJSONString(formulaVariable));
        redisTemplate.opsForSet().add(FORMULA_VARIABLE_NAMES, formulaVariable.getName());
    }

    @Override
    public void deleteFormulaVariable(String variableName) {
        variableName = variableName.toLowerCase();

        redisTemplate.delete(FORMULA_VARIABLE_PREFIX + variableName);
        redisTemplate.opsForSet().remove(FORMULA_VARIABLE_NAMES, variableName);
    }

    // todo use scan
    @Override
    public Set<String> getAllVariableNames() {
        return redisTemplate.opsForSet().members(FORMULA_VARIABLE_NAMES);
    }

}
