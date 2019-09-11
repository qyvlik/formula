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
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FormulaApplicationTests {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private String token;

    @Before
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

        Assert.assertTrue(allVariableNamesResponseObj.getError() == null);
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

            Assert.assertTrue(variableResponseObj.getError() == null);

            Assert.assertTrue(variableResponseObj.getResult() != null);
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
        Assert.assertTrue(deleteVariablesResponseObj.getError() == null);
        Assert.assertTrue(deleteVariablesResponseObj.getResult().toString().equalsIgnoreCase("success"));
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

        Assert.assertTrue(registerResponseObj.getError() == null);
        Assert.assertTrue(registerResponseObj.getResult().toString().equalsIgnoreCase("success"));

        String allVariableNames = this.mockMvc.perform(
                get("/api/v1/formula/variables/names")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject allVariableNamesResponseObj = JSON.parseObject(allVariableNames)
                .toJavaObject(ResponseObject.class);

        Assert.assertTrue(allVariableNamesResponseObj.getError() == null);
        List<String> variableNames = ((JSONArray) allVariableNamesResponseObj.getResult()).toJavaList(String.class);

        Assert.assertTrue("must contain huobipro_btc_usdt", variableNames.contains("huobipro_btc_usdt"));
        Assert.assertTrue("must contain usd_in_cny", variableNames.contains("usd_in_cny"));

        for (String variableName : variableNames) {
            String variableResponseString = this.mockMvc.perform(
                    get("/api/v1/formula/variable/" + variableName)
            ).andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
            ResponseObject variableResponseObj = JSON.parseObject(variableResponseString)
                    .toJavaObject(ResponseObject.class);

            Assert.assertTrue(variableResponseObj.getError() == null);
            Assert.assertTrue(variableResponseObj.getResult() != null);

            FormulaVariable formulaVariable =
                    ((JSONObject) variableResponseObj.getResult()).toJavaObject(FormulaVariable.class);
            Assert.assertTrue(formulaVariable.getValue().compareTo(BigDecimal.ZERO) > 0);
        }

        logger.info("variableNames:{}", variableNames);

        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/eval?formula=huobipro_btc_usdt*usd_in_cny")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        Assert.assertTrue(evalResponseObj.getError() == null);

        BigDecimal evalResult = new BigDecimal(evalResponseObj.getResult().toString());

        Assert.assertTrue("evalResult is 71000", evalResult.compareTo(new BigDecimal("71000")) == 0);
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

        Assert.assertTrue(allVariableNamesResponseObj.getError() == null);
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
        Assert.assertTrue(deleteVariablesResponseObj.getError() != null);
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

        Assert.assertTrue(registerResponseObj.getError() == null);
        Assert.assertTrue(registerResponseObj.getResult().toString().equalsIgnoreCase("success"));

        String allVariableNames = this.mockMvc.perform(
                get("/api/v1/formula/variables/names")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject allVariableNamesResponseObj = JSON.parseObject(allVariableNames)
                .toJavaObject(ResponseObject.class);

        Assert.assertTrue(allVariableNamesResponseObj.getError() == null);
        List<String> variableNames = ((JSONArray) allVariableNamesResponseObj.getResult()).toJavaList(String.class);

        Assert.assertTrue("must contain huobipro_btc_usdt", variableNames.contains("huobipro_btc_usdt"));
        Assert.assertTrue("must contain usd_in_cny", variableNames.contains("usd_in_cny"));

        for (String variableName : variableNames) {
            String variableResponseString = this.mockMvc.perform(
                    get("/api/v1/formula/variable/" + variableName)
            ).andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
            ResponseObject variableResponseObj = JSON.parseObject(variableResponseString)
                    .toJavaObject(ResponseObject.class);

            Assert.assertTrue(variableResponseObj.getError() == null);
            Assert.assertTrue(variableResponseObj.getResult() != null);

            FormulaVariable formulaVariable =
                    ((JSONObject) variableResponseObj.getResult()).toJavaObject(FormulaVariable.class);
            Assert.assertTrue(formulaVariable.getValue().compareTo(BigDecimal.ZERO) > 0);
        }

        logger.info("variableNames:{}", variableNames);

        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/eval?formula=huobipro_btc_usdt*usd_in_cny*i_am_undefined")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        Assert.assertTrue(evalResponseObj.getError() != null);
    }

    @Test
    public void test005_eval_expired_variable() throws Exception {
        long timeout = 30 * 1000L;
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

        Assert.assertTrue(registerResponseObj.getError() == null);
        Assert.assertTrue(registerResponseObj.getResult().toString().equalsIgnoreCase("success"));

        String allVariableNames = this.mockMvc.perform(
                get("/api/v1/formula/variables/names")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject allVariableNamesResponseObj = JSON.parseObject(allVariableNames)
                .toJavaObject(ResponseObject.class);

        Assert.assertTrue(allVariableNamesResponseObj.getError() == null);
        List<String> variableNames = ((JSONArray) allVariableNamesResponseObj.getResult()).toJavaList(String.class);

        Assert.assertTrue("must contain huobipro_btc_usdt", variableNames.contains("huobipro_btc_usdt"));
        Assert.assertTrue("must contain usd_in_cny", variableNames.contains("usd_in_cny"));

        for (String variableName : variableNames) {
            String variableResponseString = this.mockMvc.perform(
                    get("/api/v1/formula/variable/" + variableName)
            ).andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
            ResponseObject variableResponseObj = JSON.parseObject(variableResponseString)
                    .toJavaObject(ResponseObject.class);

            Assert.assertTrue(variableResponseObj.getError() == null);
            Assert.assertTrue(variableResponseObj.getResult() != null);

            FormulaVariable formulaVariable =
                    ((JSONObject) variableResponseObj.getResult()).toJavaObject(FormulaVariable.class);
            Assert.assertTrue(formulaVariable.getValue().compareTo(BigDecimal.ZERO) > 0);
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
        Assert.assertTrue(evalResponseObj.getError() != null);
        Assert.assertTrue(evalResponseObj.getError().getMessage().contains("expired"));
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
        Assert.assertTrue(evalResponseObj.getError() == null);
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
        Assert.assertTrue(evalResponseObj.getError() == null);
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
        Assert.assertTrue(evalResponseObj.getError() == null);
        FormulaResult formulaResult = ((JSONObject) evalResponseObj.getResult()).toJavaObject(FormulaResult.class);
        logger.info("formulaResult:{}", formulaResult);
    }

    @Test
    public void test009_black_keyword() throws Exception {
        String evalResponseString = this.mockMvc.perform(
                get("/api/v1/formula/debug?formula=while(1);")
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        ResponseObject evalResponseObj = JSON.parseObject(evalResponseString)
                .toJavaObject(ResponseObject.class);
        Assert.assertTrue(evalResponseObj.getError() != null);
        Assert.assertTrue(evalResponseObj.getError().getMessage().contains("black keyword"));
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
        Assert.assertTrue(evalResponseObj.getError() != null);
        Assert.assertTrue(evalResponseObj.getError().getMessage().contains("formula contains black keyword: `quit`"));
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
        Assert.assertTrue(evalResponseObj.getError() != null);
        Assert.assertTrue(evalResponseObj.getError().getMessage().contains("formula contains black keyword: `exit`"));
    }

}
