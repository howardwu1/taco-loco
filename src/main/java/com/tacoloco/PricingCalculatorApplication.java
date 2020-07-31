package com.tacoloco;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//needed to import Customer class since we put it in a different folder (model)
import com.tacoloco.model.Customer;
import com.tacoloco.services.*;


@SpringBootApplication
public class PricingCalculatorApplication {

  @Autowired PricingCalculatorService pricingCalculatorService;

  
  private static final Logger log = LoggerFactory.getLogger(PricingCalculatorApplication.class);

  @Autowired
  JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {

		SpringApplication.run(PricingCalculatorApplication.class, args);
	}


}




