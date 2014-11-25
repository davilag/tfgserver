package es.david.ptctest.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
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
	private HashMap<String,Usuario> registered;
	
	public Registered(){
		this.registered = new HashMap<String,Usuario>();
	}
	public Registered(String file){
		this.file =file;
		this.registered = new HashMap<String,Usuario>();
		try {
			backupUsers();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void backupUsers() throws IOException{
		File fich = new File(file);
		if(fich.exists()){
			ObjectMapper om = new ObjectMapper();
			TypeReference<HashMap<String,Usuario>> tr = new TypeReference<HashMap<String,Usuario>>() {
			};
			registered = om.readValue(fich, tr);
		}
	}
	public synchronized boolean addRegId(String username, String identificador,String role,String serverKey) throws Exception{
		Usuario user = registered.get(username);
		boolean reg = false;
		if(user!=null){
			if(serverKey.equals(user.getServerKey())){
				if(role.equals(Globals.ACTION_REQUESTER)){
					reg = user.addRequester(identificador);
				}else if(role.equals(Globals.ACTION_CONTAINER)){
					reg = user.addContainer(identificador);
				}
			}
			
		}else{
			if(role.equals(Globals.ACTION_CONTAINER)){
				user = new Usuario();
				user.setServerKey(serverKey);
				user.setUsername(username);
				reg = user.addContainer(identificador);
			}
		}
		if(user!=null)
			registered.put(username, user);
		ObjectMapper om = new ObjectMapper();
		om.writeValue(new File(file), registered);
		System.out.println("Reg es false");
		return reg;
	}
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public HashMap<String,Usuario> getRegistered() {
		HashMap<String,Usuario> hashRet = new HashMap<String,Usuario>();
		Set<String> keys = this.registered.keySet();
		for(String s: keys){
			hashRet.put(s, this.registered.get(s));
		}
		
		return hashRet;
	}
	public void setRegistered(HashMap<String,Usuario> registered) throws Exception{
		this.registered = registered;
		ObjectMapper om = new ObjectMapper();
		om.writeValue(new File(file), registered);
	}
	public synchronized int getNUsers(){
		return registered.size();
	}
	
	public synchronized String[] containers(String mail){
		ArrayList<String> containers = this.registered.get(mail).containersIds();
		String[] ret = new String[containers.size()];
		containers.toArray(ret);
		return ret;
	}
	public synchronized String[] getClearNotifIds(String mail,String regId){
		ArrayList<String> containers = this.registered.get(mail).containersIds();
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
	public synchronized boolean hasId(String role, String mail, String regId){
		Usuario user = this.registered.get(mail);
		if(user!=null){
			if(Globals.ACTION_CONTAINER.equals(role)){
				ArrayList<String> containers = user.containersIds();
				return containers.contains(regId);
			}else if(Globals.ACTION_REQUESTER.equals(role)){
				ArrayList<String> requesters = user.requesterIds();
				return requesters.contains(regId);
			}
		}
		return false;
	}
}
