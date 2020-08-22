package com.tacoloco.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import lombok.Data;

import java.text.DateFormat;

import java.util.Date;

@Entity
@Table(name = "session")
@Data
public class DAOSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	  
	@Column(unique=true)
	private String sessionStoryId;

	@Column
  private Date time;

  @Column
  private String sessionCreator;

  @Column
  private String sessionAction;
  
  @Column
  private String sessionSubjectMatter;

  @Column
  private String sessionMentor;

  @Column
  private String sessionMentee;

  @Column
  private Integer sessionMentorRating;

  @Column
  private Integer sessionMenteeRating;

  @Column
  private String sessionMentorComments;

  @Column
  private String sessionMenteeComments;

}