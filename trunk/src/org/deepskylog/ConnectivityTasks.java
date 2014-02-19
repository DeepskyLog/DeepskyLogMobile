package org.deepskylog;

import org.deepskylog.DslDialog.DslDialogOnClickListener;
import org.deepskylog.GetDslCommand.GetDslCommandOnResult;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

public class ConnectivityTasks {
	
	private MainActivity mainActivity;
	
	public boolean networkAvailable;
	public boolean serverAvailable;
	public boolean loginPassed;
	
	public static String serverUrl;
	public static String networkStatus;
	public static String serverStatus;
	public static String loginStatus;
	
	public boolean autoLogin;
	public static String loginId;
	public static String loginPassword;
	
	private String longRunningTask;

	public ConnectivityTasks(MainActivity theMainActivity) {
		super();
		mainActivity=theMainActivity;
	   	serverUrl="www.deepskylog.be/";
	   	autoLogin=mainActivity.preferences.getBoolean("autoLogin", true);
     	loginId=mainActivity.preferences.getString("loginId", "");
    	loginPassword=mainActivity.preferences.getString("loginPassword", "");
      	longRunningTask="";
	}

	private void onTaskFinished(String theTask) {
		if(longRunningTask.equals("checkConnectivityStatus")) longRunningChekConnectivityStatus(theTask);
		if(longRunningTask.equals("checkLogin")) longRunningChekLogin(theTask);
	}

	public void checkAutoConnectivityStatus() {
    	if(autoLogin) checkConnectivityStatus();		
	}
	public void checkConnectivityStatus() {
		longRunningTask="checkConnectivityStatus";
		new checkNetworkTask().execute();
	}
	
	public void checkLogin() {
		longRunningTask="checkLogin";
     	loginId=mainActivity.preferences.getString("loginId", "");
    	loginPassword=mainActivity.preferences.getString("loginPassword", "");
    	Toast.makeText(mainActivity, "username="+loginId+"&password="+loginPassword, Toast.LENGTH_LONG).show();
		new checkLoginTask().execute("http://"+serverUrl+"appgetcommand.php?command=checkuser&username="+loginId+"&password="+loginPassword);
	}
	

	private void longRunningChekLogin(String theTask) {
		if(theTask.equals("setLoginStatus")) {
			if((loginStatus.equals("invalid credentials"))||(loginStatus.equals("user invalid"))) {
				mainActivity.actionBar.setSubtitle(mainActivity.getResources().getString(R.string.actionbar_connectivity_V));				
			    DslDialog.newInstance(new DslDialogOnClickListener() {
					@Override public void onPositiveButtonClick() { mainActivity.goToFragment("settingsFragment", MainActivity.ADD_TO_BACKSTACK); }
					@Override public void onNeutralButtonClick() { }
					@Override public void onNegativeButtonClick() { }
				  }, 
				  mainActivity.getResources().getString(R.string.observers_invalidcredentials),
				  mainActivity.getResources().getString(R.string.general_Ok),
				  "",
				  mainActivity.getResources().getString(R.string.general_Cancel))
				  .show(mainActivity.getFragmentManager(), "dslDialog");				
			}
			else if(loginStatus.equals("")) {
				mainActivity.actionBar.setSubtitle(mainActivity.getResources().getString(R.string.actionbar_connectivity_X));				
			}
			else {
				mainActivity.mainFragment.setText(loginStatus);
				mainActivity.actionBar.setSubtitle(mainActivity.getResources().getString(R.string.actionbar_connectivity_L)+MainActivity.loggedPerson);								
			}
			if(autoLogin) {
				GetDslCommand.getDslCommandOnResult = new GetDslCommandOnResult() {
					@Override public void onResultAvailable(String result) {
						mainActivity.mainFragment.setText("New deepsky observations since 20140101: "+result);
					}
				};
				GetDslCommand.getCommand("newobservationcount", "&since=20140101");
			}
		}	

	}
	
	private void longRunningChekConnectivityStatus(String theTask) {
		if(theTask.equals("setNetworkAvailabilityStatus")) {
			if((networkStatus.equals("mobile"))||(networkStatus.equals("WIFI"))) {
				if(autoLogin) new checkServerTask().execute("http://"+serverUrl+"appgetcommand.php?command=alive?");
			}
			else
				mainActivity.actionBar.setSubtitle(mainActivity.getResources().getString(R.string.actionbar_connectivity_X));
		}
		if(theTask.equals("setServerAvailabilityStatus")) {
			if(serverStatus.equals("alive")) {
				mainActivity.actionBar.setSubtitle(mainActivity.getResources().getString(R.string.actionbar_connectivity_S));
				if(autoLogin) new checkLoginTask().execute("http://"+serverUrl+"appgetcommand.php?command=checkuser&username="+loginId+"&password="+loginPassword);
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
				GetDslCommand.getDslCommandOnResult = new GetDslCommandOnResult() {
					@Override public void onResultAvailable(String result) {
						mainActivity.mainFragment.setText("New deepsky observations since 20140101: "+result);
					}
				};
				GetDslCommand.getCommand("newobservationcount", "&since=20140101");
			}
		}	
	}
		
	
	//Network connection
	private void postCheckNetworkAvailability(String resultNetworkAvailability) {
		networkStatus=resultNetworkAvailability;
		onTaskFinished("setNetworkAvailabilityStatus");
	}
	//Server connection
    private void postCheckServerAvailability(String resultServerAvailability) {
    	serverStatus=resultServerAvailability;
		onTaskFinished("setServerAvailabilityStatus");
   }
  //Login
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
    
    
	private class checkNetworkTask extends AsyncTask<Void, Void, String> {
    	@Override
        protected String doInBackground(Void... args) {
    	    NetworkInfo networkInfo=((ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    	    return (((networkInfo!= null)&&(networkInfo.isConnected()))?networkInfo.getTypeName():"No network available.");		
        }
        @Override protected void onPostExecute(String result) { postCheckNetworkAvailability(result); }
    }
    
    private class checkServerTask extends AsyncTask<String, Void, String> {
    	@Override protected String doInBackground(String... urls) { return Utils.downloadUrl(urls[0]); }
        @Override protected void onPostExecute(String result) { postCheckServerAvailability(result); }
    }

    private class checkLoginTask extends AsyncTask<String, Void, String> {
    	@Override protected String doInBackground(String... urls) { return Utils.downloadUrl(urls[0]); }
        @Override protected void onPostExecute(String result) { postCheckLogin(result.trim()); }
    }   

}
