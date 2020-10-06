package com.tacoloco.services;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tacoloco.dao.UserDao;
import com.tacoloco.model.DAOUser;
import com.tacoloco.model.Customer;
import com.tacoloco.model.UserDTO;

import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.http.HttpStatus;

import org.springframework.dao.DataIntegrityViolationException;

import com.tacoloco.controller.PricingCalculatorController.UserDetailsNotFoundFromUsernameException;

import java.util.List;


@Service
public class JwtUserDetailsService implements UserDetailsService {


  @Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	public static class DuplicateUsernameException extends RuntimeException {}

  @Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		DAOUser user = userDao.findByUsername(username);
		if (user == null) {

			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				new ArrayList<>());
	}



	public DAOUser updateUser(Customer user) throws UsernameNotFoundException {
		DAOUser daoUser = userDao.findByUsername(user.getUsername());

		if(user.getFirstName() != null){
			daoUser.setFirstName(user.getFirstName());
		}
		if(user.getLastName() != null){
			daoUser.setLastName(user.getLastName());
		}
		if(user.getRole() != null){
			daoUser.setRole(user.getRole());
		}

		return userDao.save(daoUser);
	}
  
	public DAOUser save(Customer user) {

		try{
			
			DAOUser newUser = new DAOUser();
			newUser.setUsername(user.getUsername());
			newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
			newUser.setFirstName(user.getFirstName());
			newUser.setLastName(user.getLastName());
			newUser.setRole(user.getRole());
			return userDao.save(newUser);
		}
		catch(DataIntegrityViolationException e){
			throw new DuplicateUsernameException();
		}
	}

}