package com.tacoloco.pricingCalculator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 

import com.tacoloco.repository.PricingCalculatorRepository;

import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.doNothing;


import com.tacoloco.model.*;

import static org.mockito.Mockito.verify;

import org.springframework.jdbc.core.JdbcTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(value="stillatest")
class PricingCalculatorRepositoryTests {
  
  @Autowired
  private PricingCalculatorRepository repository;

  @Autowired
  JdbcTemplate jdbcTemplate;

  private static final Logger log = LoggerFactory.getLogger(PricingCalculatorRepositoryTests.class);

  @Test
  @DisplayName("Send to database with name 'Joe Cool' and username 'SirSnoopy' and is valid SQL")
  void sendDatabaseWithJoeCool() throws JsonProcessingException {

    log.info("Creating tables");

    jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
    jdbcTemplate.execute("CREATE TABLE customers(" +
        "username VARCHAR(255), id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255), encoded_password VARCHAR(255))");


    repository.insertIntoCustomers("SirSnoopy","Joe", "Cool", "encodedPasswordSample");
    

    String query = "SELECT * FROM customers";
    
    List<Customer> customerlist = new ArrayList<Customer>();
    
    customerlist = jdbcTemplate.query(
        query,
        (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("username"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("encoded_password"))
    );
    
    Assertions.assertTrue(customerlist.size()==1);

    Assertions.assertTrue(customerlist.get(0).getId()==1);

    Assertions.assertTrue(customerlist.get(0).getUsername().equals("SirSnoopy"));
    
    Assertions.assertTrue(customerlist.get(0).getFirstName().equals("Joe"));

    Assertions.assertTrue(customerlist.get(0).getLastName().equals("Cool"));

    Assertions.assertTrue(customerlist.get(0).getEncodedPassword().equals("encodedPasswordSample"));
  }
}