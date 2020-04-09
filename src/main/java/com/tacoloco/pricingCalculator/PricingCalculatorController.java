package com.tacoloco.pricingCalculator;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;

import java.net.URISyntaxException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.tacoloco.pricingCalculator.pricingCalculatorService.PricingCalculatorService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tacoloco.model.*;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;

@Controller
public class PricingCalculatorController {
  
  private static final Logger logger = LogManager.getLogger(PricingCalculatorController.class);

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
 class BadRequestException extends RuntimeException {}

  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason="No such item or counts not all whole numbers")
  class UnprocessableEntityException extends RuntimeException {}

  private final PricingCalculatorService pricingCalculatorService;
	
  public PricingCalculatorController(PricingCalculatorService pricingCalculatorService){
    this.pricingCalculatorService = pricingCalculatorService;}
  


  @GetMapping("/")
  @ResponseStatus(HttpStatus.OK)
	public @ResponseBody String greeting() {
		return pricingCalculatorService.sayHello();
	}

//because of flaky tests I needed to use a String of the json instead of the actual Order object and I have to utilize RequestBody annoataion from swagger.
  @PostMapping(value = "/total", consumes = { "application/json", "application/xml" })
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody String getTotal(
  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Order with names of items and quantity of each",
                    content = @Content(schema = @Schema(implementation = Order.class)), required = true) @RequestBody String order) throws BadRequestException, URISyntaxException, JsonProcessingException{
      
      
      if (pricingCalculatorService.isInvalidOrder(order)) { 
      
         throw new UnprocessableEntityException();
       }
       else{

          //changing implementation to help with flaky test -- probably because order objects rhave different references even though they have the same values. Thus using jsonString since that is passed in. The alternative is to use any order.class in my test.
          //return pricingCalculatorService.getTotal(new ObjectMapper().readValue(jsonString, Order.class));
          return pricingCalculatorService.getTotal(order);

       }

  
  }

}