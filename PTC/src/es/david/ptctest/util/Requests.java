package es.david.ptctest.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

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
	private HashMap<String, Response> pendingResponses;
	static private Requests singleton;
	private RandomString requestIdGenerator;
	public static synchronized Requests singleton(String nfich){
		if(singleton==null){
			singleton = new Requests(nfich);
		}
		return singleton;
	}
	public Requests() {
		super();
		this.pendingRequests = new HashMap<String,HashMap<String,String>>();
		this.pendingResponses = new HashMap<String, Response>();
		this.requestIdGenerator = new RandomString(15);
	}
	
	public Requests(String nfich) {
		super();
		this.nfich = nfich;
		this.pendingRequests = new HashMap<String,HashMap<String,String>>();
		this.pendingResponses = new HashMap<String,Response>();
		this.requestIdGenerator = new RandomString(15);
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
	
	public synchronized void addRequest(String mail, String reqId, String dominio) throws JsonGenerationException, JsonMappingException, IOException{
		HashMap<String, String> user = pendingRequests.get(mail);
		if(user == null){
			user = new HashMap<String,String>();
		}
		user.put(reqId, dominio);
		this.pendingRequests.put(mail, user);
		saveStatus();
	}
	public synchronized String getRequestId(){
		return this.requestIdGenerator.nextString();
	}
	public synchronized boolean removeRequest(String mail,String user, String pass,String reqId, String dominioIn) throws JsonGenerationException, JsonMappingException, IOException{
		String dominio = this.pendingRequests.get(mail).get(reqId);
		System.out.println("Entra en borrar.");
		if(dominio.equals(dominioIn)){
			System.out.println("Ha encontrado la peticion");
			this.pendingRequests.get(mail).remove(reqId);
			this.pendingResponses.put(reqId, new Response(user,pass));
			notifyAll();
			saveStatus();
			return true;
		}
		return false;
		
	}
	
	public synchronized Response getResponse(String requestId) throws InterruptedException{
		long originDate = (new Date()).getTime();
		boolean respond = true;
		long timeSleep = Globals.REQUEST_TIMEOUT;
		long timeElapsed = 0;
		long actualDate;
		while(!pendingResponses.containsKey(requestId)){
			if(timeElapsed>=timeSleep){
				respond = false;
				break;
			}else{
				timeSleep -= timeElapsed;
				originDate = (new Date()).getTime();
			}
			wait(timeSleep);
			actualDate = (new Date()).getTime();
			timeElapsed = actualDate - originDate;
		}
		Response pass;
		if(respond){
			 pass = pendingResponses.get(requestId);
			pendingResponses.remove(requestId);
		}else{
			pass = new Response("","timeoutExpired");
			pendingRequests.remove(requestId);
		}
		return pass;
	}
}