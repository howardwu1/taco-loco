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

import org.springframework.dao.DataIntegrityViolationException;

@Service
public class PricingCalculatorService {
  
  @Autowired PricingCalculatorRepository pricingCalculatorRepository;

  @Autowired
  AuthenticationManager authenticationManager; //not being used in this class however is a dependency that is needed for a spring boot to resolve a circular reference that also has been made @lazy
  
  @Autowired
  private SessionDao sessionDao;
  
  public static class DuplicateStoryIdException extends RuntimeException {}

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
  public String getSessionByCreator(String sessionCreator) throws JsonProcessingException{
    return "todo";
  }
  
  public DAOSession saveSession(Session session){

    try{
      DAOSession daoSession = new DAOSession();
      daoSession.setSessionStoryId(session.getSessionStoryId());
      daoSession.setTime(session.getTime());
      daoSession.setSessionCreator(session.getSessionCreator());
      daoSession.setSessionAction(session.getSessionAction());
      daoSession.setSessionSubjectMatter(session.getSessionSubjectMatter());

      daoSession.setSessionMentor(session.getSessionMentor());
      daoSession.setSessionMentee(session.getSessionMentee());
      
      daoSession.setSessionMentorComments(session.getSessionMentorComments());
      daoSession.setSessionMenteeComments(session.getSessionMenteeComments());
      return sessionDao.save(daoSession);
    } catch (DataIntegrityViolationException e){
      throw new DuplicateStoryIdException();
    }
  }

}