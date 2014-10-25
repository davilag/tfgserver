package myapp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Requests {
	private String nfich;
	private HashMap<String,HashMap<String,String>> pendingRequests;

	
	public Requests() {
		super();
		this.pendingRequests = new HashMap<String,HashMap<String,String>>();
	}
	
	public Requests(String nfich) {
		super();
		this.nfich = nfich;
		this.pendingRequests = new HashMap<String,HashMap<String,String>>();
	}

	public HashMap<String,HashMap<String,String>> getPendingRequests() {
		return pendingRequests;
	}

	public void setPendingRequests(HashMap<String,HashMap<String,String>> pendingRequests) {
		this.pendingRequests = pendingRequests;
	}
	private void saveStatus() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper om = new ObjectMapper();
		om.writeValue(new File(nfich), this.pendingRequests);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void backupRequests() throws IOException{
		File fich = new File(nfich);
		if(fich.exists()){
			ObjectMapper om = new ObjectMapper();
			pendingRequests = om.readValue(fich, HashMap.class);
		}
	}
	
	public synchronized void addRequest(String mail, String dominio, String regId) throws JsonGenerationException, JsonMappingException, IOException{
		HashMap<String, String> user = pendingRequests.get(mail);
		if(user == null){
			user = new HashMap<String,String>();
		}
		user.put(dominio, regId);
		this.pendingRequests.put(mail, user);
		saveStatus();
	}
	
	public synchronized String removeRequest(String mail, String dominio) throws JsonGenerationException, JsonMappingException, IOException{
		String regId = this.pendingRequests.get(mail).get(dominio);
		this.pendingRequests.get(mail).remove(dominio);
		saveStatus();
		return regId;
	}
}