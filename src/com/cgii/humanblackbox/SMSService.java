package com.cgii.humanblackbox;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

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
	}
	
}
