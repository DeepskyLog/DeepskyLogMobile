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
    	catch(Exception e) { Toast.makeText(MainActivity.mainActivity,"Utils.invokeClassMethodWithResult: Exception 2, "+result+" "+e.getMessage().toString(), Toast.LENGTH_LONG).show();Toast.makeText(MainActivity.mainActivity,"Utils.invokeClassMethodWithResult: Exception 2, "+result+" "+e.getMessage().toString(), Toast.LENGTH_LONG).show();Toast.makeText(MainActivity.mainActivity,"Utils.invokeClassMethodWithResult: Exception 2, "+result+" "+e.getMessage().toString(), Toast.LENGTH_LONG).show();Toast.makeText(MainActivity.mainActivity,"Utils.invokeClassMethodWithResult: Exception 2, "+result+" "+e.getMessage().toString(), Toast.LENGTH_LONG).show();Toast.makeText(MainActivity.mainActivity,"Utils.invokeClassMethodWithResult: Exception 2, "+result+" "+e.getMessage().toString(), Toast.LENGTH_LONG).show(); };
    }

    public static String followingDate(String theDate) {
    	Integer day=Integer.valueOf(theDate.substring(6,8));
    	if(day<28) return theDate.substring(0,6)+(((++day)<10?"0"+(day.toString()):(day.toString())));
    	Integer month=Integer.valueOf(theDate.substring(4,6));
    	if(((month==1)||(month==3)||(month==5)||(month==7)||(month==8)||(month==10)||(month==12))&&(day<31)) return theDate.substring(0,6)+(++day).toString();
    	Integer year=Integer.valueOf(theDate.substring(0,4));
    	if(((month==1)||(month==3)||(month==5)||(month==7)||(month==8)||(month==10))&&(day==31)) return year.toString()+((++month)<10?"0"+month.toString():month.toString())+"01";
    	if((month==12)&&(day==31)) return ((Integer)(year+1)).toString()+"0101";
    	if(((month==4)||(month==6)||(month==9)||(month==11))&&(day<30)) return theDate.substring(0,6)+(++day).toString();
    	if(((month==4)||(month==6)||(month==9)||(month==11))&&(day==30)) return theDate.substring(0,4)+((++month)<10?"0"+month.toString():month.toString())+"01";
    	if((month==2)&&(day==28)&&(((year%4)!=0)||(((year%100)==0)&&((year%400)!=0)))) return year.toString()+"0301";
    	if((month==2)&&(day==29)) return year.toString()+"0301";
    	return year.toString()+"0229";
    }

    public static String precedingDate(String theDate) {
    	Integer day=Integer.valueOf(theDate.substring(6,8));
    	if(day>1) return theDate.substring(0,6)+(((--day)<10?"0"+(day.toString()):(day.toString())));
    	Integer month=Integer.valueOf(theDate.substring(4,6));
    	if((month==2)||(month==4)||(month==6)||(month==8)||(month==9)||(month==11)) return theDate.substring(0,4)+((--month)<10?"0"+month.toString():month.toString())+"31";
    	if((month==5)||(month==7)||(month==10)||(month==12)) return theDate.substring(0,4)+((--month).toString())+"30";
    	Integer year=Integer.valueOf(theDate.substring(0,4));
    	if(month==1) return ((Integer)(--year)).toString()+"1231";
    	if(((year%4)!=0)||(((year%100)==0)&&((year%400)!=0))) return year.toString()+"0228";
    	return year.toString()+"0229";
    }

}
