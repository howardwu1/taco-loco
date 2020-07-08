package com.tacoloco.model;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Customer")
@Data
public class Customer {
  
  // default value because the id is determined by the database 
  private long id = 0;
  private String firstName = "", lastName = "";

  public Customer(long id, String firstName, String lastName) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public Customer() {
  }

  @Override
  public String toString() {
    return String.format(
        "Customer[id=%d, firstName='%s', lastName='%s']",
        id, firstName, lastName);
  }

}