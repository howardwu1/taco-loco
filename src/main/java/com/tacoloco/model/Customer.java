package com.tacoloco.model;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

@Schema(description = "Customer")
@Data
public class Customer {
  
  // default value because the id is determined by the database 
  private long id = 0;

  @NotNull
  private String username, firstName, lastName;

  @NotNull
  private String password, matchingPassword;

  private String encodedPassword;

  public Customer(long id, String username, String firstName, String lastName, String encodedPassword) {
    this.id = id;
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.encodedPassword = encodedPassword;
  }

  public Customer() {
  }

  @Override
  public String toString() {
    return String.format(
        "Customer[id=%d, username='%s', firstName='%s', lastName='%s']",
        id, username, firstName, lastName);
  }

}