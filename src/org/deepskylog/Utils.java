package org.deepskylog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {
	
	public static boolean isNumeric(String str) {
	    for (char c : str.toCharArray()) {
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
		        return readIt(inputStream, 50000);
	        }
	        else {
	        	return "Unavailable url: "+theUrl;
	        }
	    }
 	    catch (Exception e) { return "Unavailable url: "+theUrl; }
 	    finally {
 	    	if (inputStream!=null) {
 	    		try { inputStream.close(); }
 	    		catch(IOException e) { return "Command unavailable."; }
	        } 
 	    }
    }    
    
    private static String readIt(InputStream inputStream, int len) throws IOException, UnsupportedEncodingException {
    	Reader reader=new InputStreamReader(inputStream, "UTF-8");        
    	char[] buffer=new char[len];
    	reader.read(buffer);
    	return new String(buffer).trim();
    }	

}
