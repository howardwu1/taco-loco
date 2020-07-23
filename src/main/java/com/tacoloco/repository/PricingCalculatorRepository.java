package com.tacoloco.repository;

import org.springframework.stereotype.Repository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.tacoloco.model.Customer;
@Repository
public class PricingCalculatorRepository {

  private static final Logger log = LoggerFactory.getLogger(PricingCalculatorRepository.class);

  @Autowired
  JdbcTemplate jdbcTemplate;

 
  public void queryForSingleQualifier(String qualifier, String firstName)
  {
    
    /*
    PreparedStatement statement = jdbcTemplate.prepareStatement("SELECT id, first_name, last_name FROM customers WHERE ? = ?");
    statement.setString(1, qualifier);
    statement.setString(2, firstName);
    ResultSet result = statement.executeQuery();


    while(result.next()) {
      Customer c = new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"));
      log.info(c.toString());
    }*/
    jdbcTemplate.query(
        "SELECT id, first_name, last_name FROM customers WHERE first_name = ?", new Object[]{firstName},
        (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
    ).forEach(customer -> log.info(customer.toString()));
    
  }
  public void queryForMultipleQualifier(String[] qualifier, Object[] value) {
    String query = "SELECT id, first_name, last_name FROM customers WHERE ";
    
    for(int i = 0; i < qualifier.length; i++){
      query = query.concat(qualifier[i]).concat(" = ? AND ");
    }
    query = query.substring(0,query.length()-5);
    
    // log.info(query);
    //for(int i = 0; i < inputs.length; i++){
    // log.info((String)inputs[i]);

    // }

    //log.info("querying for Josh Long:");
 
    jdbcTemplate.query(
        query, value,
        (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
    ).forEach(customer -> log.info(customer.toString()));
    
  }


  public void insertIntoCustomers(String firstName, String lastName, String encodedPassword){
  
    jdbcTemplate.update("INSERT INTO customers(first_name, last_name) VALUES (?,?)", new Object[]{firstName, lastName});
  }

}