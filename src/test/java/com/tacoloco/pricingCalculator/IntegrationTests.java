
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

import com.tacoloco.model.*;

import org.junit.jupiter.api.Assertions;

import com.tacoloco.repository.PricingCalculatorRepository;

import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper; 

import com.tacoloco.services.JwtUserDetailsService;
import com.tacoloco.services.PricingCalculatorService;

import static org.hamcrest.Matchers.*;

import com.tacoloco.dao.UserDao;

import org.springframework.test.context.TestPropertySource;

import com.tacoloco.TestConfiguration;

import com.jayway.jsonpath.*;

import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

import org.springframework.http.HttpHeaders;

import static org.hamcrest.core.AnyOf.*;

import org.springframework.boot.test.mock.mockito.MockBean;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import static org.mockito.Mockito.doReturn;

import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = TestConfiguration.class)
@ActiveProfiles(value="test")
@AutoConfigureMockMvc
class IntegrationTests {


  @Autowired
  private UserDao userDao;
  
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PricingCalculatorRepository repository;

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Autowired
  private JwtUserDetailsService jwtService;

  @Autowired
  private PricingCalculatorService service;

  @MockBean
  private GoogleToken mockParentToken;

  @MockBean
  private GoogleIdToken mockToken;

  @MockBean
  private GoogleIdTokenVerifier mockVerifier;

  @Test
  @DisplayName("post /tokensignin valid user info")
  public void postTokenSigninUserValid() throws Exception {
 
    String mockTokenIdString = "faketokenforgoogle";
    // Note: this does not verify if mockTokenIdString is correctly parsed
    // as the mock overrides getIdTokenString
    String mockURLEncoded = "idTokenString=" + mockTokenIdString;
 
    doReturn(mockTokenIdString).when(mockParentToken).getIdTokenString();
    doReturn(mockToken).when(mockVerifier).verify(mockTokenIdString);
    
    Payload mockPayload = new Payload();

    mockPayload.setSubject("23493849239");
    mockPayload.setEmail("sirsnoopy@gmail.com");

    mockPayload.set("name", "sirsnoopy");

    mockPayload.set("family_name", "Cool");

    mockPayload.set("given_name", "Joe");

    //when((String) mockPayload.get("name")).thenReturn("sirsnoopy");

    doReturn(mockPayload).when(mockToken).getPayload(); 
    
    //doReturn("Cool").when(mockPayload).get("family_name");
    
    //doReturn("Joe").when(mockPayload).get("given_name");
  

        //count current users in the database -- before adding the new user

    Integer orig_user_count = userDao.findAll().size();

    mockMvc.perform(post("/tokensignin")
          .contentType(MediaType.APPLICATION_FORM_URLENCODED)
          .content(mockURLEncoded))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()", is(1)))
          .andExpect(jsonPath("$.token").isNotEmpty());

           //verify that save was called by counting the list and seeing it's 1 + orig_user_count
      Assertions.assertTrue(userDao.findAll().size() == orig_user_count + 1);
      //tokensignin with the same account name -- but verify that save was not called
      mockMvc.perform(post("/tokensignin")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content(mockURLEncoded))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", is(1)))
            .andExpect(jsonPath("$.token").isNotEmpty());
  //verify that service still only called save once and thus the number is still 1+ orig_user_count
  Assertions.assertTrue(userDao.findAll().size() == orig_user_count + 1);
	}

  @Test
  @DisplayName("post /tokensignin invalid user info")
  public void postTokenSigninUserInvalid() throws Exception {
 
    String mockTokenIdString = "faketokenforgoogle";
    // Note: this does not verify if mockTokenIdString is correctly parsed
    // as the mock overrides getIdTokenString
    String mockURLEncoded = "idTokenString=" + mockTokenIdString;
 
    doReturn(mockTokenIdString).when(mockParentToken).getIdTokenString();
    doReturn(null).when(mockVerifier).verify(mockTokenIdString);
    
    
    mockMvc.perform(post("/tokensignin")
          .contentType(MediaType.APPLICATION_FORM_URLENCODED)
          .content(mockURLEncoded))
          .andExpect(status().is(403));
    
	}

  // @Test
  // @DisplayName("get /publicUserDetails valid after registering")
	// public void getPublicDetailsForExistingUser() throws Exception{
      
  //     String mockJson = "{\"username\":\"SirSnoopy7\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\", \"role\": \"bum\"}";
  //     mockMvc.perform(post("/register")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson))
  //       .andExpect(status().is(201));

  //     String mockUserName = "SirSnoopy7";
      
  //     mockMvc.perform(get("/publicUserDetails/{username}", mockUserName)
  //                   .contentType(MediaType.APPLICATION_JSON))
  //                   .andExpect(status().isOk());

  //     UserDTO mockUserDTO = new UserDTO();
  //     mockUserDTO.setUsername("SirSnoopy7");
  //     mockUserDTO.setFirstName("Joe");
  //     mockUserDTO.setLastName("Cool");
  //     mockUserDTO.setRole("bum");

  //     ObjectMapper mapper = new ObjectMapper();

  //     String mockUserDTOJson = mapper.writeValueAsString(mockUserDTO);
  //   Assertions.assertTrue(service.getPublicUserDetails("SirSnoopy7").equals(mockUserDTOJson));
  // }
  
  // @Test
  // @DisplayName("Get public info from invalid username")
  // void getPublicInfoFromInvalidUsername() throws Exception{
  //   String mockUserName = "FakeName";

       
  //   mockMvc.perform(get("/publicUserDetails/{username}", mockUserName)
  //   .contentType(MediaType.APPLICATION_JSON))
  //   .andExpect(status().is(422))
  //   .andExpect(status().reason(containsString("User does not exist with the username provided")));
    


  // }

  // @Test
  // @DisplayName("get /allPublicUserDetails valid")
	// public void getPublicDetailsForAllExistingUsers() throws Exception{
      
  //     userDao.deleteAll();

  //     String mockJson = "{\"username\":\"SirSnoopy\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\", \"role\": \"Software Engineer I\"}";
  //     mockMvc.perform(post("/register")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson))
  //       .andExpect(status().is(201));
      
  //     String mockJson2 = "{\"username\":\"MrSnoopy\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";
  //     mockMvc.perform(post("/register")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson2))
  //       .andExpect(status().is(201));

  //     mockMvc.perform(get("/allPublicUserDetails")
  //                   .contentType(MediaType.APPLICATION_JSON))
  //                   .andExpect(status().isOk())
  //                   .andExpect(jsonPath("$[0].username",is("SirSnoopy")))
  //                   .andExpect(jsonPath("$[1].username",is("MrSnoopy")))
  //                   .andExpect(jsonPath("$[0].firstName",is("Joe")))
  //                   .andExpect(jsonPath("$[1].firstName",is("Joe")))
  //                   .andExpect(jsonPath("$[0].lastName",is("Cool")))
  //                   .andExpect(jsonPath("$[1].lastName",is("Cool")))
  //                   .andExpect(jsonPath("$[0].password").doesNotExist())
  //                   .andExpect(jsonPath("$[1].password").doesNotExist())
  //                   .andExpect(jsonPath("$[0].role",is("Software Engineer I")))
  //                   .andExpect(jsonPath("$.length()",is(2)))
  //                   .andExpect(jsonPath("$[0].length()",is(5)))
  //                   .andExpect(jsonPath("$[1].length()",is(5)));

	// }

  // @Test
  // @DisplayName("post /register valid user info")
	// public void postRegisterCustomerValid() throws Exception{
      
  //     String mockJson = "{\"username\":\"SirSnoopy2\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\", \"role\": \"Sofware Engineer I\"}";

  //     mockMvc.perform(post("/register")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson))
  //       .andExpect(status().is(201));

  // }
  
  
  // @Test
  // @DisplayName("post /register valid user info to update details")
	// public void postUpdateCustomerValid() throws Exception{
      
  //     String mockJson = "{\"username\":\"SirSnoopy4\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\", \"role\": \"Sofware Engineer I\"}";

  //     mockMvc.perform(post("/register")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson))
  //       .andExpect(status().is(201));



  //   mockJson = "{\"username\": \"SirSnoopy4\", \"firstName\":\"Joey\"}";

  //   mockMvc.perform(post("/updateUser")
  //   .contentType(MediaType.APPLICATION_JSON)
  //   .content(mockJson))
  //   .andExpect(status().is(201));

  //   mockMvc.perform(get("/publicUserDetails/{username}", "SirSnoopy4")
  //   .contentType(MediaType.APPLICATION_JSON))
  //   .andExpect(status().isOk())
  //   .andExpect(jsonPath("$.username",is("SirSnoopy4")))
  //   .andExpect(jsonPath("$.firstName",is("Joey")))
  //   .andExpect(jsonPath("$.lastName",is("Cool")));

  // }
  // @Test
  // @DisplayName("post /addNewSession valid session info")
	// public void postAddNewSessionValid() throws Exception{
      
  //   String mockJson = "{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"A\",\"sessionMentor\":null,\"sessionMentee\":\"A\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\":\"SomeTask1\"}";    

  //     mockMvc.perform(post("/addNewSession")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson))
  //       .andExpect(status().isOk());

  // }

  
  // @Test
  // @DisplayName("post /addNewSession -- valid session info but wrong user")
	// public void postAddNewSessionWrongUser() throws Exception{
      
  //   String mockJson = "{\"username\":\"SirSnoopy2\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";
    
  //   MvcResult mvcResult = mockMvc.perform(post("/authenticate")
  //   .contentType(MediaType.APPLICATION_JSON)
  //   .content(mockJson))
  //   .andExpect(status().isOk())
  //   .andReturn();
    
  //   String token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
  //   String mockJson2 = "{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"A\",\"sessionMentor\":null,\"sessionMentee\":\"A\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\":\"SomeTask1\"}";    

  //     mockMvc.perform(post("/addNewSession")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson2)
  //                   .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)) 
  //       .andExpect(status().is(403));

  // }

  // @Test
  // @DisplayName("post /addNewSession -- valid session info with correct user")
	// public void postAddNewSessionRightUser() throws Exception{
  
  //   //A was never registered in our tests -- so I need to register him.
  //   String mockJson = "{\"username\":\"A\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";
    
  //   mockMvc.perform(post("/register")
  //   .contentType(MediaType.APPLICATION_JSON)
  //   .content(mockJson))
  //   .andExpect(status().is(201))
  //   .andReturn();

  //   MvcResult mvcResult = mockMvc.perform(post("/authenticate")
  //   .contentType(MediaType.APPLICATION_JSON)
  //   .content(mockJson))
  //   .andExpect(status().isOk())
  //   .andReturn();
    
    
    
  //   String token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
  //   String mockJson2 = "{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"A\",\"sessionMentor\":null,\"sessionMentee\":\"A\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\":\"SomeTask11\"}";    

  //     mockMvc.perform(post("/addNewSession")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson2)
  //                   .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)) 
  //       .andExpect(status().isOk());

  // }

  // @Test
  // @DisplayName("post /overwriteSession valid session info")
	// public void postOverwriteSessionValid() throws Exception{
      
  //   String mockJson = "[{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"SnoopyJr\",\"sessionMentor\":null,\"sessionMentee\":\"A\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\":\"SomeTask5\"}]";    

  //     mockMvc.perform(post("/overwriteSession")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson))
  //       .andExpect(status().isOk());

  //     String mockUsername = "SnoopyJr";

  //   MvcResult result = mockMvc.perform(get("/sessionFromUsername/{username}", mockUsername))
  //                       .andExpect(status().isOk())
  //                       .andReturn();
       
  //   Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$[0].id");

  //   String mockJson2 = "[{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"SnoopyJr\",\"sessionMentor\":\"B\",\"sessionMentee\":\"A\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\":\"SomeTask5\", \"id\":" + id + "}]";    

  //   mockMvc.perform(post("/overwriteSession")
  //   .contentType(MediaType.APPLICATION_JSON)
  //   .content(mockJson2))
  //   .andExpect(status().isOk());


  //   mockMvc.perform(get("/sessionFromUsername/{username}", mockUsername)
  //                   .contentType(MediaType.APPLICATION_JSON))
  //                   .andExpect(status().isOk())
  //                   .andExpect(jsonPath("$[0].sessionCreator",is("SnoopyJr")))
  //                   .andExpect(jsonPath("$[0].sessionMentor", is("B")))
  //                   .andExpect(jsonPath("$.length()", is(1)))
  //                   .andExpect(jsonPath("$[0].length()", is(13)));
  // }

  // @Test
  // @DisplayName("post /overwriteSession valid info wrong user")
	// public void postOverwriteSessionWrongUser() throws Exception{


  //   String mockJson = "{\"username\":\"SirSnoopy5\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";
    
  //   mockMvc.perform(post("/register")
  //   .contentType(MediaType.APPLICATION_JSON)
  //   .content(mockJson))
  //   .andExpect(status().is(201))
  //   .andReturn();

  //   MvcResult mvcResult = mockMvc.perform(post("/authenticate")
  //   .contentType(MediaType.APPLICATION_JSON)
  //   .content(mockJson))
  //   .andExpect(status().isOk())
  //   .andReturn();
    
  //   String token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");

  //    //add a new session
  //     mockJson = "{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"SirSnoopy8\",\"sessionMentor\":null,\"sessionMentee\":\"A\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\":\"SomeTask22020\"}";    

  //     mockMvc.perform(post("/addNewSession")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson))
  //       .andExpect(status().isOk());

  //     //grab that sessionAction
  //   MvcResult result = mockMvc.perform(get("/sessionFromUsername/{username}", "SirSnoopy8"))
  //   .andExpect(status().isOk())
  //   .andReturn();

  // Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$[0].id");

  // String storyId = JsonPath.read(result.getResponse().getContentAsString(), "$[0].sessionStoryId");




  //    mockJson = "[{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"A\",\"sessionMentor\":null,\"sessionMentee\":\"SirSnoopy6\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\": \"" + storyId + "\", \"id\": " + id +"}]";    


  //     mockMvc.perform(post("/overwriteSession")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson)
  //                   .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
  //       .andExpect(status().is(403));


  //   }

  //   @Test
  //   @DisplayName("post /overwriteSession valid info in teammates")
  //   public void postOverwriteSessionRightUser() throws Exception{
  
  
  //     String mockJson9 = "{\"username\":\"SirSnoopy9\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";
      
  //     mockMvc.perform(post("/register")
  //     .contentType(MediaType.APPLICATION_JSON)
  //     .content(mockJson9))
  //     .andExpect(status().is(201))
  //     .andReturn();
  
  //     //register and log in as SirSnoopy10 and put in a new record with sirsnoopy8 in the teammates
  //     String mockJson = "{\"username\":\"SirSnoopy10\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";
      
  //     mockMvc.perform(post("/register")
  //     .contentType(MediaType.APPLICATION_JSON)
  //     .content(mockJson))
  //     .andExpect(status().is(201))
  //     .andReturn();

  //     MvcResult mvcResult = mockMvc.perform(post("/authenticate")
  //     .contentType(MediaType.APPLICATION_JSON)
  //     .content(mockJson))
  //     .andExpect(status().isOk())
  //     .andReturn();
      
  //     //add a new session
  //     mockJson = "{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"SirSnoopy10\",\"sessionMentor\":null,\"sessionMentee\":\"A\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\":\"SomeTask29u\",\"teammates\": [\"SirSnoopy10\",\"SirSnoopy9\"]}";    

  //     mockMvc.perform(post("/addNewSession")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson))
  //       .andExpect(status().isOk());
   
      

  //     //grab that session
  //     MvcResult result = mockMvc.perform(get("/sessionFromUsername/{username}", "SirSnoopy10"))
  //     .andExpect(status().isOk())
  //     .andReturn();

  //   Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$[0].id");

  //   String storyId = JsonPath.read(result.getResponse().getContentAsString(), "$[0].sessionStoryId");

            
  //     //login in as sirsnoopy9 now useing mockJson9
  //     mvcResult = mockMvc.perform(post("/authenticate")
  //     .contentType(MediaType.APPLICATION_JSON)
  //     .content(mockJson9))
  //     .andExpect(status().isOk())
  //     .andReturn();

  //     //get sirsnoopy9's token
  //     String token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");

  //     mockJson = "[{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"A\",\"sessionMentor\":null,\"sessionMentee\":\"SirSnoopy8\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\": \"" + storyId + "\", \"id\": " + id +", \"teammates\": [\"SirSnoopy10\",\"SirSnoopy9\"]}]";    

  //     //overwrite as sirsnoopy9
  //       mockMvc.perform(post("/overwriteSession")
  //                     .contentType(MediaType.APPLICATION_JSON)
  //                     .content(mockJson)
  //                     .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
  //         .andExpect(status().isOk());
  
  
  //     }
  //   @Test
  //   @DisplayName("post /overwriteSession valid info not in teammates")
  //   public void postOverwriteSessionNotInTeammates() throws Exception{
  
  
  //     String mockJson = "{\"username\":\"SirSnoopy6\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";
      
  //     mockMvc.perform(post("/register")
  //     .contentType(MediaType.APPLICATION_JSON)
  //     .content(mockJson))
  //     .andExpect(status().is(201))
  //     .andReturn();
  
  //     MvcResult mvcResult = mockMvc.perform(post("/authenticate")
  //     .contentType(MediaType.APPLICATION_JSON)
  //     .content(mockJson))
  //     .andExpect(status().isOk())
  //     .andReturn();

  //      //add a new session
  //      mockJson = "{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"SirSnoopy8\",\"sessionMentor\":null,\"sessionMentee\":\"A\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\":\"SomeTask239\"}";    

  //      mockMvc.perform(post("/addNewSession")
  //                    .contentType(MediaType.APPLICATION_JSON)
  //                    .content(mockJson))
  //        .andExpect(status().isOk());
 
  //      //grab that session
      
  //     //grab a random snoopy's session where user is not in teammates (teammates is null)
  //     MvcResult result = mockMvc.perform(get("/sessionFromUsername/{username}", "SirSnoopy8"))
  //     .andExpect(status().isOk())
  //     .andReturn();

  //   Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$[0].id");

  //   String storyId = JsonPath.read(result.getResponse().getContentAsString(), "$[0].sessionStoryId");


  //     String token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
  
  
  //      mockJson = "[{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"A\",\"sessionMentor\":null,\"sessionMentee\":\"SirSnoopy6\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\": \"" + storyId + "\", \"id\": " + id +"}]";    

  //       mockMvc.perform(post("/overwriteSession")
  //                     .contentType(MediaType.APPLICATION_JSON)
  //                     .content(mockJson)
  //                     .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
  //         .andExpect(status().is(403));
  
  
  //     }

  //     @Test
  //     @DisplayName("post /overwriteSession valid info match in sessionCreator")
  //     public void postOverwriteSessionRightUserSameCreator() throws Exception{
    
    
  //       String mockJson = "{\"username\":\"SirSnoopy8\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";
        
  //       mockMvc.perform(post("/register")
  //       .contentType(MediaType.APPLICATION_JSON)
  //       .content(mockJson))
  //       .andExpect(status().is(201))
  //       .andReturn();
    
  //       MvcResult mvcResult = mockMvc.perform(post("/authenticate")
  //       .contentType(MediaType.APPLICATION_JSON)
  //       .content(mockJson))
  //       .andExpect(status().isOk())
  //       .andReturn();
        
  //       //add a new session
  //       mockJson = "{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"SirSnoopy8\",\"sessionMentor\":null,\"sessionMentee\":\"A\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\":\"SomeTask29su\"}";    
  
  //       mockMvc.perform(post("/addNewSession")
  //                     .contentType(MediaType.APPLICATION_JSON)
  //                     .content(mockJson))
  //         .andExpect(status().isOk());
  
  //       //grab that session
  //       MvcResult result = mockMvc.perform(get("/sessionFromUsername/{username}", "SirSnoopy8"))
  //       .andExpect(status().isOk())
  //       .andReturn();
  
  //     Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$[0].id");
  
  //     String storyId = JsonPath.read(result.getResponse().getContentAsString(), "$[0].sessionStoryId");
  
  
  //       String token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
    
    
  //        mockJson = "[{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"SirSnoopy8\",\"sessionMentor\":null,\"sessionMentee\":\"SirSnoopy6\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\": \"" + storyId + "\", \"id\": " + id +"}]";    
  
  //         mockMvc.perform(post("/overwriteSession")
  //                       .contentType(MediaType.APPLICATION_JSON)
  //                       .content(mockJson)
  //                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
  //           .andExpect(status().isOk());
    
    
  //       }

  // @Test
  // @DisplayName("post /addNewSession twice with the same storyId info")
	// public void postAddNewSessionSameStoryId() throws Exception{
      
  //   String mockJson = "{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"A\",\"sessionMentor\":null,\"sessionMentee\":\"A\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\":\"SomeTask2\", \"teammates\":[\"A\"]}";    

  //     mockMvc.perform(post("/addNewSession")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson))
  //       .andExpect(status().isOk());

  //     mockMvc.perform(post("/addNewSession")
  //       .contentType(MediaType.APPLICATION_JSON)
  //       .content(mockJson))
  //       .andExpect(status().is(422));

  // }

  // @Test
  // @DisplayName("post /addNewSession with bad time format")
	// public void postAddNewSessionBadTimeFormat() throws Exception{
      
  //   String mockJson = "{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (E)\",\"sessionCreator\":\"A\",\"sessionMentor\":null,\"sessionMentee\":\"A\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\":\"SomeTask3\"}";    

  //     mockMvc.perform(post("/addNewSession")
  //       .contentType(MediaType.APPLICATION_JSON)
  //       .content(mockJson))
  //       .andExpect(status().is(422));


  // }

  // @Test
  // @DisplayName("get /sessionFromUsername valid after adding the session")
	// public void getSessionFromUsernameValid() throws Exception{
      
  //   String mockJson = "{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"SirSnoopy\",\"sessionMentor\":null,\"sessionMentee\":\"SirSnoopy\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\":\"SomeTask3\", \"teammates\": [\"SirSnoopy\"]}";    
  //     String mockUsername = "SirSnoopy";
  //     mockMvc.perform(post("/addNewSession")
  //                 .contentType(MediaType.APPLICATION_JSON)
  //                 .content(mockJson))
  //     .andExpect(status().isOk());

  //     String mockJson2 = "{\"time\":\"Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)\",\"sessionCreator\":\"MrSnoopy1\",\"sessionMentor\":null,\"sessionMentee\":\"SirSnoopy\",\"sessionAction\":\"Debug code for\",\"sessionSubjectMatter\":\"Java\",\"sessionMentorRating\":null,\"sessionMenteeRating\":null,\"sessionMentorComments\":null,\"sessionMenteeComments\":null,\"sessionStoryId\":\"SomeTask33\", \"teammates\": [\"SirSnoopy\", \"MrSnoopy1\"]}";    
  //     mockMvc.perform(post("/addNewSession")
  //                 .contentType(MediaType.APPLICATION_JSON)
  //                 .content(mockJson2))
  //     .andExpect(status().isOk());

  
  //     mockMvc.perform(get("/sessionFromUsername/{username}", mockUsername)
  //                   .contentType(MediaType.APPLICATION_JSON))
  //                   .andExpect(status().isOk())
  //                   .andExpect(jsonPath("$[0].sessionCreator",is("SirSnoopy")))
  //                   .andExpect(jsonPath("$[0].sessionSubjectMatter", is("Java")))
  //                   .andExpect(jsonPath("$[0].sessionStoryId", is("SomeTask3")))
  //                   .andExpect(jsonPath("$[0].sessionAction", is("Debug code for")))
  //                   .andExpect(jsonPath("$.length()", is(2)))
  //                   .andExpect(jsonPath("$[0].length()", is(13)))
  //                   .andExpect(jsonPath("$[0].time", anyOf(is("Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)"),is("Thu Aug 20 2020 17:08:59 GMT+0000 (UTC)"))))
  //                   .andExpect(jsonPath("$[1].sessionCreator",is("MrSnoopy1")))
  //                   .andExpect(jsonPath("$[1].sessionSubjectMatter", is("Java")))
  //                   .andExpect(jsonPath("$[1].sessionStoryId", is("SomeTask33")))
  //                   .andExpect(jsonPath("$[1].sessionAction", is("Debug code for")))
  //                   .andExpect(jsonPath("$[1].length()", is(13)))
  //                   .andExpect(jsonPath("$[1].time", is("Thu Aug 20 2020 13:08:59 GMT-0400 (EDT)")))
  //                   .andExpect(jsonPath("$[1].teammates", hasItem("SirSnoopy")))
  //                   .andExpect(jsonPath("$[1].teammates", hasItem("MrSnoopy1")));
	// }

  
  // @Test
  // @DisplayName("try to post /register same user twice")
	// public void postRegisterCustomerTwiceInvalid() throws Exception{
      
  //     String mockJson = "{\"username\":\"SirSnoopy3\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";

  //     mockMvc.perform(post("/register")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson))
  //       .andExpect(status().is(201));


  //     mockMvc.perform(post("/register")
  //     .contentType(MediaType.APPLICATION_JSON)
  //     .content(mockJson))
  //   .andExpect(status().is(422));

	// }

  //   @Test
  // @DisplayName("post /register invalid user info--nonmatching password and returns an exception from post /validate the user")
	// public void postRegisterCustomerInvalid() throws Exception{
      
  //     String mockJson = "{\"username\":\"SirSnoopy\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"fake1\", \"matchingPassword\": \"fake2\"}";
      
  //     mockMvc.perform(post("/register")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson))
  //       .andExpect(status().is(422));

  //     mockMvc.perform(post("/authenticate")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson))
  //       .andExpect(status().is(401));
	// }
  

  // @Test
  // @DisplayName("post /authenticate valid user info")
	// public void postAuthenticateUserValid() throws Exception{


  //  String mockJson = "{\"username\":\"SirSnoopy\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";

  //  //try catch loop in order to prevent multiple registrations of the same user (leads to ambiguous records when searching by username) -- alterantive is I just register another account
  //  //we can probably change this test by building in restriction from adding the same user twice as well
  //   try{
  //        mockMvc.perform(post("/authenticate")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson))
  //       .andExpect(status().isOk());

  //   }catch(Exception e){
  //        mockMvc.perform(post("/register")
  //         .contentType(MediaType.APPLICATION_JSON)
  //         .content(mockJson))
  //         .andExpect(status().isOk());

  //       mockMvc.perform(post("/authenticate")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson))
  //       .andExpect(status().isOk());
  //   }
  

   
	// }

  // @Test
  // @DisplayName("put /insertCustomer valid user info")
	// public void putInsertCustomerValid() throws Exception {
    
  //   jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
  //   jdbcTemplate.execute("CREATE TABLE customers(" +
  //       "id SERIAL, username VARCHAR(255), first_name VARCHAR(255), last_name VARCHAR(255), encoded_password VARCHAR(255))");

  //   String mockJson = "{\"username\": \"SirSnoopy\", \"firstName\":\"Joe\", \"lastName\": \"Cool\",\"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";
    
  //   mockMvc.perform(put("/insertCustomer")
  //                 .contentType(MediaType.APPLICATION_JSON)
  //                 .content(mockJson))
  //     .andExpect(status().isOk());
    

  //   String query = "SELECT * FROM customers";
    
  //   List<Customer> customerlist = new ArrayList<Customer>();
    
  //   customerlist = jdbcTemplate.query(
  //       query,
  //       (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("username"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("encoded_password"))
  //   );
    
  //   Assertions.assertTrue(customerlist.size()==1);

  //   Assertions.assertTrue(customerlist.get(0).getId()==1);
    
  //   Assertions.assertTrue(customerlist.get(0).getUsername().equals("SirSnoopy"));
    
  //   Assertions.assertTrue(customerlist.get(0).getFirstName().equals("Joe"));

  //   Assertions.assertTrue(customerlist.get(0).getLastName().equals("Cool"));

  //   Assertions.assertTrue(!customerlist.get(0).getEncodedPassword().isEmpty());

	// }

  // @Test
  // @DisplayName("get /total/ valid order no discount")
	// public void getTotalValid() throws Exception {
      
  //     String json = "{\"veggie\":1, \"beef\":2}";
      
  //     mockMvc.perform(get("/total")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(json))
  //       .andExpect(status().isOk())
  //       .andExpect(content().string(containsString("8.50")));

	// }

  // @Test
  // @DisplayName("get /total/ valid order with discount")
	// public void getTotalValidWithDiscount() throws Exception {
      
  //     String json = "{\"veggie\":1, \"beef\":2, \"chorizo\":1 }";
      
  //     mockMvc.perform(get("/total")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(json))
  //       .andExpect(status().isOk())
  //       .andExpect(content().string(containsString("9.60")));

	// }

  // @Test
  // @DisplayName("get /total/ invalid")
	// public void getTotalInvalid() throws Exception {
      
  //     String json = "{\"Burger\":1, \"veggie\":1, \"beef\":2, \"chorizo\":1 }";
      
  //     mockMvc.perform(get("/total")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(json))
  //       .andExpect(status().is(422))
  //       .andExpect(status().reason(containsString("No such item or counts not all whole numbers")))
  //       .andExpect(jsonPath("$.body").doesNotExist());

	// }

  // @Test
  // @DisplayName("get /total/ negative invalid")
	// public void getTotalNegativeNumber() throws Exception {
      
  //     String json = "{\"veggie\":-1, \"beef\":2, \"chorizo\":1 }";
      
  //     mockMvc.perform(get("/total")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(json))
  //       .andExpect(status().is(422))
  //       .andExpect(status().reason(containsString("No such item or counts not all whole numbers")))
  //       .andExpect(jsonPath("$.body").doesNotExist());

	// }

  // @Test
  // @DisplayName("get /total/ invalid 400")
	// public void getTotalInvalid400() throws Exception {
      
  //     String json = "{";
      
  //     mockMvc.perform(get("/total")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(json))
  //       .andExpect(status().is(400))
  //       .andExpect(jsonPath("$.body").doesNotExist());

	// }
  
  
}
