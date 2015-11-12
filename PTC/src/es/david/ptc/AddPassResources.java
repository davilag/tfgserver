package es.david.ptc;

import java.io.DataOutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.david.ptc.util.CryptoMessage;
import es.david.ptc.util.GCMMessage;
import es.david.ptc.util.GaloisCounterMode;
import es.david.ptc.util.Globals;
import es.david.ptc.util.Message;
import es.david.ptc.util.Registered;
import es.david.ptc.util.Requests;
import es.david.ptc.util.Response;
import es.david.ptc.util.TimestampCache;
import es.david.ptc.util.UtilMessage;

@Path("/addpass")
public class AddPassResources {
	private Registered registered;
	private Requests requests;
	private TimestampCache tsCache;
	
	private void sendAddPass(String[] usersIds,Message payload, String reqId, String serverKey) throws Exception{
		URL obj = new URL(Globals.GCM_URL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", "key=AIzaSyBazwPhhD0N6ddh3Ph0IK59kKOrFjBixZY");
		ObjectMapper om = new ObjectMapper();
		Message payloadRequest = new Message();
		for(String key: payload.keySet()){
			System.out.println("Key: "+key);
			payloadRequest.addData(key, payload.value(key));
		}
		payloadRequest.addData(Globals.MSG_ACTION,Globals.ACTION_ADD_PASS);
		String payloadPlain =om.writer().writeValueAsString(payloadRequest);
		
		String iv = GaloisCounterMode.getIv();
		String payloadCipher = GaloisCounterMode.GCMEncrypt(serverKey, iv, payloadPlain, reqId);
		GCMMessage gcmdata = new GCMMessage(usersIds);
		gcmdata.addData(Globals.MSG_IV,iv);
		gcmdata.addData(Globals.MSG_AAD, reqId);
		gcmdata.addData(Globals.MSG_PAYLOAD, payloadCipher);
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		om.writeValue(wr, gcmdata);
		wr.flush();
		wr.close();
		
		int responseCode = con.getResponseCode();
		System.out.println("\nEnviando mensaje de peticion a los containers."+AddPassResources.class.toString());
		System.out.println("Response code: "+responseCode+" "+AddPassResources.class.toString());
	}
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Boolean addPass(String body) throws Exception{
		if(body!=null){
			CryptoMessage cm = UtilMessage.getCryptoMessage(body);
			String mail = cm.getAad();
			if(cm!=null){
				registered = Registered.singleton(Globals.fichRegistered);
				requests = Requests.singleton();
				tsCache = TimestampCache.getSingleton();
				String payloadCipher = cm.getPayload();
				String serverKey = registered.getServerKey(mail);
				if(serverKey!=null){
					String payloadPlain = GaloisCounterMode.GCMDecrypt(serverKey, cm.getIv(), payloadCipher, mail);
					System.out.println("El payload plano es: "+payloadPlain+" "+AddPassResources.class.toString());
					Message payload = UtilMessage.stringToMessage(payloadPlain);
					Long ts = (Long)payload.value(Globals.MSG_TS);
					if(!tsCache.hasTimeStamp(ts) && UtilMessage.correctTimestamp(ts)){
						String reqId = requests.getRequestId();
						String[] containers = registered.containers(mail);
						sendAddPass(containers, payload, reqId,serverKey);
						requests.addRequest(mail, reqId, new Long((int)payload.value(Globals.MSG_NONCE)));
						Response added = null;
						if(reqId!=null){
							added = requests.getResponse(reqId);
							System.out.println("Ya tengo la respuesta de añadir!"+AddPassResources.class.toString());
							if(Globals.MSG_STATE_OK.equals(added.getEstado())){
								return true;
							}
						}
					}
				}
			}
		}
		/*
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
		*/
		return false;
	}
}
