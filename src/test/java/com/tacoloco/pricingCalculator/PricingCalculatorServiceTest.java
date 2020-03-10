package com.tacoloco.pricingCalculator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 

import com.tacoloco.pricingCalculator.pricingCalculatorService.PricingCalculatorService;

import com.tacoloco.model.*;


@SpringBootTest
class PricingCalculatorServiceTest {
  
  @Autowired
  private PricingCalculatorService service;

  @Test
  @DisplayName("Return $2.50 for 1 Veggie and is a valid order")
  void returnCorrectAmountForVeggieBurrito() throws JsonProcessingException {
    String mockJson = "{\"veggie\":1}";

    Order mockOrder = new ObjectMapper().readValue(mockJson, Order.class);
    
    Assertions.assertTrue(service.isInvalidOrder(mockJson)==false);
    Assertions.assertTrue(service.getTotal(mockOrder).equals("2.50"));
  }

  @Test
  @DisplayName("Return $3.00 for 1 Beef and is a valid order")
  void returnCorrectAmountForBeefBurrito() throws JsonProcessingException {
    String mockJson = "{\"beef\":1}";

    Order mockOrder = new ObjectMapper().readValue(mockJson, Order.class);
    
    Assertions.assertTrue(service.isInvalidOrder(mockJson)==false);
    Assertions.assertTrue(service.getTotal(mockOrder).equals("3.00"));
  }

  @Test
  @DisplayName("Return $6.00 for 2 Chicken Burritos and is a valid order")
  void returnCorrectAmountForTwoChickenBurrito() throws JsonProcessingException {
    String mockJson = "{\"chicken\":2}";

    Order mockOrder = new ObjectMapper().readValue(mockJson, Order.class);
    
    Assertions.assertTrue(service.isInvalidOrder(mockJson)==false);
    Assertions.assertTrue(service.getTotal(mockOrder).equals("6.00"));
  }

  @Test
  @DisplayName("Return true for isInvalidOrder for 1 Burger (bad order)")
  void returnIsInvalidOrderAsTrueForOneBurger() {
    String mockJson = "{\"burger\":1}";
    
    Assertions.assertTrue(service.isInvalidOrder(mockJson)==true);
  }

   @Test
  @DisplayName("Return true for isInvalidOrder for 1 Burger and 2 chicken tacos")
  void returnIsInvalidOrderAsTrueForOneBurgerAndTwoChicken() {
    String mockJson = "{\"burger\":1, \"chicken\":2}";
    
    Assertions.assertTrue(service.isInvalidOrder(mockJson)==true);
  }
}