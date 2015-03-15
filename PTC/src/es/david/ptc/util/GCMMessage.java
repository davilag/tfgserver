package es.david.ptc.util;

import java.util.HashMap;

/*
 * Clase para enviar mensajes por GCM hacia los clientes.
 * Esta parte ir�a en el cuerpo de la peticion HTTP hacia
 * el servidor de GCM.
 */
public class GCMMessage {
	private String[] registration_ids;
	private HashMap<String,String> data;
	
	public GCMMessage(){
		data = new HashMap<String, String>();
	}
	public GCMMessage(String[] registration_ids) {
		super();
		this.registration_ids = registration_ids;
		data = new HashMap<String, String>();
	}
	
	public String[] getRegistration_ids() {
		return registration_ids;
	}
	public HashMap<String, String> getData() {
		return data;
	}

	/*
	 * Funcion para a�adir datos que enviar en los mensajes
	 * hacia GCM.
	 */
	public void addData(String key, String value){
		this.data.put(key,value);
	}
}
