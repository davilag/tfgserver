package es.david.ptctest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import es.david.ptctest.util.Globals;
import es.david.ptctest.util.Message;
import es.david.ptctest.util.Registered;
import es.david.ptctest.util.UtilMessage;

@Path("/register")
public class RegisterResources {
	private Registered registered;
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello() throws JsonProcessingException {
		registered = Registered.singleton(Globals.fichRegistered);
    	HashMap<String,LinkedHashMap<String,ArrayList<String>>> registrados = registered.getRegistered();

	    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	    return ow.writeValueAsString(registrados);
	}
	
	/*
     * Método que se llama cuando el servicio REST recibe una peticion POST a la url /PTC/register.
     * Esta llamada al servicio tiene que tener en el body un objeto aplanado en JSON con los 
     * siguientes parámetros:
     * Objeto raíz data que contendrá los siguientes valores:
     * 		->mail: mail que utilizará el usuario para registrarse en el servicio.
     * 		->regId: id generada por GCM para poder poner localizar la extensión o el movil en un futuro.
     * 		->role: role que empeñará el usuario que hace la peticion en el servicio:
     * 			*REQUESTER: este role estara desempeñado por la extensión de Google Chrome y pedirá contraseñas.
     * 			*CONTAINER: este role estará desempeñado por la aplicación en Android que contendrá las contraseñas
     * 			del usuario.
     */
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Boolean registrar(String body){
		System.out.println(body);
		registered = Registered.singleton(Globals.fichRegistered);
		Message message = UtilMessage.stringToMessage(body);
		if(message!=null){
			Set<String> keys = message.keySet();
			for(String s: keys){
				System.out.println(s+": "+message.value(s));
			}
			String mail = message.value(Globals.MSG_MAIL);
			String regId = message.value(Globals.MSG_REG_ID);
			String role = message.value(Globals.MSG_ROLE);
			try {
				registered.backupUsers();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(Globals.ACTION_REQUESTER.equals(role) || Globals.ACTION_CONTAINER.equals(role)){
				System.out.println("Es un role valido.");
				try {
					return registered.addRegId(mail, regId, role);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
	    	System.out.println(registered.getNUsers());
	    	return false;
		}
		System.out.println("Es igual a null");
		return false;
		
	}
}