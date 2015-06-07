package es.david.ptc.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
	
	public static PayloadRequest getPayloadRequestMessage(String message, String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
			CryptoMessage cm = mapper.readValue(message, CryptoMessage.class);
			System.out.println("El iv es: '"+cm.getIv()+"'");
			System.out.println("El payload es: '"+cm.getPayload()+"'");
			String payload = GaloisCounterMode.GCMDecrypt(key, cm.getIv(), cm.getPayload(), cm.getAad());
			PayloadRequest out = mapper.readValue(payload, PayloadRequest.class);
			return out;
	}
	
	public static CryptoMessage getCryptoMessage(String message){
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(message, CryptoMessage.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String getMessageMail(String message){
		ObjectMapper mapper = new ObjectMapper();
		try {
			CryptoMessage cm = mapper.readValue(message, CryptoMessage.class);
			return cm.getAad();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
