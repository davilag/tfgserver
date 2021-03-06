package es.david.httptest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import myapp.domain.GCMMessage;
import myapp.domain.Message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class HttpRequest {
	 
	private final String USER_AGENT = "Mozilla/5.0";
 
	public static void main(String[] args) throws Exception {
 
		HttpRequest http = new HttpRequest();
 
//		System.out.println("Testing 1 - Send Http GET request");
//		http.sendGet();
// 
//		System.out.println("\nTesting 2 - Send Http POST request");
//		http.sendPost();
		
		http.sendGCM();
 
	}
 
	// HTTP GET request
	private void sendGet() throws Exception {
 
		String url = "http://localhost:8080/registered";
 
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		// optional default is GET
		con.setRequestMethod("GET");
 
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		System.out.println(response.toString());
 
	}
 
	// HTTP POST request
	private void sendPost() throws Exception {
 
//		String url = "https://android.googleapis.com/gcm/send";
		String url = "http://localhost:8080/register/juan";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		//add reuqest header
		con.setRequestMethod("POST");
 
//		String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
		ObjectMapper om = new ObjectMapper();
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Message message = new Message();
		message.addData("Hola", "Adios");
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		om.writeValue(wr,message);
		wr.flush();
		wr.close();
		System.out.println(ow.writeValueAsString(message));
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		System.out.println(response.toString());
 
	}
	
	public void sendGCM() throws Exception{
		String url = "https://android.googleapis.com/gcm/send";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", "key=AIzaSyBazwPhhD0N6ddh3Ph0IK59kKOrFjBixZY");
 
//		String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
		ObjectMapper om = new ObjectMapper();
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String[] userIds = {"APA91bESRG5NZt2WZkRGOUC_sSbBgj4G0BajGayBJTWK9AQxvEwnr02uHzVWfjte9jQPyNfg2QOD_y3OjoNy4HhD-5v0NSWFHt5CVjHK0udKtiIMrR-T1-cnG6iIgaMIlPIwtPJWCgj6S-kSCJzRVABlZdGuOc_GsneRIJemudYuWyvUDo2J-3I","APA91bHwLfxvb9YJGv91cDT4kL2buw-V_PvN99FBDeKHVROO7QosWewG9nR1eGDdcfV9zhyhkTPCdruSSM70N7pf5EYI9lKKQHah5vs2plG0SdHdhpGTnDHZFWZXcdCSOFBEf-SHIsFh2ZKKazgiAB_dFPrgGEDhGUfNKiz3DDrVz3FpokyDRBE"};
		GCMMessage gcmData = new GCMMessage(userIds);
		gcmData.addData("action", "request");
		gcmData.addData("dominio", "facebook.com");
		System.out.println(ow.writeValueAsString(gcmData));
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		om.writeValue(wr, gcmData);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		System.out.println(response.toString());
	}
 
}