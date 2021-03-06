package es.david.httptest;

import java.util.ArrayList;

public class Usuario {
	private String username;
	private String serverKey;
	private ArrayList<Container> containers;
	private ArrayList<Requester>requesters;
	
	public Usuario(){
		containers = new ArrayList<Container>();
		requesters = new ArrayList<Requester>();
	}

	public ArrayList<Container> getContainers() {
		return containers;
	}

	public void setContainers(ArrayList<Container> containers) {
		this.containers = containers;
	}

	public ArrayList<Requester> getRequesters() {
		return requesters;
	}

	public void setRequesters(ArrayList<Requester> requesters) {
		this.requesters = requesters;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean addContainer(String regId){
		return containers.add(new Container(regId, serverKey));
	}
	
	public boolean addRequester (String hostname){
		return requesters.add(new Requester(hostname));
	}
	
	public String getServerKey() {
		return serverKey;
	}

	public void setServerKey(String serverKey) {
		this.serverKey = serverKey;
	}

	public boolean hasRequester(String hostname){
		for(Requester r: this.requesters){
			if(r.getHostname().equals(hostname)){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasContainer(String regId){
		for(Container c: this.containers){
			if(c.getRegId().equals(regId))
				return true;
		}
		return false;
	}
	
	public class Requester {
		private String hostname;
		
		public Requester(){
			super();
		}
		
		public Requester(String hostname){
			super();
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
		
		public Container(String regId, String serverKey) {
			super();
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