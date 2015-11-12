package es.david.ptc;

import java.io.DataOutputStream;
import java.net.URL;
import java.util.Date;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import es.david.ptc.util.GCMMessage;
import es.david.ptc.util.GaloisCounterMode;
import es.david.ptc.util.Globals;
import es.david.ptc.util.Message;
import es.david.ptc.util.Registered;
import es.david.ptc.util.Requests;
import es.david.ptc.util.TimestampCache;
import es.david.ptc.util.UtilMessage;
@Path("/response")
public class ResponseResource {
	private Registered registered;
	private Requests requests;
	private TimestampCache tsCache;
	
	private void sendClearNotif(String[] userId, String reqId,String serverKey) throws Exception{
		URL obj = new URL(Globals.GCM_URL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", "key=AIzaSyBazwPhhD0N6ddh3Ph0IK59kKOrFjBixZY");
		Message payloadMessage = new Message();
		payloadMessage.addData(Globals.MSG_TS, new Date().getTime());
		payloadMessage.addData(Globals.MSG_REQ_ID, reqId);
		payloadMessage.addData(Globals.MSG_ACTION, Globals.ACTION_CLEARNOTIF);
		ObjectWriter ow = new ObjectMapper().writer();
		String payloadPlain = ow.writeValueAsString(payloadMessage);
		String iv = GaloisCounterMode.getIv();
		String payloadCipher = GaloisCounterMode.GCMEncrypt(serverKey, iv, payloadPlain, reqId);
		ObjectMapper om = new ObjectMapper();
		GCMMessage gcmdata = new GCMMessage(userId);
		gcmdata.addData(Globals.MSG_AAD,reqId);
		gcmdata.addData(Globals.MSG_IV, iv);
		gcmdata.addData(Globals.MSG_PAYLOAD, payloadCipher);
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		om.writeValue(wr, gcmdata);
		wr.flush();
		wr.close();
		
		int responseCode = con.getResponseCode();
		System.out.println("\nEnviando mensaje de borrar peticion a los requesters.");
		System.out.println("Numero de Requesters: "+userId.length);
		System.out.println("Response code: "+responseCode);
	}
	
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Boolean askForPass(String body) throws Exception{
		System.out.println("Me ha llegado un mensaje de respuesta");
		System.out.println(body);
		Message message = UtilMessage.stringToMessage(body);
		if(message!=null){
			registered = Registered.singleton(Globals.fichRegistered);
			requests = Requests.singleton();
			tsCache = TimestampCache.getSingleton();
			System.out.println("Mensaje de respuesta");
			Set<String> keys = message.keySet();
			for(String s: keys){
				System.out.println(s);
			}
			String aad = (String) message.value(Globals.MSG_AAD);
			String iv = (String) message.value(Globals.MSG_IV);
			String payloadCipher = (String) message.value(Globals.MSG_PAYLOAD);
			String mail = requests.getMail(aad);
			String serverKey = registered.getServerKey(mail);
			System.out.println("Ha llegado hasta antes de descifrar");
			String payloadPlain = GaloisCounterMode.GCMDecrypt(serverKey, iv, payloadCipher, aad);
			System.out.println("Ha llegado hasta despues de descifrar");
			System.out.println(payloadPlain);
			Message payloadMsg = UtilMessage.stringToMessage(payloadPlain);
			long timestamp = (long)payloadMsg.value(Globals.MSG_TS);
			if(payloadMsg!=null && !tsCache.hasTimeStamp ((long)payloadMsg.value(Globals.MSG_TS)) && UtilMessage.correctTimestamp(timestamp)){
				tsCache.addTimeStamp((long)payloadMsg.value(Globals.MSG_TS));
				Long nonce = new Long((int) payloadMsg.value(Globals.MSG_NONCE));
				String estado = (String)payloadMsg.value(Globals.MSG_STATE);
				String pass = (String) payloadMsg.value(Globals.MSG_PASSWD);
				String ivPass = (String) payloadMsg.value(Globals.MSG_IV);
				if(requests.validNonce(aad, mail, nonce)){
					System.out.println("El nonce es valido");
					boolean regResponse = requests.removeRequest(mail, estado, pass, aad, nonce, System.currentTimeMillis(), ivPass, registered.getNContainers(mail));
					System.out.println("RegResponse: "+regResponse);
					sendClearNotif(registered.containers(mail), aad, serverKey);
				}else{
					System.out.println("El nonce es invalido");
				}
			}
		}
		return true;
	}
}
