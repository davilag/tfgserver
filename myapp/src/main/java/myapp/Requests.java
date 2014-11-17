package myapp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Clase para almacenar las peticiones que ha hecho un requester y se ha enviado 
 * a un container.
 */
public class Requests {
	private String nfich;
	private HashMap<String,HashMap<String,String>> pendingRequests;
	private HashMap<Integer, String> pendingResponses;
	static private Requests singleton;
	private AtomicInteger requestId;
	public static synchronized Requests singleton(String nfich){
		if(singleton==null){
			singleton = new Requests(nfich);
		}
		return singleton;
	}
	public Requests() {
		super();
		this.pendingRequests = new HashMap<String,HashMap<String,String>>();
	}
	
	public Requests(String nfich) {
		super();
		this.nfich = nfich;
		this.pendingRequests = new HashMap<String,HashMap<String,String>>();
		this.pendingResponses = new HashMap<Integer,String>();
		this.requestId = new AtomicInteger();
		try {
			backupRequests();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public synchronized Integer getRequestId(){
		return this.requestId.getAndIncrement();
	}
	public synchronized String removeRequest(String mail, String dominio, String pass,Integer reqId) throws JsonGenerationException, JsonMappingException, IOException{
		String regId = this.pendingRequests.get(mail).get(dominio);
		this.pendingRequests.get(mail).remove(dominio);
		this.pendingResponses.put(reqId, pass);
		notifyAll();
		saveStatus();
		return regId;
	}
	
	public synchronized String getPass(Integer requestId) throws InterruptedException{
		while(!pendingResponses.containsKey(requestId)){
			wait();
		}
		String pass = pendingResponses.get(requestId);
		pendingResponses.remove(requestId);
		return pass;
	}
}
