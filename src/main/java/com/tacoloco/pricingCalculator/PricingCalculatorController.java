package com.tacoloco.pricingCalculator;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.net.URISyntaxException;


import com.tacoloco.pricingCalculatorService.PricingCalculatorService;

import com.tacoloco.model.*;

@Controller
public class PricingCalculatorController {
  
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
 class BadRequestException extends RuntimeException {}

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
  public @ResponseBody double getTotal(@RequestBody Order order) throws BadRequestException, URISyntaxException{

    return pricingCalculatorService.getTotal(order);
     
  }

}