package es.david.ptctest.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UtilMessage {
	public static Message stringToMessage(String cadena){
		ObjectMapper mapper = new ObjectMapper();
		try {
			Message m = mapper.readValue(cadena, Message.class);
			return m;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
