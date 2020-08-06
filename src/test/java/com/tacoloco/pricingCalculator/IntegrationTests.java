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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

import com.tacoloco.model.*;

import org.junit.jupiter.api.Assertions;

import com.tacoloco.repository.PricingCalculatorRepository;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(value="test")
@AutoConfigureMockMvc
class IntegrationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PricingCalculatorRepository repository;

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Test
  @DisplayName("put /insertCustomer valid user info")
	public void putInsertCustomerValid() throws Exception {
    
    jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
    jdbcTemplate.execute("CREATE TABLE customers(" +
        "id SERIAL, username VARCHAR(255), first_name VARCHAR(255), last_name VARCHAR(255), encoded_password VARCHAR(255))");

    String mockJson = "{\"username\": \"SirSnoopy\", \"firstName\":\"Joe\", \"lastName\": \"Cool\",\"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";
    
    mockMvc.perform(put("/insertCustomer")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mockJson))
      .andExpect(status().isOk());
    

    String query = "SELECT * FROM customers";
    
    List<Customer> customerlist = new ArrayList<Customer>();
    
    customerlist = jdbcTemplate.query(
        query,
        (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("username"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("encoded_password"))
    );
    
    Assertions.assertTrue(customerlist.size()==1);

    Assertions.assertTrue(customerlist.get(0).getId()==1);
    
    Assertions.assertTrue(customerlist.get(0).getUsername().equals("SirSnoopy"));
    
    Assertions.assertTrue(customerlist.get(0).getFirstName().equals("Joe"));

    Assertions.assertTrue(customerlist.get(0).getLastName().equals("Cool"));

    Assertions.assertTrue(!customerlist.get(0).getEncodedPassword().isEmpty());

	}

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
  @DisplayName("post /total/ negative invalid")
	public void getTotalNegativeNumber() throws Exception {
      
      String json = "{\"veggie\":-1, \"beef\":2, \"chorizo\":1 }";
      
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
      
      String json = "{";
      
      mockMvc.perform(post("/total")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.body").doesNotExist());

	}
  
  
}
