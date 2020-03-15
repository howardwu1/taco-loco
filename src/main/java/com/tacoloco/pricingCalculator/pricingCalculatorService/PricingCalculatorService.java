package com.tacoloco.pricingCalculator.pricingCalculatorService;

import org.springframework.stereotype.Service;
import com.tacoloco.model.*;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 

@Service
public class PricingCalculatorService {
  
  public String sayHello() {
    return "hello world";
  }

  public String getTotal(Order order) { 
    return order.getTotalPrice();
  }
  
  //flaky test so implementing an alternative getTotal
  public String getTotal(String json) throws JsonProcessingException {
    return (new ObjectMapper().readValue(json, Order.class)).getTotalPrice();
  }
  
  public boolean isInvalidOrder(String json) {

    try{
       Order order = new ObjectMapper().readValue(json, Order.class);
       return false;
    }
    catch (JsonProcessingException e){
      return true;
    }
    catch (NumberFormatException e) {
      
      return true;
    }

  }

}