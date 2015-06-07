package es.david.ptc.util;

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
	private HashMap<String,HashMap<String,Long>> pendingRequests;
	private HashMap<String, Response> pendingResponses;
	static private Requests singleton;
	private RandomString requestIdGenerator;
	public static synchronized Requests singleton(){
		if(singleton==null){
			singleton = new Requests();
		}
		return singleton;
	}
	public Requests() {
		super();
		this.pendingRequests = new HashMap<String,HashMap<String,Long>>();
		this.pendingResponses = new HashMap<String, Response>();
		this.requestIdGenerator = new RandomString(15);
	}
	

	public HashMap<String,HashMap<String,Long>> getPendingRequests() {
		return pendingRequests;
	}

	public void setPendingRequests(HashMap<String,HashMap<String,Long>> pendingRequests) {
		this.pendingRequests = pendingRequests;
	}
	@SuppressWarnings("unchecked")
	public synchronized void backupRequests() throws IOException{
		File fich = new File(nfich);
		if(fich.exists()){
			ObjectMapper om = new ObjectMapper();
			pendingRequests = om.readValue(fich, HashMap.class);
		}
	}
	
	public synchronized void addRequest(String mail, String reqId, long nonce) throws JsonGenerationException, JsonMappingException, IOException{
		HashMap<String, Long> user = pendingRequests.get(reqId);
		if(user == null){
			user = new HashMap<String,Long>();
		}
		user.put(mail, nonce);
		this.pendingRequests.put(reqId, user);
	}
	public synchronized String getRequestId(){
		return this.requestIdGenerator.nextString();
	}
	public synchronized String getMail(String reqId){
		HashMap<String,Long> reg = pendingRequests.get(reqId);
		if(reg!=null){
			return reg.entrySet().iterator().next().getKey();
		}else{
			return null;
		}
	}
	
	public synchronized boolean validNonce(String reqId, String mail, Long nonce){
		HashMap<String,Long> reg = pendingRequests.get(reqId);
		if(reg!=null){
			Long nonceOld = reg.get(mail);
			if(nonce - nonceOld  == 1){
				return true;
			}
		}
		return false;
	}
	public synchronized boolean removeRequest(String mail,String estado, 
											  String pass,String reqId, long nonceIn, 
											  long ts, String iv,int nContainers) throws JsonGenerationException, JsonMappingException, IOException{
		System.out.println("El numero de containers para: "+mail+" es de: "+nContainers);
		Long nonce = this.pendingRequests.get(reqId).get(mail);
		System.out.println("Entra en borrar.");
		if(nonceIn-nonce==1){
			System.out.println("Ha encontrado la peticion");
			if("".equals(estado)){
				Response r = this.pendingResponses.get(reqId);
				if(r!=null){
					r.incrementNResponses();
					System.out.println("Me han contestado ya: "+r.getNresponses());
					if(r.getNresponses()>=nContainers){
						this.pendingRequests.get(mail).remove(reqId);
						notifyAll();
						return true;
					}
				}else{
					r = new Response(estado,pass,iv,ts,nonceIn);
					r.incrementNResponses();
					this.pendingResponses.put(reqId, r);
					if(r.getNresponses()>=nContainers){
						notifyAll();
					}
				}
				return false;
			}else{
				this.pendingRequests.remove(reqId);
				this.pendingResponses.put(reqId, new Response(estado,pass,iv,ts,nonceIn));
				notifyAll();
			}
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
			pass = new Response("timeoutExpired","","",0L,0L);
			pendingRequests.remove(requestId);
		}
		return pass;
	}
}
