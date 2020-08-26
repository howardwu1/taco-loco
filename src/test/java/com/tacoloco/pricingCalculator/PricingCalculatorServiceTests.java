package com.tacoloco.pricingCalculator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 

import com.tacoloco.services.PricingCalculatorService;

import com.tacoloco.services.JwtUserDetailsService;


import com.tacoloco.repository.PricingCalculatorRepository;

import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import com.tacoloco.model.*;

import static org.mockito.Mockito.verify;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.eq;

import com.tacoloco.dao.UserDao;
import com.tacoloco.dao.SessionDao;


import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tacoloco.services.JwtUserDetailsService.DuplicateUsernameException;

import org.springframework.dao.DataIntegrityViolationException;

import static org.mockito.Mockito.doThrow;

import java.util.List;

import java.util.ArrayList;

import com.jayway.jsonpath.*;

import com.tacoloco.controller.PricingCalculatorController.UserDetailsNotFoundFromUsernameException;


@SpringBootTest
class PricingCalculatorServiceTests {
  
  @MockBean
  private PasswordEncoder bCryptEncoder;

  @MockBean
  private UserDao mockUserDao;
  
  @MockBean
	private SessionDao mockSessionDao;

  @Autowired
  private PricingCalculatorService service;

  @Autowired
  private JwtUserDetailsService jwtService;
  
  @MockBean
  private PricingCalculatorRepository repository;

  
  @Test
  @DisplayName("Get sessions with valid session creator")
  void getSessionFromCreatorValid() throws JsonProcessingException, java.text.ParseException {
    service.getSessionByCreator("SirSnoopy");

    
    Session mockSession = new Session("SomeTask1", "Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)", "SirSnoopy", "Debug code for", "Java");
    DAOSession mockDaoSession = new DAOSession();
    mockDaoSession.setSessionStoryId(mockSession.getSessionStoryId());
    mockDaoSession.setTime(mockSession.getTime());
    mockDaoSession.setSessionCreator(mockSession.getSessionCreator());
    mockDaoSession.setSessionAction(mockSession.getSessionAction());
    mockDaoSession.setSessionSubjectMatter(mockSession.getSessionSubjectMatter());

    mockDaoSession.setSessionMentor(mockSession.getSessionMentor());
    mockDaoSession.setSessionMentee(mockSession.getSessionMentee());
    
    mockDaoSession.setSessionMentorComments(mockSession.getSessionMentorComments());
    mockDaoSession.setSessionMenteeComments(mockSession.getSessionMenteeComments());
    
    List<DAOSession> mockDaoSessions = new ArrayList<DAOSession>();
    mockDaoSessions.add(mockDaoSession);

    doReturn(mockDaoSessions).when(mockSessionDao).findAllBySessionCreator("SirSnoopy");
    
    DocumentContext context = JsonPath.parse(service.getSessionByCreator("SirSnoopy"));

    Assertions.assertTrue(context.read("$.length()").equals(1));
    Assertions.assertTrue(context.read("$[0].length()").equals(12));
    Assertions.assertTrue(context.read("$[0].sessionStoryId").equals("SomeTask1"));
    Assertions.assertTrue(context.read("$[0].sessionAction").equals("Debug code for"));
    Assertions.assertTrue(context.read("$[0].sessionSubjectMatter").equals("Java"));
    Assertions.assertTrue(context.read("$[0].time").equals("Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)"));
    Assertions.assertTrue(context.read("$[0].sessionCreator").equals("SirSnoopy"));


  }

  @Test
  @DisplayName("Save Session with pricingCalculatorService")
  void saveSessionValid() throws java.text.ParseException{
    
  
    Session mockSession = new Session("SomeTask1", "Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)", "A", "Debug code for", "Java");
    DAOSession mockDaoSession = new DAOSession();
    mockDaoSession.setSessionStoryId(mockSession.getSessionStoryId());
    mockDaoSession.setTime(mockSession.getTime());
    mockDaoSession.setSessionCreator(mockSession.getSessionCreator());
    mockDaoSession.setSessionAction(mockSession.getSessionAction());
    mockDaoSession.setSessionSubjectMatter(mockSession.getSessionSubjectMatter());

    mockDaoSession.setSessionMentor(mockSession.getSessionMentor());
    mockDaoSession.setSessionMentee(mockSession.getSessionMentee());
    
    mockDaoSession.setSessionMentorComments(mockSession.getSessionMentorComments());
    mockDaoSession.setSessionMenteeComments(mockSession.getSessionMenteeComments());

    service.saveSession(mockSession);
    verify(mockSessionDao).save(mockDaoSession);
  }

  @Test
  @DisplayName("Save with JwtUserDetailsService rejects same username entered more than once")
  void saveCustomerWithJwtUserDetailsInvalidRepeatedUsedname() throws JsonProcessingException{
    
    Customer mockCustomer = new Customer("SirSnoopy3", "Joe", "Cool", "SnoopDoDubbaG");

    jwtService.save(mockCustomer);

    jwtService.save(mockCustomer);
    
    doThrow(new DuplicateUsernameException()).when(mockUserDao).save(any(DAOUser.class));
    assertThrows(DuplicateUsernameException.class, () -> jwtService.save(mockCustomer),
    "expected to see DuplicateUsernameException but didn't");

  }

  @Test
  @DisplayName("Save with JwtUserDetailsService")
  void saveCustomerWithJwtUserDetails() throws JsonProcessingException{
    
    Customer mockCustomer = new Customer("SirSnoopy2", "Joe", "Cool", "SnoopDoDubbaG");

    jwtService.save(mockCustomer);

    verify(mockUserDao).save(any(DAOUser.class));
    verify(bCryptEncoder).encode(any(String.class));
  }


  @Test
  @DisplayName("Get public info from valid username")
  void getPublicInfoFromValidUsername() throws JsonProcessingException{
    
    DAOUser mockUser = new DAOUser();
      mockUser.setUsername("SirSnoopy");
      mockUser.setFirstName("Joe");
      mockUser.setLastName("Cool");

 
    doReturn(mockUser).when(mockUserDao).findByUsername("SirSnoopy");

      UserDTO mockUserDTO = new UserDTO();
      mockUserDTO.setUsername("SirSnoopy");
      mockUserDTO.setFirstName("Joe");
      mockUserDTO.setLastName("Cool");

      ObjectMapper mapper = new ObjectMapper();

      String mockUserDTOJson = mapper.writeValueAsString(mockUserDTO);

    Assertions.assertTrue(jwtService.getPublicUserDetails("SirSnoopy").equals(mockUserDTOJson));

  }

  @Test
  @DisplayName("Unsuccessfully Get public info from invalid username")
  void getPublicInfoFromInvalidUsername() throws JsonProcessingException{
 
    assertThrows(UserDetailsNotFoundFromUsernameException.class, () -> jwtService.getPublicUserDetails("FakeUser"), "UserDetailsNotFoundFromUsernameException expected but wasn't thrown");

  }

  @Test
  @DisplayName("Get public info from all users")
  void getAllPublicInfoFromAllUsers() throws JsonProcessingException{
    
    DAOUser mockUser = new DAOUser();
    mockUser.setUsername("SirSnoopy");
    mockUser.setFirstName("Joe");
    mockUser.setLastName("Cool");
  
    DAOUser mockUser2 = new DAOUser();
    mockUser2.setUsername("SirSnoopyII");
    mockUser2.setFirstName("Joe");
    mockUser2.setLastName("Cool");

    List<DAOUser> mockUsers = new ArrayList<DAOUser>();

    mockUsers.add(mockUser);
    mockUsers.add(mockUser2);

    doReturn(mockUsers).when(mockUserDao).findAll();

    DocumentContext context = JsonPath.parse(jwtService.getAllPublicInfoFromAllUsers());

    Assertions.assertTrue(context.read("$.length()").equals(2));
    Assertions.assertTrue(context.read("$[0].length()").equals(4));
    Assertions.assertTrue(context.read("$[0].username").equals("SirSnoopy"));
    Assertions.assertTrue(context.read("$[1].username").equals("SirSnoopyII"));
    Assertions.assertTrue(context.read("$[0].firstName").equals("Joe"));
    Assertions.assertTrue(context.read("$[1].firstName").equals("Joe"));
    Assertions.assertTrue(context.read("$[0].lastName").equals("Cool"));
    Assertions.assertTrue(context.read("$[1].lastName").equals("Cool"));
    Assertions.assertTrue(context.read("$[1].length()").equals(4));
 

  }
  @Test  
  @DisplayName("Save data to respository and verify it was actually saved")
  void saveDataWithJoeCool() throws JsonProcessingException {
    String mockJson = "{\"username\": \"SirSnoopy\",\"firstName\": \"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";

    Customer mockCustomer = new ObjectMapper().readValue(mockJson, Customer.class);
    
    doNothing().when(repository).insertIntoCustomers(eq("SirSnoopy"), eq("Joe"), eq("Cool"), any(String.class));

    service.insertIntoCustomers(mockCustomer.getUsername(), mockCustomer.getFirstName(),mockCustomer.getLastName(), "myencodedpass");

    verify(repository).insertIntoCustomers(eq("SirSnoopy"), eq("Joe"), eq("Cool"), any(String.class));
  }

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
}