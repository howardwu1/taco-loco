package com.tacoloco.pricingCalculator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 

import com.tacoloco.services.PricingCalculatorService;

import com.tacoloco.repository.PricingCalculatorRepository;

import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.doNothing;

import com.tacoloco.model.*;

import static org.mockito.Mockito.verify;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.eq;


@SpringBootTest
class PricingCalculatorServiceTests {
  
  @Autowired
  private PricingCalculatorService service;

  @MockBean
  private PricingCalculatorRepository repository;

  @Test
  @DisplayName("Call repository with 'Joe Cool' and is a valid JSON")
  void callRepositoryWithJoeCool() throws JsonProcessingException {
    String mockJson = "{\"username\": \"SirSnoopy\",\"firstName\": \"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";

    Customer mockCustomer = new ObjectMapper().readValue(mockJson, Customer.class);
    
    doNothing().when(repository).insertIntoCustomers(eq("SirSnoopy"), eq("Joe"), eq("Cool"), any(String.class));

    service.insertIntoCustomers(mockCustomer.getUsername(), mockCustomer.getFirstName(),mockCustomer.getLastName(), "myencodedpass");

    verify(repository).insertIntoCustomers(eq("SirSnoopy"), eq("Joe"), eq("Cool"), any(String.class));
  }

  @Test
  @DisplayName("Return $2.50 for 1 Veggie and is a valid order")
  void returnCorrectAmountForVeggieBurrito() throws JsonProcessingException {
    String mockJson = "{\"veggie\":1}";

    Order mockOrder = new ObjectMapper().readValue(mockJson, Order.class);
    
    // Assertions.assertTrue(!service.isInvalidOrder(mockJson));
    Assertions.assertTrue(service.getTotal(mockOrder).equals("2.50"));
  }

  @Test
  @DisplayName("Return $3.00 for 1 Beef and is a valid order")
  void returnCorrectAmountForBeefBurrito() throws JsonProcessingException {
    String mockJson = "{\"beef\":1}";

    Order mockOrder = new ObjectMapper().readValue(mockJson, Order.class);
    
    // Assertions.assertTrue(!service.isInvalidOrder(mockJson));
    Assertions.assertTrue(service.getTotal(mockOrder).equals("3.00"));
  }

  @Test
  @DisplayName("Return $6.00 for 2 Chicken Burritos and is a valid order")
  void returnCorrectAmountForTwoChickenBurrito() throws JsonProcessingException {
    String mockJson = "{\"chicken\":2}";

    Order mockOrder = new ObjectMapper().readValue(mockJson, Order.class);
    
    // Assertions.assertTrue(!service.isInvalidOrder(mockJson));
    Assertions.assertTrue(service.getTotal(mockOrder).equals("6.00"));
  }


  @Test
  @DisplayName("Return a post-20%-discount total of $10.40 for 2 Chicken Burritos and 2 Chorizo Burritos and is a valid order")
  void returnCorrectAmountForTwoChickenBurritoAndTwoChorizoBurito() throws JsonProcessingException {
    String mockJson = "{\"chicken\":2, \"chorizo\":2}";

    Order mockOrder = new ObjectMapper().readValue(mockJson, Order.class);
    
    // Assertions.assertTrue(!service.isInvalidOrder(mockJson));
    Assertions.assertTrue(service.getTotal(mockOrder).equals("10.40"));
  }

  @Test
  @DisplayName("Return true for isInvalidOrder for 1 Burger (bad order)")
  void returnIsInvalidOrderAsTrueForOneBurger() {
    String mockJson = "{\"burger\":1}";
    
    Assertions.assertTrue(service.isInvalidOrder(mockJson));
  }

  @Test
  @DisplayName("Return true for isInvalidOrder for 1 Burger and 2 chicken tacos")
  void returnIsInvalidOrderAsTrueForOneBurgerAndTwoChicken() {
    String mockJson = "{\"burger\":1, \"chicken\":2}";
    
    Assertions.assertTrue(service.isInvalidOrder(mockJson));
  }

  @Test
  @DisplayName("Return true for isInvalidOrder for 2.5 chicken tacos")
  void returnIsInvalidOrderAsTrueForNonWholeNumberOrder() {
    String mockJson = "{\"chicken\":2.5}";

    Assertions.assertTrue(service.isInvalidOrder(mockJson));
  }

  @Test
  @DisplayName("Return true for isInvalidOrder for -2 chicken tacos")
  void returnIsInvalidOrderAsTrueForNegativeNumberOrder() {
    String mockJson = "{\"chicken\":-2}";

    Assertions.assertTrue(service.isInvalidOrder(mockJson));
  }
}