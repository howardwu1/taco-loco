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


@Service
public class JwtUserDetailsService implements UserDetailsService {


  @Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	// @Override
	// public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    
    
	// 	if ("javainuse".equals(username)) {
	// 		return new User("javainuse", "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6",
  //     		new ArrayList<>());
	// 	} else {
	// 		throw new UsernameNotFoundException("User not found with username: " + username);
	// 	}

  //   return new User("javainuse", "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6",
  //   new ArrayList<>());
	// }

  @Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		DAOUser user = userDao.findByUsername(username);
		if (user == null) {

			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				new ArrayList<>());
	}

	public DAOUser save(Customer user) {
		DAOUser newUser = new DAOUser();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		return userDao.save(newUser);
	}

}