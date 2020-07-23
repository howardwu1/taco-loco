package com.tacoloco.services;

import org.springframework.stereotype.Service;
import com.tacoloco.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 

import com.fasterxml.jackson.databind.DeserializationFeature;

import com.tacoloco.repository.*;
@Service
public class PricingCalculatorService {
  
  @Autowired PricingCalculatorRepository pricingCalculatorRepository;

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
  
  public void queryForFirstName(String firstName)
  {
    pricingCalculatorRepository.queryForSingleQualifier("first_name", "Josh");
    //pricingCalculatorRepository.queryForMultipleQualifier(new String[]{"first_name", "last_name"}, new String[]{firstName, "Long"});
  }

    public void queryForMultipleQualifier(String[] qualifiers, Object[] values )
  {
  
    pricingCalculatorRepository.queryForMultipleQualifier(qualifiers, values);
  }

  public void insertIntoCustomers(String firstName, String lastName, String encodedPassword){
    pricingCalculatorRepository.insertIntoCustomers(firstName, lastName, encodedPassword);
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