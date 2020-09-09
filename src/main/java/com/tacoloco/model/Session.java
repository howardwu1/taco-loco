package com.tacoloco.model;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

import java.text.SimpleDateFormat;

import java.text.DateFormat;

import javax.validation.constraints.Min;

import java.util.Date;

import com.tacoloco.controller.PricingCalculatorController.BadDateParsingException;

@Schema(description = "Session")
@Data
public class Session {
  

  // default value because the id is determined by the database 
  private long id = 0;

  @NotNull 
  private String sessionStoryId; Date time; String sessionCreator; String sessionAction; String sessionSubjectMatter; String[] teammates;

  private String sessionMentor; String sessionMentee; String sessionMentorComments; String sessionMenteeComments; 

  @Min(value = 0, message = "Rating should not be less than 0")
  private Integer sessionMentorRating; Integer sessionMenteeRating;
  
  //service-testing-only constructor
  public Session(String sessionStoryId, String time, String sessionCreator,  String sessionAction, String sessionSubjectMatter) throws java.text.ParseException {
    this.sessionStoryId = sessionStoryId;
    this.setTime(time);
    this.sessionSubjectMatter = sessionSubjectMatter;
    this.sessionCreator = sessionCreator;
    this.sessionAction = sessionAction;
    }

  public void setTime(String time) {
    try{
      this.time = new SimpleDateFormat("EE MMM d y H:m:s 'GMT'Z (zz)").parse(time);
    }
    catch (java.text.ParseException e){
      throw new BadDateParsingException();
    }  
  }

  public Session() {
  }

}