package com.tacoloco.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.tacoloco.services.PricingCalculatorService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tacoloco.model.*;

import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;

import javax.validation.Valid;

import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import 
com.fasterxml.jackson.databind.module.SimpleModule;

import
com.tacoloco.customDeserializer.StringOnlyDeserializer;
import com.tacoloco.dao.SessionDao;

import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.Module;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.tacoloco.services.JwtUserDetailsService.DuplicateUsernameException;
import com.tacoloco.services.PricingCalculatorService.DuplicateStoryIdException;


import com.tacoloco.config.JwtTokenUtil;
import com.tacoloco.services.JwtUserDetailsService;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;


import java.net.URI;

import java.util.Collections;
import java.util.Arrays;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

@Controller
@CrossOrigin
public class PricingCalculatorController {

  @Autowired
	AuthenticationManager authenticationManager;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Bean
  public Module customModule() {
      SimpleModule customModule = new SimpleModule();
      customModule.addDeserializer(String.class, new StringOnlyDeserializer());
      return customModule;
  }

	@Autowired
  private JwtTokenUtil jwtTokenUtil;
  
  @Autowired
  private SessionDao sessionDao;

	@Autowired
	private JwtUserDetailsService userDetailsService;

  @Autowired
  private PricingCalculatorService pricingCalculatorService;

  public static final String CLIENT_ID =   "222585927316-l7u0i85iuu3la1putev56uv5hs4mhikl.apps.googleusercontent.com";

  private static final Logger log = LogManager.getLogger(PricingCalculatorController.class);

  //note made this class public to make it accessible to the controller test
  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason="Passwords do not match")
  public class PasswordMismatchException extends RuntimeException {}

  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason="Datetime format error")
  public static class BadDateParsingException extends RuntimeException {}

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
 class BadRequestException extends RuntimeException {}

  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason="No such item or counts not all whole numbers")
  class UnprocessableEntityException extends RuntimeException {}
  
  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason="User does not exist with the username provided")
  public static class UserDetailsNotFoundFromUsernameException extends RuntimeException {}

  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason="User registration missing information")
  public static class UserDetailsNotCompleteException extends RuntimeException {}

  @ExceptionHandler(DuplicateUsernameException.class)
  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason="Duplicate usernames")
  public ResponseEntity<?> processHandler(DuplicateUsernameException ex) {
    return new ResponseEntity(ex, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(DuplicateStoryIdException.class)
  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason="Duplicate Story Id")
  public ResponseEntity<?> processHandler(DuplicateStoryIdException ex) {
    return new ResponseEntity(ex, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  
//want to raise 422 instead of 400 -- this is for the @Valid validation exceptions to reject non-negative numbers
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason="No such item or counts not all whole numbers")
  public ResponseEntity<?> processHandler(MethodArgumentNotValidException ex) {
    return new ResponseEntity(ex, HttpStatus.UNPROCESSABLE_ENTITY);
  }

//want to raise 422 instead of 400 -- this is for the unknown property failures (See application.properties) to reject orders with unknown menu items
  @ExceptionHandler(UnrecognizedPropertyException.class)
  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason="No such item or counts not all whole numbers")
  public ResponseEntity<?> processHandler(UnrecognizedPropertyException ex) {
    return new ResponseEntity(ex, HttpStatus.UNPROCESSABLE_ENTITY);
  }



  @ExceptionHandler(MismatchedInputException.class)
  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason="Datatype incompatibility with Json input")
  public ResponseEntity<?> processHandler(MismatchedInputException ex) {
    return new ResponseEntity(ex, HttpStatus.UNPROCESSABLE_ENTITY);
  }
  
 
  @GetMapping("/")
  @ResponseStatus(HttpStatus.OK)
	public @ResponseBody String greeting() {
		return pricingCalculatorService.sayHello();
	}

  @GetMapping("/publicUserDetails/{username}")
  @ResponseStatus(HttpStatus.OK)
	public @ResponseBody String getPublicUserDetails(@PathVariable String username) throws JsonProcessingException {
		return userDetailsService.getPublicUserDetails(username);
  }
  
  @GetMapping("/allPublicUserDetails")
  @ResponseStatus(HttpStatus.OK)
	public @ResponseBody String getAllPublicUserDetails() throws JsonProcessingException {
		return userDetailsService.getAllPublicInfoFromAllUsers();
	}

  @GetMapping("/sessionFromUsername/{username}")
  @ResponseStatus(HttpStatus.OK)
	public @ResponseBody String getSessionFromCreator(@PathVariable String username) throws JsonProcessingException {
		return pricingCalculatorService.getSessionByUsername(username);
	}


	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody Customer authenticationRequest) throws Exception {

		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new JwtResponse(token));
	}

  @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	public ResponseEntity<?> updateUser(@RequestBody Customer user, @RequestHeader (name="Authorization", required = false) String token) throws Exception {
    
    String username = user.getUsername();
    if(token != null){

      String usernameToken = jwtTokenUtil.getUsernameFromToken(token.substring(7));


      if(user.getFirstName() == null || user.getLastName() ==null){
        if(user.getId() != 0L && username != null){
          if(!usernameToken.equals(username)){
            
            return ResponseEntity.status(403).build();
          }
        }
        else{
          throw new UserDetailsNotCompleteException();
        }
     }

    }

    userDetailsService.updateUser(user);
          
     return ResponseEntity.created(new URI("/publicUserDetails/" + username)).build();
  
	}

  @RequestMapping(value = "/tokensignin", method = RequestMethod.POST)
	public ResponseEntity<?> saveUserOrValidate (String idTokenString) throws Exception {

    
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
    // Specify the CLIENT_ID of the app that accesses the backend:
    .setAudience(Collections.singletonList(CLIENT_ID))
    // Or, if multiple clients access the backend:
    //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
    .build();

// (Receive idTokenString by HTTPS POST)

GoogleIdToken idToken = verifier.verify(idTokenString);
if (idToken != null) {
  Payload payload = idToken.getPayload();

  // Print user identifier
  String userId = payload.getSubject();
  System.out.println("User ID: " + userId);

  // Get profile information from payload
  String email = payload.getEmail();
  boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
  String name = (String) payload.get("name");
  String pictureUrl = (String) payload.get("picture");
  String locale = (String) payload.get("locale");
  String familyName = (String) payload.get("family_name");
  String givenName = (String) payload.get("given_name");

  // Use or store profile information
  Customer user = new Customer();
  user.setUsername(email);
  user.setFirstName(givenName);
  user.setLastName(familyName);
  
  try{
  DAOUser newUser = userDetailsService.save(user);
  }
  catch(Exception e){
    System.out.println("User already registered");
  }
  
  } else {
  System.out.println("Invalid ID token.");
}
  }

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody Customer user) throws Exception {

    if(user.getPassword()!= null && !user.getPassword().equals(user.getMatchingPassword())){
       throw new PasswordMismatchException();
     }
    
     if(user.getPassword() == null || user.getUsername() == null || user.getFirstName() == null || user.getLastName() ==null){
        if(user.getId() == 0L){
        
          throw new UserDetailsNotCompleteException();
        }
     }
    
      DAOUser newUser = userDetailsService.save(user);
     
    
     return ResponseEntity.created(new URI("/publicUserDetails/" + newUser.getUsername())).build();
  
	}

	@RequestMapping(value = "/addNewSession", method = RequestMethod.POST)
	public ResponseEntity<?> saveNewSession(@RequestBody Session session, @RequestHeader (name="Authorization", required = false) String token) throws Exception {


   if(token == null || jwtTokenUtil.getUsernameFromToken(token.substring(7)).equals(session.getSessionCreator())){
    return ResponseEntity.ok(pricingCalculatorService.saveSession(session));
   } else{
     return ResponseEntity.status(403).build();
   }
	}


  @PutMapping(value = "/insertCustomer", consumes = {"application/json"})
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody void insertCustomer(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Username, First name, last name, password, and matching password of the customer being inserted", content=@Content(schema = @Schema(implementation = Customer.class)), required = true) @RequestBody Customer customer){


    //don't do != with strings-- use the equals function or else it looks for the same memory address
     if(!customer.getPassword().equals(customer.getMatchingPassword())){
       throw new PasswordMismatchException();
     }

    String encodedPassword = passwordEncoder.encode(customer.getPassword());
 
    pricingCalculatorService.insertIntoCustomers(customer.getUsername(), customer.getFirstName(), customer.getLastName(), encodedPassword);
  }

 @PutMapping(value = "/validateCustomer", consumes = {"application/json"})
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody void validateCustomer(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "First name, last name, and password of the customer being inserted", content=@Content(schema = @Schema(implementation = Customer.class)), required = true) @RequestBody Customer customer){

    //don't do != with strings-- use the equals function or else it looks for the same memory address
 
    pricingCalculatorService.checkPassword(customer.getFirstName(), customer.getLastName(), customer.getPassword());

  }

  @PostMapping(value = "/overwriteSession", consumes = { "application/json", "application/xml" })
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> overwriteSession(@RequestBody DAOSession[] sessions, @RequestHeader (name="Authorization", required = false) String token) throws Exception {

    //conscious decision to leave this as OK (200) -- I want to return the DAOSession (so I won't use 204 status) but I don't want to provide an endpoint to query Session by sessionid -- not ever used by the client, always get all sessions by username instead
    if(token != null){
      
      String usernameToken = jwtTokenUtil.getUsernameFromToken(token.substring(7));
      //verify original sessionCreator
      String sessionCreatorInOverwrite = sessions[0].getSessionCreator();

      Long idOfOverwrite = sessions[0].getId();
      System.out.println("idofoverwrite" + idOfOverwrite);
      //if idOfOverwrite is 0 it means it's writing a new record because 0 is the default value when not specified
      if(idOfOverwrite == 0){
        
  
        //restrict overwriting of new session with different user than the person submiting
        if(!sessionCreatorInOverwrite.equals(usernameToken)){
          return ResponseEntity.status(403).build();
        }
      }
      else{

        DAOSession daoSession = sessionDao.findById(idOfOverwrite);

        //catches an overwrite where the person overwriting is not a teammate of the original story
        if (!Arrays.asList(daoSession.getTeammates()).contains(usernameToken) && !daoSession.getSessionCreator().equals(usernameToken)){
          return ResponseEntity.status(403).build();
        } 
      }

    }
    
    return ResponseEntity.ok(pricingCalculatorService.overwriteSession(sessions));

    // if(token == null || jwtTokenUtil.getUsernameFromToken(token.substring(7)).equals(sessions[0].getSessionMentee()) ||jwtTokenUtil.getUsernameFromToken(token.substring(7)).equals(sessions[0].getSessionMentor())){
    //   return ResponseEntity.ok(pricingCalculatorService.overwriteSession(sessions));
    // } else{
    //    return ResponseEntity.status(403).build();
    //  }

  }
  
  @GetMapping(value = "/total", consumes = { "application/json", "application/xml" })
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody String getTotal(
  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Order with names of items and quantity of each",
                    content = @Content(schema = @Schema(implementation = Order.class)), required = true) @RequestBody @Valid Order order) throws BadRequestException, URISyntaxException, JsonProcessingException{
        
  return pricingCalculatorService.getTotal(order);
  }


	private void authenticate(String username, String password) throws Exception {

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
      System.out.println("******************************** " + password);
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
    
      System.out.println("******************************** " + password);
			throw new BadCredentialsException("INVALID_CREDENTIALS", e);
		} catch (Exception e ){
      System.out.println("******************************* " + e);
      throw new BadCredentialsException("INVALID_CREDENTIALS", e);

    }
	}

  

}