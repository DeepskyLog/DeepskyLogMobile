package org.deepskylog;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class ConnectivityTasks {
		
	public static boolean networkAvailable;
	public static boolean serverAvailable;
	public static boolean loginPassed;
	
	public static String serverUrl;
	public static String networkStatus;
	public static String serverStatus;
	public static String loginStatus;
	
	public static boolean autoLogin;
	public static String loginId;
	public static String loginPassword;
	
	private static String longRunningTask;

	public static void initConnectivityTasks() {
	   	serverUrl="www.deepskylog.be/";
	   	autoLogin=MainActivity.preferences.getBoolean("autoLogin", true);
     	loginId=MainActivity.preferences.getString("loginId", "");
    	loginPassword=MainActivity.preferences.getString("loginPassword", "");
      	longRunningTask="";		
	}
	
	private static void onTaskFinished(String theTask) {
		if(longRunningTask.equals("checkConnectivityStatus")) longRunningChekConnectivityStatus(theTask);
		if(longRunningTask.equals("checkLogin")) longRunningChekLogin(theTask);
	}

	public static void checkAutoConnectivityStatus() {
    	if(autoLogin) checkConnectivityStatus();		
	}
	
	public static void checkConnectivityStatus() {
		longRunningTask="checkConnectivityStatus";
		new checkNetworkTask().execute();
	}
	
	public static void checkLogin() {
		longRunningTask="checkLogin";
     	loginId=MainActivity.preferences.getString("loginId", "");
    	loginPassword=MainActivity.preferences.getString("loginPassword", "");
    	new checkLoginTask().execute("http://"+serverUrl+"appgetcommand.php?command=checkUser&username="+loginId+"&password="+loginPassword);
	}
	
	public static void ConnectivityTasksDslDialogListener1(String theKey) {
		if(theKey.equals("positive")) MainActivity.goToFragment("settingsFragment", MainActivity.ADD_TO_BACKSTACK);
	}
	
	public static void ConnectivityTasksGetDslCommandListener1(String result) {
		MainActivity.mainFragment.setText("New deepsky observations since 20140101: "+result);
	}
	
	private static void longRunningChekLogin(String theTask) {
		MainActivity.mainFragment.setText("The task: "+theTask);
		if(theTask.equals("setLoginStatus")) {
			MainActivity.mainFragment.setText("Login status: "+loginStatus);
			if((loginStatus.equals("invalid credentials"))||(loginStatus.equals("user invalid"))) {
		      	LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.loggedperson").putExtra("org.deepskylog.loggedperson", ""));
	        	DslDialog.newInstance("org.deepskylog.ConnectivityTasks","ConnectivityTasksDslDialogListener1", 
			    		MainActivity.resources.getString(R.string.observers_invalidcredentials),
			    		MainActivity.resources.getString(R.string.general_Ok),
			    		"",
			    		MainActivity.resources.getString(R.string.general_Cancel),
			    		true)
			    		.show(MainActivity.fragmentManager, "dslDialog");				
			}
			else if(loginStatus.equals("")) {
		      	LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.loggedperson").putExtra("org.deepskylog.loggedperson", ""));
			}
			else {
		      	LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.loggedperson").putExtra("org.deepskylog.loggedperson", ""));
			}
			if(autoLogin) {
				GetDslCommand.getCommandUpacked("newDeepskyObservationCount", "&since=20140101","org.deepskylog.ConnectivityTasks","ConnectivityTasksGetDslCommandListener1");
			}
		}	

	}
	
	private static void longRunningChekConnectivityStatus(String theTask) {
		if(theTask.equals("setNetworkAvailabilityStatus")) {
			if((networkStatus.equals("mobile"))||(networkStatus.equals("WIFI"))) {
				if(autoLogin) new checkServerTask().execute("http://"+serverUrl+"appgetcommand.php?command=alive");
			}
		}
		if(theTask.equals("setServerAvailabilityStatus")) {
			if(serverStatus.equals("alive")) {
				if(autoLogin) new checkLoginTask().execute("http://"+serverUrl+"appgetcommand.php?command=checkUser&username="+loginId+"&password="+loginPassword);
			}
		}
		if(theTask.equals("setLoginStatus")) {
			if(autoLogin) {
				MainActivity.mainFragment.setText("Getting observation data");
				GetDslCommand.getCommandUpacked("newDeepskyObservationCount", "&since=20140101","org.deepskylog.ConnectivityTasks","ConnectivityTasksGetDslCommandListener1");
			}
		}	
	}
		
	//Network connection
	private static void postCheckNetworkAvailability(String resultNetworkAvailability) {
		MainActivity.mainFragment.setText("Network: "+resultNetworkAvailability);
		networkStatus=resultNetworkAvailability;
		ConnectivityTasks.onTaskFinished("setNetworkAvailabilityStatus");
	}
	//Server connection
    private static void postCheckServerAvailability(String resultServerAvailability) {
    	try { 
    		serverStatus=Utils.getTagContent(resultServerAvailability,"result");
    		MainActivity.mainFragment.setText("Server: "+serverStatus);
    		ConnectivityTasks.onTaskFinished("setServerAvailabilityStatus");
    	}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,e.toString(),Toast.LENGTH_LONG).show(); }
    }
    //Login
    private static void postCheckLogin(String resultLogin) {
    	try {
    		resultLogin=Utils.getTagContent(resultLogin, "result");
	    	resultLogin=(resultLogin.startsWith("loggedUser:")?resultLogin.substring(11):"");
	    	MainActivity.preferenceEditor.putString("loggedPerson", resultLogin);
	      	MainActivity.preferenceEditor.commit();
	      	LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.loggedperson").putExtra("org.deepskylog.loggedperson", resultLogin));
	      	ConnectivityTasks.onTaskFinished("setLoginStatus");
    	}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,e.toString(),Toast.LENGTH_LONG).show(); }
    }
    
    
	private static class checkNetworkTask extends AsyncTask<Void, Void, String> {
    	@Override
        protected String doInBackground(Void... args) {
    	    NetworkInfo networkInfo=((ConnectivityManager) MainActivity.mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    	    return (((networkInfo!= null)&&(networkInfo.isConnected()))?networkInfo.getTypeName():"No network available.");		
        }
        @Override protected void onPostExecute(String result) { postCheckNetworkAvailability(result); }
    }
    
    private static class checkServerTask extends AsyncTask<String, Void, String> {
    	@Override protected String doInBackground(String... urls) { return Utils.downloadUrl(urls[0]); }
        @Override protected void onPostExecute(String result) { postCheckServerAvailability(result); }
    }

    private static class checkLoginTask extends AsyncTask<String, Void, String> {
    	@Override protected String doInBackground(String... urls) { return Utils.downloadUrl(urls[0]); }
        @Override protected void onPostExecute(String result) { postCheckLogin(result.trim()); }
    }   

}
