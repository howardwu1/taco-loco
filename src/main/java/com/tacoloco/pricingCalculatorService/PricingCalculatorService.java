package com.tacoloco.pricingCalculatorService;

import org.springframework.stereotype.Service;
import com.tacoloco.model.*;

@Service
public class PricingCalculatorService {
  
  public String sayHello() {
    return "hello world";
  }

  public String getTotal(Order order) { 
    return order.getTotalPrice();
  }

  public Boolean isEmpty(Order order) {
    return order.isEmpty();
  }
}