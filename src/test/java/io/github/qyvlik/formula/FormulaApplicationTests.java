package io.github.qyvlik.formula;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import io.github.qyvlik.formula.common.base.Result;
import io.github.qyvlik.formula.modules.api.req.CalculateFormulaReq;
import io.github.qyvlik.formula.modules.api.req.CurrencyConvertReq;
import io.github.qyvlik.formula.modules.api.req.UpdateMarketPriceReq;
import io.github.qyvlik.formula.modules.formula.model.CalculateResultData;
import io.github.qyvlik.formula.modules.formula.model.CurrencyConvertResultData;
import io.github.qyvlik.formula.modules.formula.model.MarketPrice;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class FormulaApplicationTests {

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
    public void test001_update_and_calculate() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();

        String base = "btc";
        String quote = "usdt";
        String exchange = "binance";
        final BigDecimal price = new BigDecimal("10000");
        final BigDecimal factor = new BigDecimal("1.0101");
        final BigDecimal calculateResultPrice = price.multiply(factor);

        {
            UpdateMarketPriceReq updateReq = new UpdateMarketPriceReq();
            updateReq.setExchange(exchange);
            updateReq.setCode("btc_usdt");
            updateReq.setBase(base);
            updateReq.setQuote(quote);
            updateReq.setPrice(price);
            updateReq.setTimestamp(currentTimeMillis);
            String updateVariableResponseString = this.mockMvc.perform(
                            post("/api/v1/formula/variable/market-price/update")
                                    .contentType(MediaType.parseMediaType("application/json"))
                                    .header("token", token)
                                    .content(JSON.toJSONString(updateReq))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse().getContentAsString();

            Result<Long> updateResult = JSON.parseObject(updateVariableResponseString, new TypeReference<Result<Long>>() {
            });
            assertTrue(updateResult.isSuccess());
        }

        {
            String getVariableInfoResponseString = this.mockMvc.perform(
                            get(String.format("/api/v1/formula/variable/market-price/info?base=%s&quote=%s&exchange=%s", base, quote, exchange))
                                    .header("token", token)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse().getContentAsString();

            Result<MarketPrice> getResult = JSON.parseObject(getVariableInfoResponseString, new TypeReference<Result<MarketPrice>>() {
            });
            assertTrue(getResult.isSuccess());
            assertNotNull(getResult.getData());
            assertEquals(exchange, getResult.getData().getExchange());
            assertEquals(base, getResult.getData().getBase());
            assertEquals(quote, getResult.getData().getQuote());
            assertEquals(0, getResult.getData().getPrice().compareTo(price));
        }




        {
            String variableName1 = String.join("_", exchange, base, quote);
            String formula = String.format("%s * %s", variableName1, factor);

            CalculateFormulaReq calculateFormulaReq = new CalculateFormulaReq();
            calculateFormulaReq.setFormula(formula);

            String calculateResponseString = this.mockMvc.perform(
                            post("/api/v1/formula/calculate")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("token", token)
                                    .content(JSON.toJSONString(calculateFormulaReq))
                    ).andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse().getContentAsString();

            Result<CalculateResultData> calculateResult = JSON.parseObject(calculateResponseString, new TypeReference<Result<CalculateResultData>>() {
            });
            assertTrue(calculateResult.isSuccess());
            assertNotNull(calculateResult.getData());
            assertEquals(formula, calculateResult.getData().getOrigin());
            assertEquals(0, calculateResult.getData().getResult().compareTo(calculateResultPrice));
        }
    }

    @Test
    public void test002_update_and_calculate() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();

        String base = "btc";
        String quote = "usdt";
        String exchange = "okex";
        final BigDecimal price = new BigDecimal("10001");
        final BigDecimal factor = new BigDecimal("1.0102");
        final BigDecimal calculateResultPrice = price.multiply(factor);

        {
            UpdateMarketPriceReq updateReq = new UpdateMarketPriceReq();
            updateReq.setExchange(exchange);
            updateReq.setCode("btc_usdt");
            updateReq.setBase(base);
            updateReq.setQuote(quote);
            updateReq.setPrice(price);
            updateReq.setTimestamp(currentTimeMillis);
            String updateVariableResponseString = this.mockMvc.perform(
                            post("/api/v1/formula/variable/market-price/update")
                                    .contentType(MediaType.parseMediaType("application/json"))
                                    .header("token", token)
                                    .content(JSON.toJSONString(updateReq))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse().getContentAsString();

            Result<Long> updateResult = JSON.parseObject(updateVariableResponseString, new TypeReference<Result<Long>>() {
            });
            assertTrue(updateResult.isSuccess());
        }

        {
            String getVariableInfoResponseString = this.mockMvc.perform(
                            get(String.format("/api/v1/formula/variable/market-price/info?base=%s&quote=%s&exchange=%s", base, quote, exchange))
                                    .header("token", token)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse().getContentAsString();

            Result<MarketPrice> getResult = JSON.parseObject(getVariableInfoResponseString, new TypeReference<Result<MarketPrice>>() {
            });
            assertTrue(getResult.isSuccess());
            assertNotNull(getResult.getData());
            assertEquals(exchange, getResult.getData().getExchange());
            assertEquals(base, getResult.getData().getBase());
            assertEquals(quote, getResult.getData().getQuote());
            assertEquals(0, getResult.getData().getPrice().compareTo(price));
        }


        {
            String variableName1 = String.join("_", exchange, base, quote);
            String formula = String.format("%s * %s", variableName1, factor);

            CalculateFormulaReq calculateFormulaReq = new CalculateFormulaReq();
            calculateFormulaReq.setFormula(formula);

            String calculateResponseString = this.mockMvc.perform(
                            post("/api/v1/formula/calculate")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("token", token)
                                    .content(JSON.toJSONString(calculateFormulaReq))
                    ).andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse().getContentAsString();

            Result<CalculateResultData> calculateResult = JSON.parseObject(calculateResponseString, new TypeReference<Result<CalculateResultData>>() {
            });
            assertTrue(calculateResult.isSuccess());
            assertNotNull(calculateResult.getData());
            assertEquals(formula, calculateResult.getData().getOrigin());
            assertEquals(0, calculateResult.getData().getResult().compareTo(calculateResultPrice));
        }
    }

    @Test
    public void test003_update_and_convert() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();

        {
            String base = "btc";
            String quote = "usdt";
            String exchange = "binance";
            final BigDecimal price = new BigDecimal("10001");

            UpdateMarketPriceReq updateReq = new UpdateMarketPriceReq();
            updateReq.setExchange(exchange);
            updateReq.setCode("btc_usdt");
            updateReq.setBase(base);
            updateReq.setQuote(quote);
            updateReq.setPrice(price);
            updateReq.setTimestamp(currentTimeMillis);
            String updateVariableResponseString = this.mockMvc.perform(
                            post("/api/v1/formula/variable/market-price/update")
                                    .contentType(MediaType.parseMediaType("application/json"))
                                    .header("token", token)
                                    .content(JSON.toJSONString(updateReq))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse().getContentAsString();

            Result<Long> updateResult = JSON.parseObject(updateVariableResponseString, new TypeReference<Result<Long>>() {
            });
            assertTrue(updateResult.isSuccess());

            String getVariableInfoResponseString = this.mockMvc.perform(
                            get(String.format("/api/v1/formula/variable/market-price/info?base=%s&quote=%s&exchange=%s", base, quote, exchange))
                                    .header("token", token)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse().getContentAsString();

            Result<MarketPrice> getResult = JSON.parseObject(getVariableInfoResponseString, new TypeReference<Result<MarketPrice>>() {
            });
            assertTrue(getResult.isSuccess());
            assertNotNull(getResult.getData());
            assertEquals(exchange, getResult.getData().getExchange());
            assertEquals(base, getResult.getData().getBase());
            assertEquals(quote, getResult.getData().getQuote());
            assertEquals(0, getResult.getData().getPrice().compareTo(price));
        }

        {

            String base = "eth";
            String quote = "btc";
            String exchange = "okex";
            final BigDecimal price = new BigDecimal("0.077");

            UpdateMarketPriceReq updateReq = new UpdateMarketPriceReq();
            updateReq.setExchange(exchange);
            updateReq.setCode("eth_btc");
            updateReq.setBase(base);
            updateReq.setQuote(quote);
            updateReq.setPrice(price);
            updateReq.setTimestamp(currentTimeMillis);
            String updateVariableResponseString = this.mockMvc.perform(
                            post("/api/v1/formula/variable/market-price/update")
                                    .contentType(MediaType.parseMediaType("application/json"))
                                    .header("token", token)
                                    .content(JSON.toJSONString(updateReq))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse().getContentAsString();

            Result<Long> updateResult = JSON.parseObject(updateVariableResponseString, new TypeReference<Result<Long>>() {
            });
            assertTrue(updateResult.isSuccess());

            String getVariableInfoResponseString = this.mockMvc.perform(
                            get(String.format("/api/v1/formula/variable/market-price/info?base=%s&quote=%s&exchange=%s", base, quote, exchange))
                                    .header("token", token)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse().getContentAsString();

            Result<MarketPrice> getResult = JSON.parseObject(getVariableInfoResponseString, new TypeReference<Result<MarketPrice>>() {
            });
            assertTrue(getResult.isSuccess());
            assertNotNull(getResult.getData());
            assertEquals(exchange, getResult.getData().getExchange());
            assertEquals(base, getResult.getData().getBase());
            assertEquals(quote, getResult.getData().getQuote());
            assertEquals(0, getResult.getData().getPrice().compareTo(price));
        }

        {
            CurrencyConvertReq currencyConvertReq = new CurrencyConvertReq();
            currencyConvertReq.setSource("btc");
            currencyConvertReq.setTarget("usdt");
            currencyConvertReq.setAmount(new BigDecimal("1.099"));
            currencyConvertReq.setScale(4);
            currencyConvertReq.setExchanges(Lists.newArrayList("okex", "binance"));

            String convertResponseString = this.mockMvc.perform(
                            post("/api/v1/formula/convert")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("token", token)
                                    .content(JSON.toJSONString(currencyConvertReq))
                    ).andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse().getContentAsString();

            Result<CurrencyConvertResultData> calculateResult = JSON.parseObject(convertResponseString, new TypeReference<Result<CurrencyConvertResultData>>() {
            });
            assertTrue(calculateResult.isSuccess());
            assertNotNull(calculateResult.getData());
            log.info("calculateResult={}", calculateResult);
        }
    }


}
