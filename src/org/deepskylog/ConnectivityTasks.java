package org.deepskylog;

import org.deepskylog.GetDslCommand.GetDslCommandOnResult;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
    	new checkLoginTask().execute("http://"+serverUrl+"appgetcommand.php?command=checkuser&username="+loginId+"&password="+loginPassword);
	}
	
	public static void ConnectivityTasksDslDialogListener1(String theKey) {
		if(theKey.equals("positive")) MainActivity.goToFragment("settingsFragment", MainActivity.ADD_TO_BACKSTACK);
	}
	private static void longRunningChekLogin(String theTask) {
		if(theTask.equals("setLoginStatus")) {
			if((loginStatus.equals("invalid credentials"))||(loginStatus.equals("user invalid"))) {
				MainActivity.actionBar.setSubtitle(MainActivity.resources.getString(R.string.actionbar_connectivity_V));				
			    DslDialog.newInstance("ConnectivityTasks","ConnectivityTasksDslDialogListener1", 
			    		MainActivity.resources.getString(R.string.observers_invalidcredentials),
			    		MainActivity.resources.getString(R.string.general_Ok),
			    		"",
			    		MainActivity.resources.getString(R.string.general_Cancel),
			    		true)
			    		.show(MainActivity.fragmentManager, "dslDialog");				
			}
			else if(loginStatus.equals("")) {
				MainActivity.actionBar.setSubtitle(MainActivity.resources.getString(R.string.actionbar_connectivity_X));				
			}
			else {
				MainActivity.mainFragment.setText(loginStatus);
				MainActivity.actionBar.setSubtitle(MainActivity.resources.getString(R.string.actionbar_connectivity_L)+MainActivity.loggedPerson);								
			}
			if(autoLogin) {
				GetDslCommand.getCommand("newobservationcount", 
										 "&since=20140101",
										 new GetDslCommandOnResult() {
											@Override public void onResultAvailable(String result) {
												MainActivity.mainFragment.setText("New deepsky observations since 20140101: "+result);
											}
										 });
			}
		}	

	}
	
	private static void longRunningChekConnectivityStatus(String theTask) {
		if(theTask.equals("setNetworkAvailabilityStatus")) {
			if((networkStatus.equals("mobile"))||(networkStatus.equals("WIFI"))) {
				if(autoLogin) new checkServerTask().execute("http://"+serverUrl+"appgetcommand.php?command=alive?");
			}
			else
				MainActivity.actionBar.setSubtitle(MainActivity.resources.getString(R.string.actionbar_connectivity_X));
		}
		if(theTask.equals("setServerAvailabilityStatus")) {
			if(serverStatus.equals("alive")) {
				MainActivity.actionBar.setSubtitle(MainActivity.resources.getString(R.string.actionbar_connectivity_S));
				if(autoLogin) new checkLoginTask().execute("http://"+serverUrl+"appgetcommand.php?command=checkuser&username="+loginId+"&password="+loginPassword);
			}
			else
				MainActivity.actionBar.setSubtitle(MainActivity.resources.getString(R.string.actionbar_connectivity_X));				
		}
		if(theTask.equals("setLoginStatus")) {
			if(loginStatus.equals("invalid credentials")) {
				MainActivity.actionBar.setSubtitle(MainActivity.resources.getString(R.string.actionbar_connectivity_V));				
			}
			else if(loginStatus.equals("")) {
				MainActivity.actionBar.setSubtitle(MainActivity.resources.getString(R.string.actionbar_connectivity_X));				
			}
			else {
				MainActivity.actionBar.setSubtitle(MainActivity.resources.getString(R.string.actionbar_connectivity_L)+MainActivity.loggedPerson);								
			}
			if(autoLogin) {
				GetDslCommand.getCommand("newobservationcount", 
										 "&since=20140101",
										 new GetDslCommandOnResult() {
											@Override public void onResultAvailable(String result) {
												MainActivity.mainFragment.setText("New deepsky observations since 20140101: "+result);
											}
										 });
			}
		}	
	}
		
	
	//Network connection
	private static void postCheckNetworkAvailability(String resultNetworkAvailability) {
		networkStatus=resultNetworkAvailability;
		onTaskFinished("setNetworkAvailabilityStatus");
	}
	//Server connection
    private static void postCheckServerAvailability(String resultServerAvailability) {
    	serverStatus=resultServerAvailability;
		onTaskFinished("setServerAvailabilityStatus");
   }
  //Login
    private static void postCheckLogin(String resultLogin) {
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
      	MainActivity.preferenceEditor.putString("loggedPerson", MainActivity.loggedPerson);
		onTaskFinished("setLoginStatus");
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
