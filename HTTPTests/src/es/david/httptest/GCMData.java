package es.david.httptest;

import java.util.HashMap;

public class GCMData {
	private String[] registration_ids;
	private HashMap<String,String> data;
	
	public GCMData(String[] registration_ids) {
		super();
		this.registration_ids = registration_ids;
		data = new HashMap<String, String>();
	}
	
	public String[] getRegistration_ids() {
		return registration_ids;
	}
	public void setRegistration_ids(String[] registration_ids) {
		this.registration_ids = registration_ids;
	}
	public HashMap<String, String> getData() {
		return data;
	}
	public void setData(HashMap<String, String> data) {
		this.data = data;
	}
	public void addData(String key, String value){
		this.data.put(key,value);
	}
}
