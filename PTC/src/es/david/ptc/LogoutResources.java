package es.david.ptc;

import java.io.IOException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import es.david.ptc.util.Globals;
import es.david.ptc.util.Message;
import es.david.ptc.util.Registered;
import es.david.ptc.util.Requests;
import es.david.ptc.util.UtilMessage;

@Path("/logout")
public class LogoutResources {
	
	private Registered registered;
	private Requests requests;
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Boolean addPassRes(String body){
		System.out.println("\nHa llegado un mensaje de respuesta para borrar un contenedor.");
		System.out.println(body);
		Message message = UtilMessage.stringToMessage(body);
		if(message!=null){
			registered = Registered.singleton(Globals.fichRegistered);
			requests = Requests.singleton(Globals.fichRequests);
			String mail = message.value(Globals.MSG_MAIL);
			String regId = message.value(Globals.MSG_REG_ID);
			String serverKey = message.value(Globals.MSG_SERVER_KEY);
			
			if(registered.correctServerKey(mail, serverKey)){
				System.out.println("Coincide la el mail con la serverkey");
				try {
					return registered.deleteRegId(mail, regId);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}
