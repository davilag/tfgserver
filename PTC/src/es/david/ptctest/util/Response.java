package es.david.ptctest.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {
	private String username;
	private String passwd;
	public Response(){
		super();
	}
	@JsonCreator
	public Response(@JsonProperty("username")String username, @JsonProperty("passwd") String passwd) {
		super();
		this.username = username;
		this.passwd = passwd;
	}
	public String getUsername() {
		return username;
	}
	public String getPasswd() {
		return passwd;
	}
}
