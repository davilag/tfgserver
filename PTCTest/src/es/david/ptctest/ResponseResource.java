package es.david.ptctest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
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
	
	private void sendClearNotif(String[] userId,String mail,String dominio,String pass, Integer reqId) throws Exception{
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
		gcmdata.addData(Globals.MSG_REQ_ID,reqId.toString());
		
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
	    	String pass = message.value(Globals.MSG_PASSWD);
	    	String regId = message.value(Globals.MSG_REG_ID);
	    	Integer reqId = Integer.parseInt(message.value(Globals.MSG_REQ_ID));
	    	try {
				String regresponse = requests.removeRequest(mail, dominio,pass,reqId);
				if(regresponse!=null){
					System.out.println("Existe la peticion");
//					sendResponseMessage(regresponse, mail, dominio, pass);
					requests.removeRequest(mail, dominio, pass, reqId);
					System.out.println("Mensaje enviado con exito");
					String[] regIdsClear = registered.getClearNotifIds(mail, regId);
					sendClearNotif(regIdsClear, mail, dominio, pass,reqId);
					return true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return false;
		}
		return false;
	}
}
