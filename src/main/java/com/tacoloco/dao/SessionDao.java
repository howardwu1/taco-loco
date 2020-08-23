package com.tacoloco.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tacoloco.model.DAOSession;

import java.util.List;

@Repository
public interface SessionDao extends CrudRepository<DAOSession, Integer> {
    List<DAOSession> findAllBySessionCreator(String sessionCreator);
}