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
		String[] userIds = {"APA91bG0ybo1zihZ99c6_d2P3p3LVSzFgt7GSJwmBi1g6MpzuKFtbDCm7tVIyQ_tJeGobUkq8xS11PF3vrBcLvZQBK2CAWjDpYi3ULG1INGZixHo_g69xI2UqBfVm5BY0F-zwB9Zon5L48ySfHkzzChQ_iqv-bMs5quya3jVzgX67Iuwk6QzciM","APA91bHAKNAXmGC-OKQuhgVzvj35nSi3j1JjE4C_q7j1C169Lw9XlmlKYEvyHqpFd787MXCn3-IBq-eHyTR9EE_fNzJulTSYR-K-PDhPnJsM2dlVLPJEKHy45S5iMK6RCzv3KLGfSN57ZD8P1ETF7pWCw3nnwPi9lw"};
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