package com.tacoloco.repository;

import org.springframework.stereotype.Repository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.tacoloco.model.Customer;
@Repository
public class PricingCalculatorRepository {

  private static final Logger log = LoggerFactory.getLogger(PricingCalculatorRepository.class);

  @Autowired
  JdbcTemplate jdbcTemplate;

 
  public void queryForFirstName(String firstName)
  {
    jdbcTemplate.query(
        "SELECT id, first_name, last_name FROM customers WHERE first_name = ?", new Object[] { firstName },
        (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
    ).forEach(customer -> log.info(customer.toString()));
  }
}