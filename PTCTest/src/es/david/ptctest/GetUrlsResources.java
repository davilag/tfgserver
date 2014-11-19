package es.david.ptctest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;

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

@Path("/getURLs")
public class GetUrlsResources {
	private Requests requests;
	private Registered registered;
	
	private void sendRequestURLMessage(String[] usersIds,String mail,Integer reqId ) throws Exception{
		URL obj = new URL(Globals.GCM_URL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", "key=AIzaSyBazwPhhD0N6ddh3Ph0IK59kKOrFjBixZY");
		
		ObjectMapper om = new ObjectMapper();
		GCMMessage gcmdata = new GCMMessage(usersIds);
		gcmdata.addData(Globals.MSG_ACTION,Globals.ACTION_GET_URLS);
		gcmdata.addData(Globals.MSG_MAIL, mail);
		gcmdata.addData(Globals.MSG_REQ_ID,reqId.toString());
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		om.writeValue(wr, gcmdata);
		wr.flush();
		wr.close();
		
		int responseCode = con.getResponseCode();
		System.out.println("\nEnviando mensaje de peticion de URLs a los containers.");
		System.out.println("Response code: "+responseCode);
	}
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String getURLs(String body){
		System.out.println(body);
		Message message = UtilMessage.stringToMessage(body);
		if(message != null){
			requests = Requests.singleton(Globals.fichRequests);
			registered = Registered.singleton(Globals.fichRegistered);
			System.out.println("Mensaje de peticion URLs");
			String mail = message.value(Globals.MSG_MAIL);
			String regId = message.value(Globals.MSG_REG_ID);
			if(registered.hasRegId(Globals.ACTION_REQUESTER, mail, regId)){
				String urls = null;
				Integer reqId = null;
				try{
					reqId = requests.getRequestId();
					requests.addRequest(mail, "es.davidavila.urls", regId);
				}catch(IOException excp){
					excp.printStackTrace();
				}
				String [] containers = registered.containers(mail);
				try {
					sendRequestURLMessage(containers, mail, reqId);
					if(reqId!=null)
						urls = requests.getResponse(reqId);
					return urls;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		return "[]";
	}
}