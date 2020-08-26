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
	private JwtUserDetailsService userDetailsService;

  @Autowired
  private PricingCalculatorService pricingCalculatorService;


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

  @GetMapping("/sessionFromCreator/{sessionCreator}")
  @ResponseStatus(HttpStatus.OK)
	public @ResponseBody String getSessionFromCreator(@PathVariable String sessionCreator) throws JsonProcessingException {
		return pricingCalculatorService.getSessionByCreator(sessionCreator);
	}


	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody Customer authenticationRequest) throws Exception {

		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new JwtResponse(token));
	}

  
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody Customer user) throws Exception {

    if(!user.getPassword().equals(user.getMatchingPassword())){
       throw new PasswordMismatchException();
     }

		return ResponseEntity.ok(userDetailsService.save(user));
	}

	@RequestMapping(value = "/addNewSession", method = RequestMethod.POST)
	public ResponseEntity<?> saveNewSession(@RequestBody Session session) throws Exception {

		return ResponseEntity.ok(pricingCalculatorService.saveSession(session));
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

  @PostMapping(value = "/total", consumes = { "application/json", "application/xml" })
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