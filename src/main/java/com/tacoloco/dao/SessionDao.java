package com.tacoloco.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tacoloco.model.DAOSession;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;


@Repository
public interface SessionDao extends CrudRepository<DAOSession, Integer> {
    List<DAOSession> findAllBySessionCreator(String sessionCreator);

    @Query("select s from DAOSession s WHERE :username in elements(s.teammates) OR s.sessionCreator = :username")
    List<DAOSession> findAllBySessionCreatorOrTeammates(@Param("username") String username);
}