package com.tacoloco.services;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.SessionStatus;

import com.tacoloco.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.security.authentication.AuthenticationManager;

import com.tacoloco.repository.*;

import com.tacoloco.dao.SessionDao;

import org.springframework.dao.DataIntegrityViolationException;

import java.text.SimpleDateFormat;

import java.text.DateFormat;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.tacoloco.dao.UserDao;
import com.tacoloco.controller.PricingCalculatorController.UserDetailsNotFoundFromUsernameException;

@Service
public class PricingCalculatorService {
  
  @Autowired PricingCalculatorRepository pricingCalculatorRepository;

  @Autowired
	private UserDao userDao;

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

  public String getPublicUserDetails(String username) throws JsonProcessingException{
    
    try{
      DAOUser user = userDao.findByUsername(username);

      UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setRole(user.getRole());
    
      ObjectMapper mapper = new ObjectMapper();
    
      String json = mapper.writeValueAsString(userDTO);
      return json;
    } catch(java.lang.NullPointerException e){
      throw new UserDetailsNotFoundFromUsernameException();
    }

  }

  
	public String getAllPublicInfoFromAllUsers() throws JsonProcessingException{

		List<DAOUser> users = userDao.findAll();

		ObjectMapper mapper = new ObjectMapper();

		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(users);
		return json;
	
	}

  public void checkPassword(String firstName, String lastName, String password){

    //todo


  }



  public String getSessionByUsername(String username) throws JsonProcessingException{
    List<DAOSession> daoSessions = sessionDao.findAllBySessionCreatorOrTeammates(username);


    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);
    DateFormat df = new SimpleDateFormat("EE MMM d y H:mm:ss 'GMT'Z (zz)");
    mapper.setDateFormat(df);

    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(daoSessions);
    return json;
  }
  
  public DAOSession saveSession(Session session){

    try{
      DAOSession daoSession = new DAOSession();
      System.out.println("sessionSTory*****" + session.getSessionStoryId());
      daoSession.setSessionStoryId(session.getSessionStoryId());
      daoSession.setTime(session.getTime());
      daoSession.setSessionCreator(session.getSessionCreator());
      daoSession.setSessionAction(session.getSessionAction());
      daoSession.setSessionSubjectMatter(session.getSessionSubjectMatter());
      daoSession.setTeammates(session.getTeammates());
      daoSession.setSessionMentor(session.getSessionMentor());
      daoSession.setSessionMentee(session.getSessionMentee());
      daoSession.setSessionMentorComments(session.getSessionMentorComments());
      daoSession.setSessionMenteeComments(session.getSessionMenteeComments());
      return sessionDao.save(daoSession);
    } catch (DataIntegrityViolationException e){
      throw new DuplicateStoryIdException();
    }
  }

  public List<DAOSession> overwriteSession(DAOSession[] sessions){
    System.out.println("sessions****" + sessions[0]);
     return (List<DAOSession>) sessionDao.saveAll(new ArrayList<DAOSession>(Arrays.asList(sessions)));
 
  }


}