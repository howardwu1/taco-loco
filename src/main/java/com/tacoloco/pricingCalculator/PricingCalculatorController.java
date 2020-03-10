package com.tacoloco.pricingCalculator;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.tacoloco.pricingCalculator.pricingCalculatorService.PricingCalculatorService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tacoloco.model.*;


@Controller
public class PricingCalculatorController {
  
  private static final Logger logger = LogManager.getLogger(PricingCalculatorController.class);

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
 class BadRequestException extends RuntimeException {}

  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason="No such item or counts not all whole numbers")
  class UnprocessableEntityException extends RuntimeException {}
//   @ExceptionHandler(BadRequestException.class)
//   @ResponseStatus(HttpStatus.BAD_REQUEST)
//   @ResponseBody
//   public ResponseEntity<BadRequestException> handleInvalidAddressException(BadRequestException e)
//   {
//     return ResponseEntity.created(new URI("/total/")).body(e);
//   }

//   @ExceptionHandler(URISyntaxException.class)
//   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//   @ResponseBody
//   public ResponseEntity<URISyntaxException> handleURISyntaxException(URISyntaxException e)
//   {
//     return ResponseEntity.created(new URI("/total/")).body(e);
//   }

  private final PricingCalculatorService pricingCalculatorService;
	
  public PricingCalculatorController(PricingCalculatorService pricingCalculatorService){
    this.pricingCalculatorService = pricingCalculatorService;}
  


  @GetMapping("/")
  @ResponseStatus(HttpStatus.OK)
	public @ResponseBody String greeting() {
		return pricingCalculatorService.sayHello();
	}

  @GetMapping("/total")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody String getTotal(@RequestBody String jsonString) throws BadRequestException, URISyntaxException, JsonProcessingException{
      
      
      if (pricingCalculatorService.isInvalidOrder(jsonString)) { 
      
         throw new UnprocessableEntityException();
       }
       else{
          Order order = new ObjectMapper().readValue(jsonString, Order.class);
          return pricingCalculatorService.getTotal(order);

       }

  
  }

}