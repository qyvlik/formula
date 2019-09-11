package io.github.qyvlik.formula.modules.api;

import io.github.qyvlik.formula.common.base.ResponseObject;
import io.github.qyvlik.formula.common.properties.FormulaProperties;
import io.github.qyvlik.formula.modules.api.entity.DeleteVariablesRequest;
import io.github.qyvlik.formula.modules.api.entity.UpdateVariablesRequest;
import io.github.qyvlik.formula.modules.formula.entity.FormulaResult;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import io.github.qyvlik.formula.modules.formula.service.FormulaCalculator;
import io.github.qyvlik.formula.modules.formula.service.impl.FormulaVariableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
public class FormulaController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FormulaVariableService formulaVariableService;
    @Autowired
    private FormulaCalculator formulaCalculator;
    @Autowired
    private FormulaProperties formulaProperties;

    @RequestMapping(value = "api/v1/formula/variables/update", method = RequestMethod.POST)
    public ResponseObject<String> updateVariables(@RequestBody UpdateVariablesRequest request) {
        if (request.getVariables() == null || request.getVariables().size() == 0) {
            return new ResponseObject<>(20400, "variables is empty");
        }

        int count = 0;
        for (FormulaVariable formulaVariable : request.getVariables()) {
            try {
                formulaVariableService.updateFormulaVariable(formulaVariable);
                count++;
            } catch (Exception e) {
                logger.debug("updateVariable failure name:{}, error:{}",
                        formulaVariable.getName(), e.getMessage());
            }
        }

        return new ResponseObject<>(count == request.getVariables().size() ? "success" : "failure");
    }

    @RequestMapping(value = "api/v1/formula/variables/delete", method = RequestMethod.POST)
    public ResponseObject<String> deleteVariables(@RequestBody DeleteVariablesRequest request) {
        if (request.getVariableNames() == null || request.getVariableNames().size() == 0) {
            return new ResponseObject<>(20400, "variableNames is empty");
        }

        int count = 0;
        for (String variableName : request.getVariableNames()) {
            try {
                formulaVariableService.deleteFormulaVariable(variableName);
                count++;
            } catch (Exception e) {
                logger.debug("deleteVariables failure name:{}, error:{}",
                        variableName, e.getMessage());
            }
        }

        return new ResponseObject<>(count == request.getVariableNames().size() ? "success" : "failure");
    }

    @RequestMapping(value = "api/v1/formula/variables/names", method = RequestMethod.GET)
    public ResponseObject<Set<String>> getFormulaVariableNames() {
        return new ResponseObject<>(formulaVariableService.getAllVariableNames());
    }

    @RequestMapping(value = "api/v1/formula/variables/alias", method = RequestMethod.GET)
    public ResponseObject<Map<String, String>> getFormulaVariableAlias() {
        try {
            return new ResponseObject<>(formulaProperties.getVariableAlias());
        } catch (Exception e) {
            logger.debug("getFormulaVariableAlias failure error:{}", e.getMessage());
            return new ResponseObject<>(20500, e.getMessage());
        }
    }

    @RequestMapping(value = "api/v1/formula/variable/{variableName}", method = RequestMethod.GET)
    public ResponseObject<FormulaVariable> getFormulaVariable(@PathVariable String variableName) {
        try {
            FormulaVariable formulaVariable = formulaVariableService.getFormulaVariable(variableName);
            return new ResponseObject<>(formulaVariable);
        } catch (Exception e) {
            logger.debug("getFormulaVariable failure name:{}, error:{}", variableName, e.getMessage());
            return new ResponseObject<>(20500, e.getMessage());
        }
    }

    @RequestMapping(value = "api/v1/formula/eval", method = RequestMethod.GET)
    public ResponseObject<String> eval(@RequestParam String formula) {
        try {
            FormulaResult formulaResult = formulaCalculator.calculate(formula);

            return new ResponseObject<>(formulaResult.getFormula());
        } catch (Exception e) {
            return new ResponseObject<>(20500, e.getMessage());
        }
    }

    @RequestMapping(value = "api/v1/formula/debug", method = RequestMethod.GET)
    public ResponseObject<FormulaResult> debug(@RequestParam String formula) {
        try {
            FormulaResult formulaResult = formulaCalculator.calculate(formula);

            return new ResponseObject<>(formulaResult);
        } catch (Exception e) {
            return new ResponseObject<>(20500, e.getMessage());
        }
    }


}
