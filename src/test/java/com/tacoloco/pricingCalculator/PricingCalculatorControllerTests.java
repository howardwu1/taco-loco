package com.tacoloco.pricingCalculator;

import com.tacoloco.pricingCalculatorService.PricingCalculatorService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.DisplayName;


import static org.assertj.core.api.Assertions.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tacoloco.model.*;

@SpringBootTest
@AutoConfigureMockMvc
class PricingCalculatorControllerTests {

  @Autowired
	private MockMvc mockMvc;

  @MockBean
  private PricingCalculatorService service;

	@Test
  @DisplayName("get / -- hello world")
	public void getHelloWorld() throws Exception {
      
      doReturn("hello world").when(service).sayHello();

      mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("hello world")));

	}

  @Test
  @DisplayName("get /total/ valid order")
	public void getTotalValid() throws Exception {
      
      String mockJson = "{\"veggie\":1}";
      Order mockOrder = new ObjectMapper().readValue(mockJson, Order.class);
      doReturn(2.50).when(service).getTotal(mockOrder);

      mockMvc.perform(get("/total")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mockJson))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("2.5")));

	}
}
