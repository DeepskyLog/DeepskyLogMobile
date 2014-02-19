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
	
	public String serverUrl;
	public String networkStatus;
	public String serverStatus;
	public String loginStatus;
	
	public boolean autoLogin;
	public String loginId;
	public String loginPassword;
	
	private String longRunningTask;

	public void checkConnectivityStatus() {
		longRunningTask="checkConnectivityStatus";
		addTaskCheckTasks("setNetworkAvailabilityStatus");
	}
	
	public void checkAutoConnectivityStatus() {
    	if(autoLogin) checkConnectivityStatus();		
	}

	public ConnectivityTasks(MainActivity theMainActivity) {
		super();
		mainActivity=theMainActivity;
	   	serverUrl="www.deepskylog.be/";
	   	autoLogin=mainActivity.preferences.getBoolean("autoLogin", true);
     	loginId=mainActivity.preferences.getString("loginId", "");
    	loginPassword=mainActivity.preferences.getString("loginPassword", "");
      	taskQueue=new LinkedList<String>();
      	longRunningTask="";
	}

	private void longRunningChekConnectivityStatus(String theTask) {
		if(theTask.equals("setNetworkAvailabilityStatus")) {
			if((networkStatus.equals("mobile"))||(networkStatus.equals("WIFI"))) {
				if(autoLogin) addTaskCheckTasks("setServerAvailabilityStatus");
			}
			else
				mainActivity.actionBar.setSubtitle(mainActivity.getResources().getString(R.string.actionbar_connectivity_X));
		}
		if(theTask.equals("setServerAvailabilityStatus")) {
			if(serverStatus.equals("alive")) {
				mainActivity.actionBar.setSubtitle(mainActivity.getResources().getString(R.string.actionbar_connectivity_S));
				if(autoLogin) addTaskCheckTasks("setLoginStatus");				
			}
			else
				mainActivity.actionBar.setSubtitle(mainActivity.getResources().getString(R.string.actionbar_connectivity_X));				
		}
		if(theTask.equals("setLoginStatus")) {
			if(loginStatus.equals("invalid credentials")) {
				mainActivity.actionBar.setSubtitle(mainActivity.getResources().getString(R.string.actionbar_connectivity_V));				
			}
			else if(loginStatus.equals("")) {
				mainActivity.actionBar.setSubtitle(mainActivity.getResources().getString(R.string.actionbar_connectivity_X));				
			}
			else {
				mainActivity.actionBar.setSubtitle(mainActivity.getResources().getString(R.string.actionbar_connectivity_L)+MainActivity.loggedPerson);								
			}
			if(autoLogin) {
				getCommand("newobservationcount", "&since=20140101");
			}
		}	
	}
	
	private void onTaskFinished(String theTask) {
		if(longRunningTask.equals("checkConnectivityStatus")) longRunningChekConnectivityStatus(theTask);
	}
	
	private void onGetCommandFinished(String theResult) {
		mainActivity.mainFragment.setText("New deepsky observations since 20140101: "+theResult);
	}

	private boolean knownTask(String theTask) {
		return (theTask.equals("resetStatusParameters")||
				theTask.equals("setNetworkAvailabilityStatus")||
				theTask.equals("setServerAvailabilityStatus")||
				theTask.equals("setLoginStatus")||
				theTask.equals("getnewobservationscount"));
	}
	private void addTask(String theTask) {
		if(knownTask(theTask)) taskQueue.add(theTask);
		else Toast.makeText(mainActivity, "DEBUG: unknown task in addTask: "+theTask, Toast.LENGTH_LONG).show();
	}
	private void addTaskCheckTasks(String theTask) {
		if(knownTask(theTask)) taskQueue.add(theTask);
		else Toast.makeText(mainActivity, "DEBUG: unknown task in addTaskCheckTask: "+theTask, Toast.LENGTH_LONG).show();
		checkTasks();
	}
	private void checkTasks() {
		String theTask;
		if(!taskQueue.isEmpty()) {
			theTask=taskQueue.remove();
			if(theTask=="resetRegistrationParameters") resetStatusParameters();
	    	else if(theTask=="setNetworkAvailabilityStatus") setNetworkAvailabilityStatus();
	    	else if(theTask=="setServerAvailabilityStatus") setServerAvailabilityStatus();
	    	else if(theTask=="setLoginStatus") setLoginStatus();		  	
		}
	}
    private void resetStatusParameters() {
    	networkAvailable=false;
    	serverAvailable=false;
    	loginPassed=false;
    }
	//Network connection
	private void setNetworkAvailabilityStatus() {
		new checkNetworkTask().execute();
	}	
	private void postCheckNetworkAvailability(String resultNetworkAvailability) {
		networkStatus=resultNetworkAvailability;
		onTaskFinished("setNetworkAvailabilityStatus");
	}
	//Server connection
	private void setServerAvailabilityStatus() {
		new checkServerTask().execute("http://"+serverUrl+"appgetcommand.php?command=alive?");
	}
    private void postCheckServerAvailability(String resultServerAvailability) {
    	serverStatus=resultServerAvailability;
		onTaskFinished("setServerAvailabilityStatus");
   }
  //Login
  	private void setLoginStatus() {
		new checkLoginTask().execute("http://"+serverUrl+"appgetcommand.php?command=checkuser&username="+loginId+"&password="+loginPassword);
  	};
    private void postCheckLogin(String resultLogin) {
    	if(resultLogin.equals("invalid credentials")) {
    		loginStatus=resultLogin;
    		MainActivity.loggedPerson="";
    	}
    	else if(resultLogin.startsWith("loggedUser:")) {
    		loginStatus="logged user";
        	MainActivity.loggedPerson=resultLogin.substring(11);    		
    	}
    	else {
    		//DEVELOP: to change...
    		loginStatus=resultLogin;
    		MainActivity.loggedPerson="";
    	}
      	mainActivity.preferenceEditor.putString("loggedPerson", MainActivity.loggedPerson);
		onTaskFinished("setLoginStatus");
    }
  	private void getCommand(String command, String params) {
  		new getCommandTask().execute("http://"+serverUrl+"appgetcommand.php?command="+command+params);
  	};
    private void postGetCommand(String result) {
    	onGetCommandFinished(result);
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

    private class getCommandTask extends AsyncTask<String, Void, String> {
    	@Override
        protected String doInBackground(String... urls) {
    	    try {
                return downloadUrl(urls[0]);
            } 
    	    catch (IOException e) {
                return "Command unavailable.";
            }
        }
        @Override
        protected void onPostExecute(String result) {
        	postGetCommand(result.trim());
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
    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
    	Reader reader=null;
    	reader=new InputStreamReader(stream, "UTF-8");        
    	char[] buffer=new char[len];
    	reader.read(buffer);
    	return new String(buffer).trim();
    }	


}
