package com.tacoloco.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import lombok.Data;

import java.text.DateFormat;

import java.util.Date;
import java.util.List;

import java.text.SimpleDateFormat;

import com.tacoloco.controller.PricingCalculatorController.BadDateParsingException;

import java.util.Arrays;

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

  @Column
  @ElementCollection
  private String[] teammates;

  //note I'm doing this because I'm cheating and we really do need to define session first and then convert to DAOSession
  public void setTime(String time) {
    try{
      this.time = new SimpleDateFormat("EE MMM d y H:m:s 'GMT'Z (zz)").parse(time);
    }
    catch (java.text.ParseException e){
      throw new BadDateParsingException();
    }  
  }

  public void setTime(Date time) {

    this.time = time;
    
     
  }

  public void setTeammates(String[] teammates){
    this.teammates = teammates;
  }

}