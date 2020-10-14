package com.tacoloco.model;

import java.io.Serializable;

public class JwtResponse implements Serializable {

	// private static final long serialVersionUID = -8091879091924046844L;
	private final String jwttoken;

	public JwtResponse(String jwttoken) {
		this.jwttoken = jwttoken;
	}
	
	//note the no @Data annotation meaning this is a the only getter so when it gets serialized into json it will only have token as its field not jwttoken
	public String getToken() {
		return this.jwttoken;
	}
}