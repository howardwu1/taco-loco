package com.tacoloco.model;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Schema(description = "Taco Order")
@Data
public class Order{

  @Min(value = 0, message = "Number should not less than 0")
  @Schema(description = "Number of Veggie Burritos", example = "2", required = false)
  private int veggie = 0;

  @Min(value = 0, message = "Number should not less than 0")
  @Schema(description = "Number of Chicken Burritos", example = "1", required = false)
  private int chicken = 0;

  @Min(value = 0, message = "Number should not less than 0")
  @Schema(description = "Number of Beef Burritos", example = "2", required = false)
  private int beef = 0;

  @Min(value = 0, message = "Number should not less than 0")
  @Schema(description = "Number of Chorizo Burritos", example = "2", required = false)
  private int chorizo = 0;

  private static final double VEGPRICE = 2.50;
  private static final double CHICKPRICE = 3;
  private static final double BEEFPRICE = 3;
  private static final double CHORIZPRICE = 3.50;

   //need custom setters in order to do value validation because default setters will truncate floats and convert to ints, when they should be invalid. Also it's a good spot to validate for negative values.
  // public void setVeggie(String veg) {
  //       try {
  //         veggie = Integer.parseInt(veg);
          
  //         if (veggie < 0){
  //           throw new IllegalArgumentException("veggie must be a positive number");
  //         }
  //       } catch (NumberFormatException e) {
  //          throw new IllegalArgumentException("veggie must be a whole number");
  //       }
  // }
  //   public void setChicken(String chick) {

  //       try {
  //           chicken = Integer.parseInt(chick);

  //           if (chicken < 0){
  //             throw new IllegalArgumentException("chicken must be a positive number");
  //           }
  //       } catch (NumberFormatException e) {
  //          throw new IllegalArgumentException("chicken must be a whole number");
  //       }
  // }
  // public void setBeef(String beefval) {


  //       try {
  //           beef = Integer.parseInt(beefval);

  //           if (beef < 0){
  //             throw new IllegalArgumentException("beef must be a positive number");
  //           }
  //       } catch (NumberFormatException e) {
  //          throw new IllegalArgumentException("beef must be a whole number");
  //       }
  // }

  //   public void setChorizo(String choriz) {

  //       try {
  //           chorizo = Integer.parseInt(choriz);

  //           if (chorizo < 0){
  //             throw new IllegalArgumentException("chorizo must be a positive number");
  //           }
  //       } catch (NumberFormatException e) {
  //          throw new IllegalArgumentException("chorizo must be a whole number");
  //       }
  // }

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


