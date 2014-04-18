package org.deepskylog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
	        	byte[] buffer=new byte[500000];
	        	byte[] tempbuffer=new byte[500000];
	            int count=0;
	            int len1=0;
	            while((len1=inputStream.read(tempbuffer))>0) {
	            	for(int i=0;i<len1;i++) buffer[count+i]=tempbuffer[i];
	            	count+=len1;
	            }
	        	return new String(buffer);
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
    
	public static String downloadUrlToFile(String filename, String theUrl, String storageFolder) {
    	InputStream is=null;
    	int len=15000000;
    	try {
 	    	URL urlConnection=new URL(theUrl+filename+".jpg");
	        HttpURLConnection conn=(HttpURLConnection) urlConnection.openConnection();
	        conn.setReadTimeout(30000);
	        conn.setConnectTimeout(15000);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        conn.connect();
	        int response=conn.getResponseCode();
	        if(response==200) {
	        	FileOutputStream f=new FileOutputStream(new File(MainActivity.storagePath+storageFolder,filename+".jpg"));
		        is=conn.getInputStream();
		        byte[] buffer=new byte[len];
	            Long count=0L;
	            int len1=0;
	            while((len1=is.read(buffer))>0) {
	            	count+=len1;
	                f.write(buffer, 0, len1);
	            }
	            f.close();
		        return "<result>"+"Download complete"+"</result>";
	        }
	        else { return "<result>"+"Download failure"+"</result>"; }
	    }
 	    catch (Exception e) { return "<result>"+"Download failure "+e.toString()+"</result>"; }
 	    finally { if(is!=null) { try { is.close(); } catch(Exception e) {} } }
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
    	if(((month==1)||(month==3)||(month==5)||(month==7)||(month==8)||(month==10)||(month==12))&&(day<31)) return theDate.substring(0,6)+(((++day)<10?"0"+(day.toString()):(day.toString())));
    	Integer year=Integer.valueOf(theDate.substring(0,4));
    	if(((month==1)||(month==3)||(month==5)||(month==7)||(month==8)||(month==10))&&(day==31)) return year.toString()+((++month)<10?"0"+month.toString():month.toString())+"01";
    	if((month==12)&&(day==31)) return ((Integer)(year+1)).toString()+"0101";
    	if(((month==4)||(month==6)||(month==9)||(month==11))&&(day<30)) return theDate.substring(0,6)+(((++day)<10?"0"+(day.toString()):(day.toString())));
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
    	if((month==5)||(month==7)||(month==10)||(month==12)) return theDate.substring(0,4)+((--month)<10?"0"+month.toString():month.toString())+"30";
    	Integer year=Integer.valueOf(theDate.substring(0,4));
    	if(month==1) return ((Integer)(--year)).toString()+"1231";
    	if(((year%4)!=0)||(((year%100)==0)&&((year%400)!=0))) return year.toString()+"0228";
    	return year.toString()+"0229";
    }

    public static String followingMonth(String theDate) {
    	String dayString=theDate.substring(6,8);
    	Integer month=Integer.valueOf(theDate.substring(4,6));
    	Integer year=Integer.valueOf(theDate.substring(0,4));
    	if(month==12) return (++year).toString()+"01"+dayString;
    	return year.toString()+((++month)<10?"0"+month.toString():month.toString())+dayString;
    }

    public static String precedingMonth(String theDate) {
    	String dayString=theDate.substring(6,8);
    	Integer month=Integer.valueOf(theDate.substring(4,6));
    	Integer year=Integer.valueOf(theDate.substring(0,4));
    	if(month==1) return (--year).toString()+"12"+dayString;
    	return year.toString()+((--month)<10?"0"+month.toString():month.toString())+dayString;
    }

    public static String followingYear(String theDate) {
    	String dayString=theDate.substring(6,8);
    	String monthString=theDate.substring(4,6);
    	Integer year=Integer.valueOf(theDate.substring(0,4));
    	return (++year).toString()+monthString+dayString;
    }

    public static String precedingYear(String theDate) {
    	String dayString=theDate.substring(6,8);
    	String monthString=theDate.substring(4,6);
    	Integer year=Integer.valueOf(theDate.substring(0,4));
    	return (--year).toString()+monthString+dayString;
    }
    
    public static String toUiDate(String theDate) {
    	return theDate.substring(6,8)+"/"+theDate.substring(4,6)+"/"+theDate.substring(0,4);
    }

}
