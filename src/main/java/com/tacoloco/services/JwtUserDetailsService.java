package com.tacoloco.services;

import java.util.ArrayList;

import com.tacoloco.services.PricingCalculatorService;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class JwtUserDetailsService implements UserDetailsService {

  @Autowired
  PricingCalculatorService pricingCalculatorService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		if ("javainuse".equals(username)) {
			return new User("javainuse", "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6",
      		new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}
}