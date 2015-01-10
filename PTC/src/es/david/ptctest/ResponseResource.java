package es.david.ptctest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.david.ptctest.util.GCMMessage;
import es.david.ptctest.util.Globals;
import es.david.ptctest.util.Message;
import es.david.ptctest.util.Registered;
import es.david.ptctest.util.Requests;
import es.david.ptctest.util.UtilMessage;
@Path("/response")
public class ResponseResource {
	private Registered registered;
	private Requests requests;
	
	private void sendClearNotif(String[] userId,String mail,String dominio,String pass, String reqId, String serverKey) throws Exception{
		URL obj = new URL(Globals.GCM_URL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", "key=AIzaSyBazwPhhD0N6ddh3Ph0IK59kKOrFjBixZY");
		
		ObjectMapper om = new ObjectMapper();
		GCMMessage gcmdata = new GCMMessage(userId);
		gcmdata.addData(Globals.MSG_ACTION,Globals.ACTION_CLEARNOTIF);
		gcmdata.addData(Globals.MSG_MAIL, mail);
		gcmdata.addData(Globals.MSG_DOMAIN, dominio);
		gcmdata.addData(Globals.MSG_PASSWD,pass);
		gcmdata.addData(Globals.MSG_REQ_ID,reqId);
		gcmdata.addData(Globals.MSG_SERVER_KEY, serverKey);
		
		System.out.println("Voy a enviar:");
		HashMap<String, String> data = gcmdata.getData();
		Set<String> keys = data.keySet();
		for(String s: keys){
			System.out.println("["+s+","+data.get(s)+"]");
		}
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		om.writeValue(wr, gcmdata);
		wr.flush();
		wr.close();
		
		int responseCode = con.getResponseCode();
		System.out.println("\nEnviando mensaje a los requesters.");
		System.out.println("Response code: "+responseCode);
	}
	
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Boolean askForPass(String body){
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
					boolean regresponse = requests.removeRequest(mail, user,pass,reqId,dominio);
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
	}
}
