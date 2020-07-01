package com.tacoloco.services;

import org.springframework.stereotype.Service;
import com.tacoloco.model.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 

import com.fasterxml.jackson.databind.DeserializationFeature;

@Service
public class PricingCalculatorService {
  
  public String sayHello() {
    return "hello world";
  }

  public String getTotal(Order order) { 
    return order.calculateTotalPrice();
  }
  
  //flaky test so implementing an alternative getTotal
  //todo: get rid of this in case I am no longer using "strings"
  public String getTotal(String json) throws JsonProcessingException {

    Order order = new ObjectMapper().reader(Order.class).without(DeserializationFeature.ACCEPT_FLOAT_AS_INT).readValue(json);
    
    return order.calculateTotalPrice();
  }
  
  // public boolean isInvalidOrder(String json) {

  //   try{
  //      Order order = new ObjectMapper().reader(Order.class).without(DeserializationFeature.ACCEPT_FLOAT_AS_INT).readValue(json);
  //      return false;
  //   }
  //   catch (JsonProcessingException e){
  //     return true;
  //   }
  //   catch (NumberFormatException e) {
      
  //     return true;
  //   }

  // }

}