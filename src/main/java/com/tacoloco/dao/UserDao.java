package com.tacoloco.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tacoloco.model.DAOUser;

import java.util.List;


@Repository
public interface UserDao extends CrudRepository<DAOUser, Integer> {
  //original link had an error -- said UserDao as the return type for findByUsername
  DAOUser findByUsername(String username);
  List<DAOUser> findAll();

  //testing only 
  void deleteAll();

}