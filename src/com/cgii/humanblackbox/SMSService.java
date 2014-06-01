package com.cgii.humanblackbox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class SMSService implements Runnable{
	
	public SMSService(){
		
	}
	
	@Override
	public void run() {
		HttpClient httpClient = new DefaultHttpClient();
		String number = "4155952268";
		
		String message = "From:" + Services.name + ". I might be in an accident at " 
				+ Services.address + " " + Services.zipCode;
		String parm1 = null;
		String parm2 = null;
		
		String address = "http://textbelt.com/text/";
		/*
		HttpPost httpPost = new HttpPost(address);
		
		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("number", number));
		params.add(new BasicNameValuePair("message", "\"" + message + "\""));
		try {
		    httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
		    // writing error to Log
		    e.printStackTrace();
		}
		
		try {
		    HttpResponse response = httpClient.execute(httpPost);
		    HttpEntity respEntity = response.getEntity();

		    if (respEntity != null) {
		        // EntityUtils to get the response content
		        String content =  EntityUtils.toString(respEntity);
		        Log.v(Services.TAG, content);
		    }
		} catch (ClientProtocolException e) {
		    // writing exception to log
		    e.printStackTrace();
		} catch (IOException e) {
		    // writing exception to log
		    e.printStackTrace();
		}
		*/
		try{
			URL url = new URL(address);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			
			writer.write("number="+number + "&message=\"" + message + "\"");
			writer.flush();
			String line;
			BufferedReader reader = new BufferedReader (new InputStreamReader(conn.getInputStream()));
			while((line = reader.readLine()) != null){
				System.out.println(line);
			}
			writer.close();
			reader.close();
		}
		catch (Exception e){
			Log.v(Services.TAG, "SMS an exception occurred");
			e.printStackTrace();
		}
		
		
	}
	
}
