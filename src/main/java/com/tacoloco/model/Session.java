package com.tacoloco.model;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

import java.text.SimpleDateFormat;


import javax.validation.constraints.Min;

import java.util.Date;
import java.util.List;

import com.tacoloco.controller.PricingCalculatorController.BadDateParsingException;

import java.util.Arrays;
@Schema(description = "Session")
@Data
public class Session {
  

  // default value because the id is determined by the database 
  private long id = 0;

  @NotNull 
  private String sessionStoryId; Date time; String sessionCreator; String sessionAction; String sessionSubjectMatter;

  private String sessionMentor; String sessionMentee; String sessionMentorComments; String sessionMenteeComments; 

  private String[] teammates = {};

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