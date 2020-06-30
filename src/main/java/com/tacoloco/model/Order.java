package com.tacoloco.model;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;

@Schema(description = "Taco Order")
@Data
public class Order{
  
  @Min(value = 0, message = "Number should not be less than 0")
  @Schema(description = "Number of Veggie Burritos", example = "2", required = false)
  private int veggie = 0;

  @Min(value = 0, message = "Number should not be less than 0")
  @Schema(description = "Number of Chicken Burritos", example = "1", required = false)
  private int chicken = 0;

  @Min(value = 0, message = "Number should not be less than 0")
  @Schema(description = "Number of Beef Burritos", example = "2", required = false)
  private int beef = 0;

  @Min(value = 0, message = "Number should not be less than 0")
  @Schema(description = "Number of Chorizo Burritos", example = "2", required = false)
  private int chorizo = 0;

  private static final double VEGPRICE = 2.50;
  private static final double CHICKPRICE = 3;
  private static final double BEEFPRICE = 3;
  private static final double CHORIZPRICE = 3.50;

  public String calculateTotalPrice(){

    double totalPrice = veggie*VEGPRICE + chicken*CHICKPRICE + beef*BEEFPRICE + chorizo*CHORIZPRICE;

    if (veggie + chicken + beef + chorizo >= 4) {
      return String.format("%.2f",totalPrice * 0.80);
    }
    else {
      return String.format("%.2f",totalPrice);
    }
  }

}


