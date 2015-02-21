package es.david.ptctest;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import es.david.ptctest.util.Globals;
import es.david.ptctest.util.Message;
import es.david.ptctest.util.Registered;
import es.david.ptctest.util.Requests;
import es.david.ptctest.util.UtilMessage;

@Path("/savedres")
public class PassResponseResources {
	
	private Registered registered;
	private Requests requests;
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public boolean addPassRes(String body){
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
		return false;
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String prueba(){
		return "hola";
	}
}
