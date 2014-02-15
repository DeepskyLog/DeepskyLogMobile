package org.deepskylog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

public class ConnectivityTasks {
	
	private Queue<String> taskQueue;
	
	private MainActivity mainActivity;
	
	public boolean networkAvailable;
	public boolean serverAvailable;
	public boolean loginPassed;
	
	public ConnectivityTasks(MainActivity theMainActivity) {
		super();
		mainActivity=theMainActivity;
       	taskQueue =new LinkedList<String>();
	}
	private boolean knownTask(String theTask) {
		return (theTask.equals("resetRegistrationParameters")||
				theTask.equals("setNetworkAvailabilityStatus")||
				theTask.equals("setServerAvailabilityStatus")||
				theTask.equals("setLoginStatus"));
	}
	public void addTask(String theTask) {
		if(knownTask(theTask)) taskQueue.add(theTask);
		else Toast.makeText(mainActivity, "DEBUG: unknown tast in addTask: "+theTask, Toast.LENGTH_LONG).show();
	}
	public void addTaskCheckTasks(String theTask) {
		if(knownTask(theTask)) taskQueue.add(theTask);
		else Toast.makeText(mainActivity, "DEBUG: unknown tast in addTaskCheckTask: "+theTask, Toast.LENGTH_LONG).show();
		checkTasks();
	}
	public void checkTasks() {
		String theTask;
		if(!taskQueue.isEmpty()) {
			theTask=taskQueue.remove();
			if(theTask=="resetRegistrationParameters") resetRegistrationParameters();
	    	else if(theTask=="setNetworkAvailabilityStatus") setNetworkAvailabilityStatus();
	    	else if(theTask=="setServerAvailabilityStatus") setServerAvailabilityStatus();
	    	else if(theTask=="setLoginStatus") setLoginStatus();		  	
		}
	}
    public void resetRegistrationParameters() {
    	MainActivity.networkStatus="";
    	MainActivity.serverStatus="";
    	MainActivity.loginStatus="";
		checkTasks();
    }
	//Network connection
	public void setNetworkAvailabilityStatus() {
		new checkNetworkTask().execute();
	}	
	public void postCheckNetworkAvailability(String resultNetworkAvailability) {
		MainActivity.networkStatus=resultNetworkAvailability;
		mainActivity.onTaskFinished("setNetworkAvailabilityStatus");
	}
	//Server connection
	public void setServerAvailabilityStatus() {
		new checkServerTask().execute("http://"+MainActivity.serverUrl+"appgetcommand.php?command=alive?");
	}
    public void postCheckServerAvailability(String resultServerAvailability) {
    	MainActivity.serverStatus=resultServerAvailability;
		mainActivity.onTaskFinished("setServerAvailabilityStatus");
   }
  //Login
  	public void setLoginStatus() {
  		//if(MainActivity.networkStatus.equals("No network available."))
  		//	MainActivity.loginStatus="Problem for logging in on server, no network";
  		//else if(!MainActivity.serverStatus.equals("Server alive"))
  		//	MainActivity.loginStatus="Problem for logging in on server, server not alive";
  		//else {
  			//Toast.makeText(mainActivity, "http://"+MainActivity.serverUrl+"appgetcommand.php?command=checkuser&username="+MainActivity.loginName+"&password="+MainActivity.loginPassword, Toast.LENGTH_LONG).show();
   			new checkLoginTask().execute("http://"+MainActivity.serverUrl+"appgetcommand.php?command=checkuser&username="+MainActivity.loginName+"&password="+MainActivity.loginPassword);
  		//}
  	};
    public void postCheckLogin(String resultLogin) {
     	MainActivity.loginStatus=resultLogin;
		mainActivity.onTaskFinished("setLoginStatus");
    }
    
	// ASync Tasks 
    private class checkNetworkTask extends AsyncTask<Void, Void, String> {
    	@Override
        protected String doInBackground(Void... args) {
    	    NetworkInfo networkInfo = ((ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    	    if (networkInfo != null && networkInfo.isConnected()) {
    	      	return networkInfo.getTypeName();
    	    } else {
    	      	return "No network available.";
    	    }		
        }
        @Override
        protected void onPostExecute(String result) {
        	postCheckNetworkAvailability(result);
        }
    }
    
    private class checkServerTask extends AsyncTask<String, Void, String> {
    	@Override
        protected String doInBackground(String... urls) {
    		try {
                return downloadUrl(urls[0]);
            } 
    	    catch (IOException e) {
                return "Server unavailable.";
    	    }
        }
        @Override
        protected void onPostExecute(String result) {
        	postCheckServerAvailability(result);
        }
    }

    private class checkLoginTask extends AsyncTask<String, Void, String> {
    	@Override
        protected String doInBackground(String... urls) {
    	    try {
                return downloadUrl(urls[0]);
            } 
    	    catch (IOException e) {
                return "Checkin unavailable.";
            }
        }
        @Override
        protected void onPostExecute(String result) {
        	postCheckLogin(result.trim());
        }
    }   
   	// Helper function  
    private String downloadUrl(String myurl) throws IOException {
    	InputStream is = null;
    	int len = 500;
        
	    try {
 	    	URL url = new URL(myurl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000);
	        conn.setConnectTimeout(15000);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        conn.connect();
	        
	        int response = conn.getResponseCode();
	        if(response==200) {
		        is = conn.getInputStream();
		        String contentAsString = readIt(is, len);
		        return contentAsString;
	        }
	        else {
	        	return "Unavailable url: "+myurl;
	        }
	    }
 	   catch (Exception e) {
 		   return "Unavailable url: "+myurl;
 		}
 	    finally {
 	    	if (is!=null) {
 	    		is.close();
	        } 
 	    }
    }    
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
    	Reader reader=null;
    	reader=new InputStreamReader(stream, "UTF-8");        
    	char[] buffer=new char[len];
    	reader.read(buffer);
    	return new String(buffer).trim();
    }	


}
