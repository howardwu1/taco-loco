package com.tacoloco.services;

import org.springframework.stereotype.Service;
import com.tacoloco.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.security.authentication.AuthenticationManager;

import com.tacoloco.repository.*;

import com.tacoloco.dao.SessionDao;

@Service
public class PricingCalculatorService {
  
  @Autowired PricingCalculatorRepository pricingCalculatorRepository;

  @Autowired
  AuthenticationManager authenticationManager; //not being used in this class however is a dependency that is needed for a spring boot to resolve a circular reference that also has been made @lazy
  
  @Autowired
  private SessionDao sessionDao;
  
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

  public void insertIntoCustomers(String username, String firstName, String lastName, String encodedPassword){
    pricingCalculatorRepository.insertIntoCustomers(username, firstName, lastName, encodedPassword);
  }

  public void checkPassword(String firstName, String lastName, String password){

    //todo


  }

  public DAOSession saveSession(Session session){
    //todo
    return sessionDao.save(new DAOSession());
  }

}