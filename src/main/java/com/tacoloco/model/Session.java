package com.tacoloco.model;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

import java.text.DateFormat;

@Schema(description = "Session")
@Data
public class Session {
  
  // default value because the id is determined by the database 
  private long id = 0;

  @NotNull 
  private String storyId; DateFormat datetime; String sessionCreator; String sessionAction; String sessionSubjectMatter;

  private String sessionMentor; String sessionMentee; String sessionMentorComments; String sessionMenteeComments; 

  @Min(value = 0, message = "Rating should not be less than 0")
  private Integer sessionMentorRating; Integer sessionMenteeRating;
  
  //testing-only constructor
  public Session(String storyId, String dateTimeString, String sessionCreator, String sessionAction, String sessionSubjectMatter) {
    this.storyId = storyId;
    this.dateTime = new SimpleDateFormat("EE MMM d y H:m:s 'GMT'Z (zz)").parse(dateTimeString);
    this.sessionCreator = sessionCreator;
    this.sessionAction = sessionAction;
  }

  public Session() {
  }

}