package com.tacoloco.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class DAOUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
  @Column(unique=true)
	private String username;
	@Column
	@JsonIgnore
	private String password;

  @Column
  private String firstName;

  @Column
  private String lastName;
  
	// public String getUsername() {
	// 	return username;
	// }

	// public void setUsername(String username) {
	// 	this.username = username;
	// }

	// public String getPassword() {
	// 	return password;
	// }

	// public void setPassword(String password) {
	// 	this.password = password;
	// }

}