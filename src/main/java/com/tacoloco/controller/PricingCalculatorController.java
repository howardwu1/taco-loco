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

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.beans.factory.annotation.Autowired;

//todo: create more fields for storing passwords for the user -- or the hashed value?
//todo: figure out if I need to salt the hash?


@Controller
public class PricingCalculatorController {

  @Bean
  public Module customModule() {
      SimpleModule customModule = new SimpleModule();
      customModule.addDeserializer(String.class, new StringOnlyDeserializer());
      return customModule;
  }

  @Autowired
  BCryptPasswordEncoder passwordEncoder;

  private static final Logger log = LogManager.getLogger(PricingCalculatorController.class);

  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason="Passwords do not match")
  class PasswordMismatchException extends RuntimeException {}

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
 class BadRequestException extends RuntimeException {}

  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason="No such item or counts not all whole numbers")
  class UnprocessableEntityException extends RuntimeException {}

  private final PricingCalculatorService pricingCalculatorService;
	
  public PricingCalculatorController(PricingCalculatorService pricingCalculatorService){
    this.pricingCalculatorService = pricingCalculatorService;
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

  @PutMapping(value = "/insertCustomer", consumes = {"application/json"})
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody void insertCustomer(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "First name, last name, password, and matching password of the customer being inserted", content=@Content(schema = @Schema(implementation = Customer.class)), required = true) @RequestBody Customer customer){

    //todo: encode the password here using bcrypt
    //todo: do validation on the password and matching password
    //todo: create another endpoint to login

    //don't do != with strings-- use the equals function or else it looks for the same memory address
    if(!customer.getPassword().equals(customer.getMatchingPassword())){
      throw new PasswordMismatchException();
    }

    String encodedPassword = passwordEncoder.encode(customer.getPassword());
 
    pricingCalculatorService.insertIntoCustomers(customer.getFirstName(), customer.getLastName(), encodedPassword);
  }


  @PostMapping(value = "/total", consumes = { "application/json", "application/xml" })
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody String getTotal(
  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Order with names of items and quantity of each",
                    content = @Content(schema = @Schema(implementation = Order.class)), required = true) @RequestBody @Valid Order order) throws BadRequestException, URISyntaxException, JsonProcessingException{
        
  return pricingCalculatorService.getTotal(order);
  }


  

}