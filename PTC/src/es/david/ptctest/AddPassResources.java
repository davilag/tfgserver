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
import es.david.ptctest.util.Response;
import es.david.ptctest.util.UtilMessage;

@Path("/addpass")
public class AddPassResources {
	private Registered registered;
	private Requests requests;
	
	private void sendAddPass(String[] usersIds,String mail,String dominio,String reqId, 
							String serverKey, String pass, String username ) throws Exception{
		URL obj = new URL(Globals.GCM_URL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", "key=AIzaSyBazwPhhD0N6ddh3Ph0IK59kKOrFjBixZY");
		
		ObjectMapper om = new ObjectMapper();
		GCMMessage gcmdata = new GCMMessage(usersIds);
		gcmdata.addData(Globals.MSG_ACTION,Globals.ACTION_ADD_PASS);
		gcmdata.addData(Globals.MSG_MAIL, mail);
		gcmdata.addData(Globals.MSG_DOMAIN, dominio);
		gcmdata.addData(Globals.MSG_REQ_ID,reqId);
		gcmdata.addData(Globals.MSG_SERVER_KEY,serverKey);
		gcmdata.addData(Globals.MSG_USER, username);
		gcmdata.addData(Globals.MSG_PASSWD,pass);
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		om.writeValue(wr, gcmdata);
		wr.flush();
		wr.close();
		
		int responseCode = con.getResponseCode();
		System.out.println("\nEnviando mensaje de peticion a los containers.");
		System.out.println("Response code: "+responseCode);
	}
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Boolean addPass(String body){
		System.out.println("\nHa llegado un mensaje para guardar la contraseña.");
		System.out.println(body);
		Message message = UtilMessage.stringToMessage(body);
		if(message!=null){
			registered = Registered.singleton(Globals.fichRegistered);
			requests = Requests.singleton(Globals.fichRequests);
			String mail = message.value(Globals.MSG_MAIL);
			String serverKey = message.value(Globals.MSG_SERVER_KEY);
			String usuario = message.value(Globals.MSG_USER);
			String pass = message.value(Globals.MSG_PASSWD);
			String dom = message.value(Globals.MSG_DOMAIN);
			if(registered.correctServerKey(mail, serverKey)){
				Response added = null;
				String reqId = requests.getRequestId();
				try {
					requests.addRequest(mail, reqId, "savePass");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String[] containers = registered.containers(mail);
				try {
					sendAddPass(containers, mail, dom, reqId, serverKey,pass,usuario);
					if(reqId!=null){
						added = requests.getResponse(reqId);
						System.out.println("Ya tengo la respuesta.");
						if(added.getUsername().equals("")){
							return false;
						}else{
							return Boolean.parseBoolean(added.getUsername());
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}