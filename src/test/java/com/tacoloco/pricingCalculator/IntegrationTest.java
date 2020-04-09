package com.tacoloco.pricingCalculator;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.http.MediaType;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("post /total/ valid order no discount")
	public void postTotalValid() throws Exception {
      
      String json = "{\"veggie\":1, \"beef\":2}";
      
      mockMvc.perform(post("/total")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("8.50")));

	}

  @Test
  @DisplayName("post /total/ valid order with discount")
	public void postTotalValidWithDiscount() throws Exception {
      
      String json = "{\"veggie\":1, \"beef\":2, \"chorizo\":1 }";
      
      mockMvc.perform(post("/total")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("9.60")));

	}

  @Test
  @DisplayName("post /total/ invalid")
	public void getTotalInvalid() throws Exception {
      
      String json = "{\"Burger\":1, \"veggie\":1, \"beef\":2, \"chorizo\":1 }";
      
      mockMvc.perform(post("/total")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
        .andExpect(status().is(422))
        .andExpect(status().reason(containsString("No such item or counts not all whole numbers")))
        .andExpect(jsonPath("$.body").doesNotExist());

	}

  @Test
  @DisplayName("get /total/ invalid 400")
	public void postTotalInvalid400() throws Exception {
      
      String json = "";
      
      mockMvc.perform(post("/total")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.body").doesNotExist());

	}
  
  
}
