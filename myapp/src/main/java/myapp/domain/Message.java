package myapp.domain;

import java.util.HashMap;
import java.util.Set;

/*
 * Clase para facilitar el aplanado y desaplanado de los cuerpos en las 
 * peticiones HTTP del servicio.
 */
public class Message {
	private HashMap<String,String> data;

	public Message(){
		data = new HashMap<String,String>();
	}


	public void setData(HashMap<String, String> data) {
		this.data = data;
	}
	
	public void addData(String key, String value){
		this.data.put(key, value);
	}
	
	public Set<String> keySet(){
		return this.data.keySet();
	}
	
	public String value(String key){
		return this.data.get(key);
	}


	public HashMap<String, String> getData() {
		return data;
	}
}
