package io.github.qyvlik.formula;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import io.github.qyvlik.formula.common.base.ResponseObject;
import io.github.qyvlik.formula.modules.api.entity.DeleteVariablesRequest;
import io.github.qyvlik.formula.modules.api.entity.UpdateVariablesRequest;
import io.github.qyvlik.formula.modules.formula.entity.FormulaResult;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class FormulaApplicationTests {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private String token;

    @BeforeAll
    public void setup() {
        mockMvc = webAppContextSetup(this.wac).build();
        token = "ad82c6ae-f7a3-486b-b933-aa19104d8142";
    }

    @Test
    public void test000_get_variable_names_and_delete() throws Exception {
        String allVariableNames = this.mockMvc.perform(
                get("/api/v1/formula/variables/names")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject allVariableNamesResponseObj = JSON.parseObject(allVariableNames)
                .toJavaObject(ResponseObject.class);

        assertTrue(allVariableNamesResponseObj.getError() == null);
        List<String> variableNames = ((JSONArray) allVariableNamesResponseObj.getResult()).toJavaList(String.class);

        if (variableNames == null || variableNames.isEmpty()) {
            return;
        }

        logger.info("delete variable names:{}", variableNames);

        for (String variableName : variableNames) {
            String variableResponseString = this.mockMvc.perform(
                    get("/api/v1/formula/variable/" + variableName)
            ).andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
            ResponseObject variableResponseObj = JSON.parseObject(variableResponseString)
                    .toJavaObject(ResponseObject.class);

            assertTrue(variableResponseObj.getError() == null);

            assertTrue(variableResponseObj.getResult() != null);
        }


        DeleteVariablesRequest deleteVariablesRequest = new DeleteVariablesRequest();
        deleteVariablesRequest.setVariableNames(variableNames);

        String deleteVariablesResponseString = this.mockMvc.perform(
                post("/api/v1/formula/variables/delete")
                        .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .header("token", token)
                        .content(JSON.toJSONString(deleteVariablesRequest))
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject deleteVariablesResponseObj = JSON.parseObject(deleteVariablesResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(deleteVariablesResponseObj.getError() == null);
        assertTrue(deleteVariablesResponseObj.getResult().toString().equalsIgnoreCase("success"));
    }

    @Test
    public void test001_update_and_eval() throws Exception {

        long currentTimeMillis = System.currentTimeMillis();
        long timeout = 30 * 1000L;

        UpdateVariablesRequest request = new UpdateVariablesRequest();

        List<FormulaVariable> variables = Lists.newLinkedList();

        variables.add(new FormulaVariable(
                "huobipro_btc_usdt",
                new BigDecimal("10000"),
                currentTimeMillis,
                timeout
        ));

        variables.add(new FormulaVariable(
                "usd_in_cny",
                new BigDecimal("7.1"),
                currentTimeMillis,
                timeout
        ));

        request.setVariables(variables);

        String updateVariablesResponseString = this.mockMvc.perform(
                post("/api/v1/formula/variables/update")
                        .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .header("token", token)
                        .content(JSON.toJSONString(request))
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject registerResponseObj = JSON.parseObject(updateVariablesResponseString)
                .toJavaObject(ResponseObject.class);

        assertTrue(registerResponseObj.getError() == null);
        assertTrue(registerResponseObj.getResult().toString().equalsIgnoreCase("success"));

        String allVariableNames = this.mockMvc.perform(
                get("/api/v1/formula/variables/names")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject allVariableNamesResponseObj = JSON.parseObject(allVariableNames)
                .toJavaObject(ResponseObject.class);

        assertTrue(allVariableNamesResponseObj.getError() == null);
        List<String> variableNames = ((JSONArray) allVariableNamesResponseObj.getResult()).toJavaList(String.class);

        assertTrue(variableNames.contains("huobipro_btc_usdt"), "must contain huobipro_btc_usdt");
        assertTrue(variableNames.contains("usd_in_cny"), "must contain usd_in_cny");

        for (String variableName : variableNames) {
            String variableResponseString = this.mockMvc.perform(
                    get("/api/v1/formula/variable/" + variableName)
            ).andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
            ResponseObject variableResponseObj = JSON.parseObject(variableResponseString)
                    .toJavaObject(ResponseObject.class);

            assertTrue(variableResponseObj.getError() == null);
            assertTrue(variableResponseObj.getResult() != null);

            FormulaVariable formulaVariable =
                    ((JSONObject) variableResponseObj.getResult()).toJavaObject(FormulaVariable.class);
            assertTrue(formulaVariable.getValue().compareTo(BigDecimal.ZERO) > 0);
        }

        logger.info("variableNames:{}", variableNames);

        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/eval?formula=huobipro_btc_usdt*usd_in_cny")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(evalResponseObj.getError() == null);

        BigDecimal evalResult = new BigDecimal(evalResponseObj.getResult().toString());

        assertTrue(evalResult.compareTo(new BigDecimal("71000")) == 0, "evalResult is 71000");
    }

    @Test
    public void test002_get_variable_names_and_delete_without_token() throws Exception {
        String allVariableNames = this.mockMvc.perform(
                get("/api/v1/formula/variables/names")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject allVariableNamesResponseObj = JSON.parseObject(allVariableNames)
                .toJavaObject(ResponseObject.class);

        assertTrue(allVariableNamesResponseObj.getError() == null);
        List<String> variableNames = ((JSONArray) allVariableNamesResponseObj.getResult()).toJavaList(String.class);

        if (variableNames == null || variableNames.isEmpty()) {
            return;
        }

        logger.info("delete variable names:{}", variableNames);

        DeleteVariablesRequest deleteVariablesRequest = new DeleteVariablesRequest();
        deleteVariablesRequest.setVariableNames(variableNames);

        String deleteVariablesResponseString = this.mockMvc.perform(
                post("/api/v1/formula/variables/delete")
                        .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .content(JSON.toJSONString(deleteVariablesRequest))
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject deleteVariablesResponseObj = JSON.parseObject(deleteVariablesResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(deleteVariablesResponseObj.getError() != null);
    }

    @Test
    public void test003_update_and_eval_with_undefined() throws Exception {

        long currentTimeMillis = System.currentTimeMillis();
        long timeout = 30 * 1000L;

        UpdateVariablesRequest request = new UpdateVariablesRequest();

        List<FormulaVariable> variables = Lists.newLinkedList();

        variables.add(new FormulaVariable(
                "huobipro_btc_usdt",
                new BigDecimal("10000"),
                currentTimeMillis,
                timeout
        ));

        variables.add(new FormulaVariable(
                "usd_in_cny",
                new BigDecimal("7.1"),
                currentTimeMillis,
                timeout
        ));

        request.setVariables(variables);

        String updateVariablesResponseString = this.mockMvc.perform(
                post("/api/v1/formula/variables/update")
                        .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .header("token", token)
                        .content(JSON.toJSONString(request))
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject registerResponseObj = JSON.parseObject(updateVariablesResponseString)
                .toJavaObject(ResponseObject.class);

        assertTrue(registerResponseObj.getError() == null);
        assertTrue(registerResponseObj.getResult().toString().equalsIgnoreCase("success"));

        String allVariableNames = this.mockMvc.perform(
                get("/api/v1/formula/variables/names")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject allVariableNamesResponseObj = JSON.parseObject(allVariableNames)
                .toJavaObject(ResponseObject.class);

        assertTrue(allVariableNamesResponseObj.getError() == null);
        List<String> variableNames = ((JSONArray) allVariableNamesResponseObj.getResult()).toJavaList(String.class);

        assertTrue(variableNames.contains("huobipro_btc_usdt"), "must contain huobipro_btc_usdt");
        assertTrue(variableNames.contains("usd_in_cny"), "must contain usd_in_cny");

        for (String variableName : variableNames) {
            String variableResponseString = this.mockMvc.perform(
                    get("/api/v1/formula/variable/" + variableName)
            ).andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
            ResponseObject variableResponseObj = JSON.parseObject(variableResponseString)
                    .toJavaObject(ResponseObject.class);

            assertTrue(variableResponseObj.getError() == null);
            assertTrue(variableResponseObj.getResult() != null);

            FormulaVariable formulaVariable =
                    ((JSONObject) variableResponseObj.getResult()).toJavaObject(FormulaVariable.class);
            assertTrue(formulaVariable.getValue().compareTo(BigDecimal.ZERO) > 0);
        }

        logger.info("variableNames:{}", variableNames);

        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/eval?formula=huobipro_btc_usdt*usd_in_cny*i_am_undefined")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(evalResponseObj.getError() != null);
    }

    @Test
    public void test005_eval_expired_variable() throws Exception {
        long timeout = 31 * 1000L;
        long currentTimeMillis = System.currentTimeMillis() - timeout;

        UpdateVariablesRequest request = new UpdateVariablesRequest();

        List<FormulaVariable> variables = Lists.newLinkedList();

        variables.add(new FormulaVariable(
                "huobipro_btc_usdt",
                new BigDecimal("10000"),
                currentTimeMillis,
                timeout
        ));

        variables.add(new FormulaVariable(
                "usd_in_cny",
                new BigDecimal("7.1"),
                currentTimeMillis,
                timeout
        ));

        request.setVariables(variables);

        String updateVariablesResponseString = this.mockMvc.perform(
                post("/api/v1/formula/variables/update")
                        .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .header("token", token)
                        .content(JSON.toJSONString(request))
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject registerResponseObj = JSON.parseObject(updateVariablesResponseString)
                .toJavaObject(ResponseObject.class);

        assertTrue(registerResponseObj.getError() == null);
        assertTrue(registerResponseObj.getResult().toString().equalsIgnoreCase("success"));

        String allVariableNames = this.mockMvc.perform(
                get("/api/v1/formula/variables/names")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject allVariableNamesResponseObj = JSON.parseObject(allVariableNames)
                .toJavaObject(ResponseObject.class);

        assertTrue(allVariableNamesResponseObj.getError() == null);
        List<String> variableNames = ((JSONArray) allVariableNamesResponseObj.getResult()).toJavaList(String.class);

        assertTrue(variableNames.contains("huobipro_btc_usdt"), "must contain huobipro_btc_usdt");
        assertTrue(variableNames.contains("usd_in_cny"), "must contain usd_in_cny" );

        for (String variableName : variableNames) {
            String variableResponseString = this.mockMvc.perform(
                    get("/api/v1/formula/variable/" + variableName)
            ).andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
            ResponseObject variableResponseObj = JSON.parseObject(variableResponseString)
                    .toJavaObject(ResponseObject.class);

            assertTrue(variableResponseObj.getError() == null);
            assertTrue(variableResponseObj.getResult() != null);

            FormulaVariable formulaVariable =
                    ((JSONObject) variableResponseObj.getResult()).toJavaObject(FormulaVariable.class);
            assertTrue(formulaVariable.getValue().compareTo(BigDecimal.ZERO) > 0);
            logger.info("formulaVariable:{}", formulaVariable);
        }

        logger.info("variableNames:{}", variableNames);

        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/eval?formula=huobipro_btc_usdt*usd_in_cny")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(evalResponseObj.getError() != null);
        assertTrue(evalResponseObj.getError().getMessage().contains("expired"));
    }

    @Test
    public void test006_simple_formula() throws Exception {
        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/debug?formula=(1+1)*1.01")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(evalResponseObj.getError() == null);
        FormulaResult formulaResult = ((JSONObject) evalResponseObj.getResult()).toJavaObject(FormulaResult.class);
        logger.info("formulaResult:{}", formulaResult);
    }

    @Test
    public void test007_simple_formula() throws Exception {
        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/debug?formula=(1+1)*1.01-1")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(evalResponseObj.getError() == null);
        FormulaResult formulaResult = ((JSONObject) evalResponseObj.getResult()).toJavaObject(FormulaResult.class);
        logger.info("formulaResult:{}", formulaResult);
    }

    @Test
    public void test008_simple_formula() throws Exception {
        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/debug?formula=(1+1)*-1.01-1")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(evalResponseObj.getError() == null);
        FormulaResult formulaResult = ((JSONObject) evalResponseObj.getResult()).toJavaObject(FormulaResult.class);
        logger.info("formulaResult:{}", formulaResult);
    }

    @Test
    public void test009_black_keyword() throws Exception {
        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/debug?formula=while(1)")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(evalResponseObj.getError() != null);
        String errorMessage = evalResponseObj.getError().getMessage();
        assertTrue(
                errorMessage.contains("black keyword")
                        || errorMessage.equals("Unknown function while at character position 1")
        );
    }

    @Test
    public void test010_eval_quit() throws Exception {
        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/debug?formula=quit()")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(evalResponseObj.getError() != null);
        String errorMessage = evalResponseObj.getError().getMessage();
        assertTrue(
                errorMessage.contains("contains black keyword: `quit`")
                        || errorMessage.equals("Unknown function quit at character position 1"));
    }

    @Test
    public void test011_eval_exit() throws Exception {
        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/debug?formula=exit()")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(evalResponseObj.getError() != null);
        String errorMessage = evalResponseObj.getError().getMessage();
        assertTrue(
                errorMessage.contains("contains black keyword: `exit`")
                        || errorMessage.equals("Unknown function exit at character position 1"));
    }

    @Test
    public void test012_eval_scientific_notation() throws Exception {
        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/debug?formula=9.8e-7")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(evalResponseObj.getError() == null);
    }

    @Test
    public void test013_eval_math() throws Exception {
        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/debug?formula=Math.min(1, 2)")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(evalResponseObj.getError() != null);
    }

    @Test
    public void test014_get_alias() throws Exception {
        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/variables/alias")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(evalResponseObj.getError() == null);
    }

    @Test
    public void test015_eval_math_min() throws Exception {
        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/debug?formula=min(1, 2)")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(evalResponseObj.getError() == null);
    }

    @Test
    public void test016_update_and_eval_binance() throws Exception {

        long currentTimeMillis = System.currentTimeMillis();
        long timeout = 30 * 1000L;

        UpdateVariablesRequest request = new UpdateVariablesRequest();

        List<FormulaVariable> variables = Lists.newLinkedList();

        variables.add(new FormulaVariable(
                "binance_btc_usdt",
                new BigDecimal("10000"),
                currentTimeMillis,
                timeout
        ));

        variables.add(new FormulaVariable(
                "usd_in_cny",
                new BigDecimal("7.1"),
                currentTimeMillis,
                timeout
        ));

        request.setVariables(variables);

        String updateVariablesResponseString = this.mockMvc.perform(
                post("/api/v1/formula/variables/update")
                        .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .header("token", token)
                        .content(JSON.toJSONString(request))
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject registerResponseObj = JSON.parseObject(updateVariablesResponseString)
                .toJavaObject(ResponseObject.class);

        assertTrue(registerResponseObj.getError() == null);
        assertTrue(registerResponseObj.getResult().toString().equalsIgnoreCase("success"));

        String allVariableNames = this.mockMvc.perform(
                get("/api/v1/formula/variables/names")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject allVariableNamesResponseObj = JSON.parseObject(allVariableNames)
                .toJavaObject(ResponseObject.class);

        assertTrue(allVariableNamesResponseObj.getError() == null);
        List<String> variableNames = ((JSONArray) allVariableNamesResponseObj.getResult()).toJavaList(String.class);

        assertTrue(variableNames.contains("binance_btc_usdt"), "must contain binance_btc_usdt");
        assertTrue(variableNames.contains("usd_in_cny"), "must contain usd_in_cny");

        for (String variableName : variableNames) {
            String variableResponseString = this.mockMvc.perform(
                    get("/api/v1/formula/variable/" + variableName)
            ).andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
            ResponseObject variableResponseObj = JSON.parseObject(variableResponseString)
                    .toJavaObject(ResponseObject.class);

            assertTrue(variableResponseObj.getError() == null);
            assertTrue(variableResponseObj.getResult() != null);

            FormulaVariable formulaVariable =
                    ((JSONObject) variableResponseObj.getResult()).toJavaObject(FormulaVariable.class);
            assertTrue(formulaVariable.getValue().compareTo(BigDecimal.ZERO) > 0);
        }

        logger.info("variableNames:{}", variableNames);

        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/eval?formula=binance_btc_usdt*usd_in_cny")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        assertTrue(evalResponseObj.getError() == null);

        BigDecimal evalResult = new BigDecimal(evalResponseObj.getResult().toString());

        assertTrue(evalResult.compareTo(new BigDecimal("71000")) == 0, "evalResult is 71000");
    }

    @Test
    public void test017_update_and_convert() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        long timeout = 40 * 1000L;

        UpdateVariablesRequest request = new UpdateVariablesRequest();

        List<FormulaVariable> variables = Lists.newLinkedList();

        variables.add(new FormulaVariable(
                "binance_btc_usdt",
                new BigDecimal("10000"),
                currentTimeMillis,
                timeout
        ));

        variables.add(new FormulaVariable(
                "huobipro_btc_usdt",
                new BigDecimal("10000"),
                currentTimeMillis,
                timeout
        ));

        variables.add(new FormulaVariable(
                "binance_eth_usdt",
                new BigDecimal("1000"),
                currentTimeMillis,
                timeout
        ));

        request.setVariables(variables);


        String updateVariablesResponseString = this.mockMvc.perform(
                post("/api/v1/formula/variables/update")
                        .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .header("token", token)
                        .content(JSON.toJSONString(request))
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject registerResponseObj = JSON.parseObject(updateVariablesResponseString)
                .toJavaObject(ResponseObject.class);

        assertTrue(registerResponseObj.getError() == null);
        assertTrue(registerResponseObj.getResult().toString().equalsIgnoreCase("success"));

        String allVariableNames = this.mockMvc.perform(
                get("/api/v1/formula/variables/names")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject allVariableNamesResponseObj = JSON.parseObject(allVariableNames)
                .toJavaObject(ResponseObject.class);

        assertTrue(allVariableNamesResponseObj.getError() == null);
        List<String> variableNames = ((JSONArray) allVariableNamesResponseObj.getResult()).toJavaList(String.class);

        assertTrue(variableNames.contains("binance_btc_usdt"), "must contain binance_btc_usdt");
        assertTrue(variableNames.contains("binance_eth_usdt"), "must contain binance_eth_usdt");

        String convertResponseString = this.mockMvc.perform(
                get("/api/v1/formula/convert?from=btc&to=eth&value=1")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(convertResponseString)
                .toJavaObject(ResponseObject.class);
        if (evalResponseObj.getError() != null) {
            logger.error("test017_update_and_convert:{}", convertResponseString);
        }
        assertTrue(evalResponseObj.getError() == null);

        FormulaResult formulaResult = JSON.parseObject(evalResponseObj.getResult().toString()).toJavaObject(FormulaResult.class);

        BigDecimal evalResult = new BigDecimal(formulaResult.getResult());

        assertTrue(evalResult.compareTo(new BigDecimal("10")) == 0, "evalResult is 10");
    }

}
