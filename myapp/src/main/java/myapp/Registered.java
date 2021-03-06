package myapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Clase para almacenar las regIds que han dado de alta los diferentes usuarios del servicio.
 */
public class Registered {

	static private Registered single;
	static public synchronized Registered singleton(String nfich) {
		if(single == null){
			single = new Registered(nfich);
		}
		return single;
	}
	
	private String file;
	private HashMap<String,LinkedHashMap<String,ArrayList<String>>> registered;
	
	public Registered(){
		this.registered = new HashMap<String,LinkedHashMap<String,ArrayList<String>>>();
	}
	public Registered(String file){
		this.file =file;
		this.registered = new HashMap<String,LinkedHashMap<String,ArrayList<String>>>();
		try {
			backupUsers();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void backupUsers() throws IOException{
		File fich = new File(file);
		if(fich.exists()){
			ObjectMapper om = new ObjectMapper();
			registered = om.readValue(fich, HashMap.class);
		}
	}
	public synchronized boolean addRegId(String mail, String regId,String role) throws Exception{
		LinkedHashMap<String,ArrayList<String>> user = registered.get(mail);
		boolean reg = false;
		if(user!=null){
			if(role.equals(Globals.ACTION_REQUESTER)){
				reg = user.get(Globals.REQUESTER_IDS).add(regId);
			}else if(role.equals(Globals.ACTION_CONTAINER)){
				reg = user.get(Globals.CONTAINERS_IDS).add(regId);
			}
		}else{
			user = new LinkedHashMap<String,ArrayList<String>>();
			user.put(Globals.CONTAINERS_IDS, new ArrayList<String>());
			user.put(Globals.REQUESTER_IDS, new ArrayList<String>());
			if(role.equals(Globals.ACTION_REQUESTER)){
				reg = user.get(Globals.REQUESTER_IDS).add(regId);
			}else if(role.equals(Globals.ACTION_CONTAINER)){
				reg = user.get(Globals.CONTAINERS_IDS).add(regId);
			}
		}
		registered.put(mail, user);
		ObjectMapper om = new ObjectMapper();
		om.writeValue(new File(file), registered);
		return reg;
	}
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public HashMap<String,LinkedHashMap<String,ArrayList<String>>> getRegistered() {
		HashMap<String,LinkedHashMap<String,ArrayList<String>>> hashRet = new HashMap<String,LinkedHashMap<String,ArrayList<String>>>();
		Set<String> keys = this.registered.keySet();
		for(String s: keys){
			hashRet.put(s, this.registered.get(s));
		}
		
		return hashRet;
	}
	public void setRegistered(HashMap<String,LinkedHashMap<String,ArrayList<String>>> registered) throws Exception{
		this.registered = registered;
		ObjectMapper om = new ObjectMapper();
		om.writeValue(new File(file), registered);
	}
	public synchronized int getNUsers(){
		return registered.size();
	}
	
	public synchronized String[] containers(String mail){
		ArrayList<String> containers = this.registered.get(mail).get(Globals.CONTAINERS_IDS);
		String[] ret = new String[containers.size()];
		containers.toArray(ret);
		return ret;
	}
	public synchronized String[] getClearNotifIds(String mail,String regId){
		ArrayList<String> containers = this.registered.get(mail).get(Globals.CONTAINERS_IDS);
		String[] ret = new String[containers.size()-1];
		int i = 0;
		for(String s: containers){
			if(!s.equals(regId)){
				ret[i] = s;
				i++;
			}
		}
		return ret;
	}
	public synchronized boolean hasRegId(String role, String mail, String regId){
		HashMap<String,ArrayList<String>> user = this.registered.get(mail);
		if(Globals.ACTION_CONTAINER.equals(role)){
			ArrayList<String> containers = user.get(Globals.CONTAINERS_IDS);
			return containers.contains(regId);
		}else if(Globals.ACTION_REQUESTER.equals(role)){
			ArrayList<String> requesters = user.get(Globals.REQUESTER_IDS);
			return requesters.contains(regId);
		}
		return false;
	}
	
	private int cont;
	public String getNumber() {
		return (new Integer(cont++)).toString();
	}
}
