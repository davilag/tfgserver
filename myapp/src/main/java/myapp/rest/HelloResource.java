package myapp.rest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import myapp.Globals;
import myapp.Registered;
import myapp.Requests;
import myapp.domain.GCMMessage;
import myapp.domain.Message;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/*
 * Clase que contiene las llamadas al servicio REST y los diferentes métodos que se
 * ejecutan cuando se les llama.
 */
@Component @RestxResource
public class HelloResource {
	private static final String fichRegistered = "/tmp/users-REST.json";
	private static final String fichRequests = "/tmp/requests-REST.json";
	private Registered registered;
	private Requests requests;
//    /**
//     * Say hello to currently logged in user.
//     *
//     * Authorized only for principals with Roles.HELLO_ROLE role.
//     *
//     * @return a Message to say hello
//     */
//    @GET("/message")
//    @RolesAllowed(Roles.HELLO_ROLE)
//    public Message sayHello() {
//        return new Message().setMessage(String.format(
//                "hello %s, it's %s",
//                RestxSession.current().getPrincipal().get().getName(),
//                DateTime.now().toString("HH:mm:ss")));
//    }
	
	/*
	 * Método que envia un mensaje de petición a los containers que tiene cada usuario registrado en 
	 * la plataforma.
	 */
	private void sendRequestMessage(String[] usersIds,String mail,String dominio,Integer reqId ) throws Exception{
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
		gcmdata.addData(Globals.MSG_REQ_ID,reqId.toString());
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		om.writeValue(wr, gcmdata);
		wr.flush();
		wr.close();
		
		int responseCode = con.getResponseCode();
		System.out.println("\nEnviando mensaje de peticion a los containers.");
		System.out.println("Response code: "+responseCode);
	}
	
	/*
	 * Mensaje que se envia desde el servidor a un requester cuando este ha hecho una peticion anteriormente.
	 */
//	private void sendResponseMessage(String userId,String mail,String dominio,String pass) throws Exception{
//		URL obj = new URL(Globals.GCM_URL);
//		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
//		con.setRequestMethod("POST");
//		con.setRequestProperty("Content-Type", "application/json");
//		con.setRequestProperty("Authorization", "key=AIzaSyBazwPhhD0N6ddh3Ph0IK59kKOrFjBixZY");
//		
//		ObjectMapper om = new ObjectMapper();
//		String[] usersIds = {userId};
//		GCMMessage gcmdata = new GCMMessage(usersIds);
//		gcmdata.addData(Globals.MSG_ACTION,Globals.ACTION_RESPONSE);
//		gcmdata.addData(Globals.MSG_MAIL, mail);
//		gcmdata.addData(Globals.MSG_DOMAIN, dominio);
//		gcmdata.addData(Globals.MSG_PASSWD,pass);
//		
//		con.setDoOutput(true);
//		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//		om.writeValue(wr, gcmdata);
//		wr.flush();
//		wr.close();
//		
//		int responseCode = con.getResponseCode();
//		System.out.println("\nEnviando mensaje a los requesters.");
//		System.out.println("Response code: "+responseCode);
//	}
//	
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
    /**
     * Say hello to anybody.
     *
     * Does not require authentication.
     *
     * @return a Message to say hello
     */
    @GET("/hello")
    @PermitAll
    public String helloPublic(String who) {
        return "Hi!";
    }
    
    /*
     * Método que se llama cuando el servicio REST recibe una peticion POST a la url /register.
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
    @POST("/register")
    @PermitAll
    public Boolean register(Message message) {
    	System.out.println(message);
		registered = Registered.singleton(fichRegistered);
		try {
			registered.backupUsers();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
    
    /*
     * Método al que se llama cuando el servicio REST recibe una petición POST a la URL /askforpass.
     * Este método solo será util cuando sea llamado por un dispositivo registrado anteriormente e 
     * identificador como requester. Cuando se llama satisfactoriamente a este método, envia una 
     * notificación a todos los containers que haya registrado el usuario. Esta llamada consume
     * un json en el body de la petición con la siguiente estructura:
     * Objeto raíz data que contiene los siguientes valores:
     * 		->mail: mail con el que se ha registrado el usuario anteriormente en el servicio.
     * 		->regId: regId generado con GCM que utiliza el dispositivo que ha hecho la petición.
     * 		->dominio: dominio de la página web de la que se ha pedido la contraseña.
     */
    @POST("/askforpass")
    @PermitAll
    public String askForPass(Message message){
    	registered = Registered.singleton(fichRegistered);
    	requests = Requests.singleton(fichRequests);
    	System.out.println("Mensaje de peticion:");
    	Set<String> keys = message.keySet();
    	for(String s: keys){
    		System.out.println(s+": "+message.value(s));
    	}
    	String mail = message.value(Globals.MSG_MAIL);
    	String regId = message.value(Globals.MSG_REG_ID);
    	String dominio = message.value(Globals.MSG_DOMAIN);
    	if(registered.hasRegId(Globals.ACTION_REQUESTER, mail, regId)){
    		String pass = null;
    		Integer reqId = null;
    		//Añadimos a la lista de peticiones pendientes.
    		try {
    			reqId = requests.getRequestId();
				requests.addRequest(mail,dominio,regId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
    		String[] containers = registered.containers(mail);
    		try {
				sendRequestMessage(containers, mail, dominio,reqId);
				if(reqId!=null)
					pass = requests.getPass(reqId);
				return pass;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    	return null;
    }
    
    @POST("/response")
    @PermitAll
    public Boolean responsePass(Message message){
    	registered = Registered.singleton(fichRegistered);
    	requests = Requests.singleton(fichRequests);
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
//				sendResponseMessage(regresponse, mail, dominio, pass);
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
    
    /*
     * 
     * METODOS DE DEBUG PARA VER EL ESTADO DE LOS REGISTRADOS Y LAS PETICIONES.
     * 
     */
    @GET("/registered")
    @PermitAll
    public String getRegistered() throws JsonProcessingException{
    	registered = Registered.singleton(fichRegistered);
    	HashMap<String,LinkedHashMap<String,ArrayList<String>>> registrados = registered.getRegistered();

	    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	    return ow.writeValueAsString(registrados);
    }
    
    @GET("/requests")
    @PermitAll
    public String getRequests() throws JsonProcessingException{
    	requests = Requests.singleton(fichRequests);
    	HashMap<String,HashMap<String,String>> peticiones = requests.getPendingRequests();

	    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	    return ow.writeValueAsString(peticiones);
    }
}
