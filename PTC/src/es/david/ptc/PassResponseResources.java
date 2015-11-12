package es.david.ptc;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.david.ptc.util.GaloisCounterMode;
import es.david.ptc.util.Globals;
import es.david.ptc.util.Message;
import es.david.ptc.util.Registered;
import es.david.ptc.util.Requests;
import es.david.ptc.util.TimestampCache;
import es.david.ptc.util.UtilMessage;

@Path("/savedres")
public class PassResponseResources {
	
	private Registered registered;
	private Requests requests;
	private TimestampCache tsCache;
	
    
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public boolean addPassRes(String body) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, JsonGenerationException, JsonMappingException, IOException{

		System.out.println("Me ha llegado un mensaje de respuesta");
		System.out.println(body);
		Message message = UtilMessage.stringToMessage(body);
		if(message!=null){
			registered = Registered.singleton(Globals.fichRegistered);
			requests = Requests.singleton();
			tsCache = TimestampCache.getSingleton();
			System.out.println("Mensaje de respuesta");
			Set<String> keys = message.keySet();
			for(String s: keys){
				System.out.println(s);
			}
			String aad = (String) message.value(Globals.MSG_AAD);
			String iv = (String) message.value(Globals.MSG_IV);
			String payloadCipher = (String) message.value(Globals.MSG_PAYLOAD);
			String mail = requests.getMail(aad);
			String serverKey = registered.getServerKey(mail);
			System.out.println("Ha llegado hasta antes de descifrar");
			String payloadPlain = GaloisCounterMode.GCMDecrypt(serverKey, iv, payloadCipher, aad);
			System.out.println("Ha llegado hasta despues de descifrar");
			System.out.println(payloadPlain);
			Message payloadMsg = UtilMessage.stringToMessage(payloadPlain);
			long timestamp = (long)payloadMsg.value(Globals.MSG_TS);
			if(payloadMsg!=null && !tsCache.hasTimeStamp (timestamp) && UtilMessage.correctTimestamp(timestamp)){
				tsCache.addTimeStamp(timestamp);
				Long nonce = new Long((int) payloadMsg.value(Globals.MSG_NONCE));
				String estado = (String)payloadMsg.value(Globals.MSG_STATE);
				String pass = (String) payloadMsg.value("");
				String ivPass = (String) payloadMsg.value("");
				if(requests.validNonce(aad, mail, nonce)){
					System.out.println("El nonce es valido");
					boolean regResponse = requests.removeRequest(mail, estado, pass, aad, nonce, System.currentTimeMillis(), ivPass, registered.getNContainers(mail));
					System.out.println("RegResponse: "+regResponse);
					return true;
					//TODO: Hacer lo de borrar las notis
				}else{
					System.out.println("El nonce es invalido");
				}
			}
		}
		return false;
		/*
		System.out.println("\nHa llegado un mensaje de respuesta con la contraseña.");
		System.out.println(body);
		Message message = UtilMessage.stringToMessage(body);
		if(message!=null){
			registered = Registered.singleton(Globals.fichRegistered);
	    	requests = Requests.singleton(Globals.fichRequests);
	    	System.out.println("Mensaje de respuesta:");
	    	Set<String> keys = message.keySet();
	    	for(String s: keys){
	    		System.out.println(s+": "+message.value(s));
	    	}
	    	String mail = message.value(Globals.MSG_MAIL);
	    	String dominio = message.value(Globals.MSG_DOMAIN);
	    	String user = message.value(Globals.MSG_USER);
	    	String pass = message.value(Globals.MSG_PASSWD);
	    	String regId = message.value(Globals.MSG_REG_ID);
	    	String reqId = message.value(Globals.MSG_REQ_ID);
	    	String serverKey = message.value(Globals.MSG_SERVER_KEY);
	    	if(registered.correctServerKey(mail, serverKey)){
		    	try {
					boolean regresponse = requests.removeRequest(mail, user,pass,reqId,dominio,registered.getNContainers(mail));
					if(regresponse){
						System.out.println("Existe la peticion");
	//					sendResponseMessage(regresponse, mail, dominio, pass);
						System.out.println("Mensaje enviado con exito");
						String[] regIdsClear = registered.getClearNotifIds(mail, regId);
						if(regIdsClear.length>0){
							sendClearNotif(regIdsClear, mail, dominio, pass,reqId,serverKey);
						}else{
							System.out.println("No hay a quien enviar que borre la notificacion.");
						}
						return true;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	return false;
		}
		return false;
		*/	
		/*
		System.out.println("\nHa llegado un mensaje de respuesta para guardar la contraseña.");
		System.out.println(body);
		Message message = UtilMessage.stringToMessage(body);
		if(message!=null){
			registered = Registered.singleton(Globals.fichRegistered);
			requests = Requests.singleton(Globals.fichRequests);
			String mail = message.value(Globals.MSG_MAIL);
			String reqId = message.value(Globals.MSG_REQ_ID);
			String saved = message.value(Globals.MSG_SAVED_PASS);
			String serverKey = message.value(Globals.MSG_SERVER_KEY);
			
			if(registered.correctServerKey(mail, serverKey)){
				try {
					requests.removeRequest(mail, saved, "", reqId, "savePass",registered.getNContainers(mail));
					return true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		*/
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String prueba(){
		return "hola";
	}
}
