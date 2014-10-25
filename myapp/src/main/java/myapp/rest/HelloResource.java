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
	private void sendRequestMessage(String[] usersIds,String mail,String dominio) throws Exception{
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
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		om.writeValue(wr, gcmdata);
		wr.flush();
		wr.close();
		
		int responseCode = con.getResponseCode();
		System.out.println("\nEnviando mensaje de peticion a los containers.");
		System.out.println("Response code: "+responseCode);
	}
	
	private void sendResponseMessage(String userId,String mail,String dominio,String pass) throws Exception{
		URL obj = new URL(Globals.GCM_URL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", "key=AIzaSyBazwPhhD0N6ddh3Ph0IK59kKOrFjBixZY");
		
		ObjectMapper om = new ObjectMapper();
		String[] usersIds = {userId};
		GCMMessage gcmdata = new GCMMessage(usersIds);
		gcmdata.addData(Globals.MSG_ACTION,Globals.ACTION_RESPONSE);
		gcmdata.addData(Globals.MSG_MAIL, mail);
		gcmdata.addData(Globals.MSG_DOMAIN, dominio);
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
    
    @POST("/register")
    @PermitAll
    public Boolean register(Message message) {
    	System.out.println(message);
		registered = new Registered(fichRegistered);
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
    
    @POST("/askforpass")
    @PermitAll
    public Boolean askForPass(Message message){
    	registered = new Registered(fichRegistered);
    	requests = new Requests(fichRequests);
    	try {
			registered.backupUsers();
			requests.backupRequests();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("Mensaje de peticion:");
    	Set<String> keys = message.keySet();
    	for(String s: keys){
    		System.out.println(s+": "+message.value(s));
    	}
    	String mail = message.value(Globals.MSG_MAIL);
    	String regId = message.value(Globals.MSG_REG_ID);
    	String dominio = message.value(Globals.MSG_DOMAIN);
    	if(registered.hasRegId(Globals.ACTION_REQUESTER, mail, regId)){
    		//Añadimos a la lista de peticiones pendientes.
    		try {
				requests.addRequest(mail,dominio,regId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		String[] containers = registered.containers(mail);
    		try {
				sendRequestMessage(containers, mail, dominio);
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return false;
    }
    
    @POST("/response")
    @PermitAll
    public Boolean responsePass(Message message){
    	registered = new Registered(fichRegistered);
    	requests = new Requests(fichRequests);
    	try {
			registered.backupUsers();
			requests.backupRequests();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("Mensaje de respuesta:");
    	Set<String> keys = message.keySet();
    	for(String s: keys){
    		System.out.println(s+": "+message.value(s));
    	}
    	String mail = message.value(Globals.MSG_MAIL);
    	String dominio = message.value(Globals.MSG_DOMAIN);
    	String pass = message.value(Globals.MSG_PASSWD);
    	try {
			String regresponse = requests.removeRequest(mail, dominio);
			if(regresponse!=null){
				System.out.println("Existe la peticion");
				sendResponseMessage(regresponse, mail, dominio, pass);
				System.out.println("Mensaje enviado con exito");
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
    
    @GET("/registered")
    @PermitAll
    public String getRegistered() throws JsonProcessingException{
    	if(registered == null){
    		registered = new Registered(fichRegistered);
    		try {
				registered.backupUsers();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	HashMap<String,LinkedHashMap<String,ArrayList<String>>> registrados = registered.getRegistered();

	    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	    return ow.writeValueAsString(registrados);
    }
    
    @GET("/requests")
    @PermitAll
    public String getRequests() throws JsonProcessingException{
    	if(requests == null){
    		requests = new Requests(fichRequests);
    		try {
				requests.backupRequests();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	HashMap<String,HashMap<String,String>> peticiones = requests.getPendingRequests();

	    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	    return ow.writeValueAsString(peticiones);
    }
}