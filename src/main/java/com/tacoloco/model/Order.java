package com.tacoloco.model;

import lombok.Data;

@Data
public class Order{

  public Integer veggie = 0;
  public Integer chicken = 0;
  public Integer beef = 0;
  public Integer chorizo = 0;

  public static final double VEGPRICE = 2.50;
  public static final double CHICKPRICE = 3;
  public static final double BEEFPRICE = 3;
  public static final double CHORIZPRICE = 3.50;
  
  public String getTotalPrice(){

    double totalPrice = veggie*VEGPRICE + chicken*CHICKPRICE + beef*BEEFPRICE + chorizo*CHORIZPRICE;

    if (veggie + chicken + beef + chorizo >= 4) {
      return String.format("%.2f",totalPrice * 0.80);
    }
    else {
      return String.format("%.2f",totalPrice);
    }
  }

  public boolean isEmpty(){
    return this.getTotalPrice().equals("0.00");
  }
}


