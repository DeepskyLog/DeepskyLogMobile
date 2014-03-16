package org.deepskylog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class Utils {
	
	public static boolean isNumeric(String str) {
	    for (char c:str.toCharArray()) {
	        if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}

	public static String downloadUrl(String theUrl) {
    	InputStream inputStream=null;
    	try {
 	    	URL url=new URL(theUrl);
	        HttpURLConnection conn=((HttpURLConnection) url.openConnection());
	        conn.setReadTimeout(10000);
	        conn.setConnectTimeout(15000);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        conn.connect();
	        if(conn.getResponseCode()==200) {
	        	inputStream=conn.getInputStream();
	        	Intent intent=new Intent("org.deepskylog.online");
	        	if(intent.getStringExtra("org.deepskylog.online")!=null) intent.removeExtra("org.deepskylog.online");
	 			LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(intent.putExtra("org.deepskylog.online", "online"));
		        return readIt(inputStream, 50000);
	        }
	        else {
	        	Intent intent=new Intent("org.deepskylog.online");
	        	if(intent.getStringExtra("org.deepskylog.online")!=null) intent.removeExtra("org.deepskylog.online");
	 			LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(intent.putExtra("org.deepskylog.online", "offline"));
	        	return "<result>"+"Unavailable url: "+theUrl+"</result>";
	        }
	    }
 	    catch (Exception e) { 
 			Intent intent=new Intent("org.deepskylog.online");
 			if(intent.getStringExtra("org.deepskylog.online")!=null) intent.removeExtra("org.deepskylog.online");
 			LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(intent.putExtra("org.deepskylog.online", "offline"));
        	return "<result>"+"Unavailable url: "+theUrl+"</result>"; 
        }
 	    finally {
 	    	if (inputStream!=null) {
 	    		try { inputStream.close(); }
 	    		catch(IOException e) { return "<result>"+"Command unavailable."+"</result>"; }
	        } 
 	    }
    }    
    
    private static String readIt(InputStream inputStream, int len) throws IOException, UnsupportedEncodingException {
    	Reader reader=new InputStreamReader(inputStream);        
    	char[] buffer=new char[len];
    	reader.read(buffer);
    	return new String(buffer).trim();
    }	
    
    public static String getTagContent(String result, String tag) throws Exception {
    	Integer datatag1=result.indexOf("<"+tag+">")+(tag.length()+2);
  	   	if(datatag1==-1) {
  	   		//Toast.makeText(MainActivity.mainActivity, "No opening tag "+tag+" in "+result,Toast.LENGTH_LONG).show();
    		throw new Exception( "No opening tag "+tag+" in "+result);
    	}
		Integer datatag2=result.indexOf("</"+tag+">",datatag1);
		if(datatag2==-1) {
    		Toast.makeText(MainActivity.mainActivity, "No closing tag "+tag+" in "+result,Toast.LENGTH_LONG).show();
    		throw new Exception( "No closing tag "+tag+" in "+result);
      	}
    	return result.substring(datatag1,datatag2);
    }
    
    public static void onResultRaw(String result) {
    	try { Class.forName(getTagContent(result,"onResultClass")).getMethod(getTagContent(result,"onResultMethod"), String.class).invoke(null,result); }
    	catch(Exception e) { Toast.makeText(MainActivity.mainActivity,"Exception 2 in Utils "+result+e.getMessage().toString(), Toast.LENGTH_SHORT).show(); };
    }


}
