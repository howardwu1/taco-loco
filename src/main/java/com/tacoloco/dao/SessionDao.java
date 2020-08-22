package com.tacoloco.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tacoloco.model.DAOSession;

@Repository
public interface SessionDao extends CrudRepository<DAOSession, Integer> {
    DAOSession findBySessionCreator(String sessionCreator);
}