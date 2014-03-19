package org.deepskylog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	        	LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.online").putExtra("org.deepskylog.online", "online"));
		        return readIt(inputStream, 50000);
	        }
	        else {
	        	LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.online").putExtra("org.deepskylog.online", "offline"));
	        	return "<result>"+"Unavailable url: "+theUrl+"</result>";
	        }
	    }
 	    catch(Exception e) { 
 			LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.online").putExtra("org.deepskylog.online", "offline"));
        	return "<result>"+"Unavailable url: "+theUrl+"</result>"; 
        }
 	    finally {
 	    	if(inputStream!=null) {
 	    		try { inputStream.close(); }
 	    		catch(IOException e) { }
	        } 
 	    }
    }    
    
    private static String readIt(InputStream inputStream, int len) throws IOException, UnsupportedEncodingException {
    	char[] buffer=new char[len];
    	(new InputStreamReader(inputStream)).read(buffer);
    	return new String(buffer).trim();
    }	
    
    public static String getTagContent(String result, String tag) throws Exception {
  	   	if(result==null) throw new Exception("Utils.getTagContent: No string to analyse");
  	   	if(tag==null) throw new Exception("Utils.getTagContent: No tag to analyse");
  	   	if(tag.equals("")) throw new Exception("Utils.getTagContent: Empty tag to analyse");	   	
    	Integer datatag1=result.indexOf("<"+tag+">")+(tag.length()+2);
    	if(datatag1==-1) throw new Exception("Utils.getTagContent: No opening tag "+tag+" in "+result);
    	Integer datatag2=result.indexOf("</"+tag+">",datatag1);
		if(datatag2==-1) throw new Exception("Utils.getTagContent: No closing tag "+tag+" in "+result);
    	return result.substring(datatag1,datatag2);
    }
    
    public static void invokeClassMethodWithResult(String result) {
    	try { Class.forName(getTagContent(result,"onResultClass")).getMethod(getTagContent(result,"onResultMethod"), String.class).invoke(null,result); }
    	catch(Exception e) { Toast.makeText(MainActivity.mainActivity,"Utils.invokeClassMethodWithResult: Exception 2, "+result+e.getMessage().toString(), Toast.LENGTH_SHORT).show(); };
    }


}
