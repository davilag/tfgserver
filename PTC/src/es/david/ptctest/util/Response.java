package es.david.ptctest.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {
	private String username;
	private String passwd;
	private int nresponses;
	
	public Response(){
		super();
	}
	@JsonCreator
	public Response(@JsonProperty("username")String username, @JsonProperty("passwd") String passwd) {
		super();
		this.username = username;
		this.passwd = passwd;
		this.nresponses = 0;
	}
	public String getUsername() {
		return username;
	}
	public String getPasswd() {
		return passwd;
	}
	public int getNresponses() {
		return nresponses;
	}
	public synchronized void incrementNResponses() {
		this.nresponses++;
	}
}
