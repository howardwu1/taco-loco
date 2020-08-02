package com.tacoloco.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tacoloco.model.DAOUser;

@Repository
public interface UserDao extends CrudRepository<DAOUser, Integer> {
  //original link had an error -- said UserDao as the return type for findByUsername
	DAOUser findByUsername(String username);
}