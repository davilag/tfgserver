package es.david.ptctest.util;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Usuario {
	@JsonProperty("username") private String username;
	@JsonProperty("serverKey") private String serverKey;
	@JsonProperty("containers") private ArrayList<String> containers;
	
	public Usuario(){
		containers = new ArrayList<String>();
	}
	
	@JsonCreator
	public Usuario(@JsonProperty("username")String username, @JsonProperty("serverKey")String serverKey,
					@JsonProperty("containers") ArrayList<String> containers){
		this.username = username;
		this.serverKey = serverKey;
		this.containers = containers;
		
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public void setServerKey(String serverKey) {
		this.serverKey = serverKey;
	}

	public ArrayList<String> getContainers() {
		return containers;
	}


	
	public String getUsername() {
		return username;
	}
	public boolean addContainer(String regId){
		return containers.add(regId);
	}
	

	
	public String getServerKey() {
		return serverKey;
	}
	
	public boolean hasContainer(String regId){
		for(String s: this.containers){
			if(s.equals(regId))
				return true;
		}
		return false;
	}
	
	public ArrayList<String> containersIds(){
		ArrayList<String> containersIds = new ArrayList<String>();
		for(String s: this.containers){
			containersIds.add(s);
		}
		return containersIds;
	}
	
	public class Requester {
		@JsonProperty("hostname") private String hostname;
		
		public Requester(){
			super();
		}
		
		@JsonCreator
		public Requester( String hostname){
			this.hostname = hostname;
			
		}
		public String getHostname() {
			return hostname;
		}

		public void setHostname(String hostname) {
			this.hostname = hostname;
		}
	}
	
	public class Container{
		private String regId;
		
		public Container(){
			super();
		}

		public Container(String regId) {
			this.regId = regId;
		}

		public String getRegId() {
			return regId;
		}
		public void setRegId(String regId) {
			this.regId = regId;
		}
	}
}