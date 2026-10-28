package com.kanso.market;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MarketController.class)
@Import(PriceGenerator.class)
class MarketControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void statusIsOk() throws Exception {
        mvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.version").value("dev"));
    }

    @Test
    void companiesListsAllFour() throws Exception {
        mvc.perform(get("/api/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].slug").value("shizuka"))
                .andExpect(jsonPath("$[0].name").value("Shizuka Ceramics"))
                .andExpect(jsonPath("$[0].symbol").value("SZK"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"shizuka", "mokume", "kinari", "sumi"})
    void eachCompanyReturnsSixtyPositivePrices(String company) throws Exception {
        mvc.perform(get("/api/company/{name}/stocks", company))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company").value(company))
                .andExpect(jsonPath("$.currency").value("JPY"))
                .andExpect(jsonPath("$.prices", hasSize(MarketController.DEFAULT_COUNT)))
                .andExpect(jsonPath("$.prices", everyItem(greaterThan(0.0))));
    }

    @Test
    void countChangesTheLength() throws Exception {
        mvc.perform(get("/api/company/sumi/stocks").param("count", "5"))
                .andExpect(jsonPath("$.prices", hasSize(5)));
    }

    @Test
    void unknownCompanyIs404() throws Exception {
        mvc.perform(get("/api/company/nope/stocks")).andExpect(status().isNotFound());
    }

    @Test
    void countOutOfRangeIs400() throws Exception {
        mvc.perform(get("/api/company/sumi/stocks").param("count", "0")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/company/sumi/stocks").param("count", "501")).andExpect(status().isBadRequest());
    }
}
