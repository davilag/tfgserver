package es.david.ptctest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import es.david.ptctest.util.GCMMessage;
import es.david.ptctest.util.Globals;
import es.david.ptctest.util.Message;
import es.david.ptctest.util.Registered;
import es.david.ptctest.util.Requests;
import es.david.ptctest.util.Response;
import es.david.ptctest.util.UtilMessage;

@Path("/askforpass")
public class AskForPassResources {
	private Registered registered;
	private Requests requests;
	/*
	 * Método que envia un mensaje de petición a los containers que tiene cada usuario registrado en 
	 * la plataforma.
	 */
	private void sendRequestMessage(String[] usersIds,String mail,String dominio,String reqId, String serverKey ) throws Exception{
		URL obj = new URL(Globals.GCM_URL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", "key=AIzaSyBazwPhhD0N6ddh3Ph0IK59kKOrFjBixZY");
		
		ObjectMapper om = new ObjectMapper();
		GCMMessage gcmdata = new GCMMessage(usersIds);
		gcmdata.addData(Globals.MSG_ACTION,Globals.ACTION_REQUEST);
		gcmdata.addData(Globals.MSG_MAIL, mail);
		gcmdata.addData(Globals.MSG_DOMAIN, dominio);
		gcmdata.addData(Globals.MSG_REQ_ID,reqId);
		gcmdata.addData(Globals.MSG_SERVER_KEY,serverKey);
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		om.writeValue(wr, gcmdata);
		wr.flush();
		wr.close();
		
		int responseCode = con.getResponseCode();
		System.out.println("\nEnviando mensaje de peticion a los containers.");
		System.out.println("Response code: "+responseCode);
	}
	
	private void sendClearNotif(String[] userId,String mail,String dominio,String pass, String reqId,String serverKey) throws Exception{
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
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		om.writeValue(wr, gcmdata);
		wr.flush();
		wr.close();
		
		int responseCode = con.getResponseCode();
		System.out.println("\nEnviando mensaje a los requesters.");
		System.out.println("Response code: "+responseCode);
	}
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String ping() throws JsonProcessingException{
		requests = Requests.singleton(Globals.fichRequests);
    	HashMap<String,HashMap<String,String>> peticiones = requests.getPendingRequests();

	    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	    return ow.writeValueAsString(peticiones);
	}
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String askForPass(String body){
		System.out.println("\nHa llegado un mensaje para pedir una contraseña.");
		System.out.println(body);
		Message message = UtilMessage.stringToMessage(body);
		if(message!=null){
			registered = Registered.singleton(Globals.fichRegistered);
	    	requests = Requests.singleton(Globals.fichRequests);
	    	System.out.println("Mensaje de peticion:");
	    	Set<String> keys = message.keySet();
	    	for(String s: keys){
	    		System.out.println(s+": "+message.value(s));
	    	}
	    	String mail = message.value(Globals.MSG_MAIL);
	    	//String regId = message.value(Globals.MSG_REG_ID); para cuando envie al movil el ordenador que ha enviado la peticion.
	    	String dominio = message.value(Globals.MSG_DOMAIN);
	    	String serverKey = message.value(Globals.MSG_SERVER_KEY);
	    	if(registered.correctServerKey(mail, serverKey)){
	    		Response pass = null;
	    		String reqId = null;
	    		//Añadimos a la lista de peticiones pendientes.
	    		try {
	    			reqId = requests.getRequestId();
					requests.addRequest(mail,reqId,dominio);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	    		String[] containers = registered.containers(mail);
	    		try {
					sendRequestMessage(containers, mail, dominio,reqId,serverKey);
					if(reqId!=null)
						pass = requests.getResponse(reqId);
					if(pass.getUsername().equals("")){
						//No existe el regId vacio por lo que se lo mando a todos ya que ninguno ha contestado.
						System.out.println("No me han contestado. Envio a todos los containers.");
						sendClearNotif(containers, mail, dominio, pass.getPasswd(), reqId,serverKey);
					}
					ObjectWriter ow = new ObjectMapper().writer();
					return ow.writeValueAsString(pass);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    	}
	    	return null;
		}
		return null;
	}
}