package org.deepskylog;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener {

	private static final String ACTUAL_FRAGMENT = "ACTUAL_FRAGMENT";
	public static final boolean ADD_TO_BACKSTACK = true;
	public static final boolean DONT_ADD_TO_BACKSTACK = false;
	
	public Fragment actualFragment;
	public String actualFragmentName;
	
	public MainFragment mainFragment;
	public SettingsFragment settingsFragment;
	public LoginDialog loginDialog;

	public ConnectivityTasks connectivityTasks;
	
	public  SharedPreferences preferences;
	public SharedPreferences.Editor preferenceEditor;
	
	public static String loggedPerson = "";

	public static String serverUrl;
	public static String networkStatus;
	public static String serverStatus;
	public static String loginStatus;
	
	public static boolean autoLogin;
	public static String loginName;
	public static String loginPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainactivity);
		
		mainFragment = new MainFragment();
		settingsFragment = new SettingsFragment();
		loginDialog = new LoginDialog();
        connectivityTasks = new ConnectivityTasks(this);
        
		PreferenceManager.setDefaultValues(this, R.xml.preferences, true);		 
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	preferenceEditor = preferences.edit();
		
    	serverUrl="www.deepskylog.be/";
    	
     	autoLogin=preferences.getBoolean("autoLogin", true);
     	loginName=preferences.getString("loginName", "");
    	loginPassword=preferences.getString("loginPassword", "");
    	
    	if(savedInstanceState==null) {
    		goToFragment("mainFragment",DONT_ADD_TO_BACKSTACK);			
		}
		else {
			setFragment(savedInstanceState.getString(ACTUAL_FRAGMENT));
		}
    	
    	if(autoLogin) {
    		connectivityTasks.addTaskCheckTasks("setNetworkAvailabilityStatus");
    	}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mainmenu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
    	case R.id.mainmenu_settings_id:
	    	goToFragment("settingsFragment",ADD_TO_BACKSTACK);	
			break;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	    
	    return true;
	}

	@Override
	protected void onResume() {
	    super.onResume();
	    preferences.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {    	
    	//DEVELOP: code for changes in login name, password or auto login
    	
    	/*
    	if(key.equals("usertypeTSCHNA")) {
		    if(preferences.getBoolean("usertypeTSCHNA", true))
		    	userType="TSCHNA";
		    else
		    	userType="cc";
		    if(actualFragment==mainFragment)
		    	mainFragment.checkTargetNr();
        }
    	 */
    }
	
	@Override
	protected void onPause() {
	    preferences.unregisterOnSharedPreferenceChangeListener(this);
	    super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putString(ACTUAL_FRAGMENT, actualFragmentName);
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	public void onTaskFinished(String theTask) {
		if(theTask.equals("setNetworkAvailabilityStatus")) {
			Toast.makeText(this, networkStatus, Toast.LENGTH_LONG).show();
			if(autoLogin) connectivityTasks.addTaskCheckTasks("setServerAvailabilityStatus");
		}
		if(theTask.equals("setServerAvailabilityStatus")) {
			Toast.makeText(this, serverStatus, Toast.LENGTH_LONG).show();
			if(autoLogin) connectivityTasks.addTaskCheckTasks("setLoginStatus");
		}
		if(theTask.equals("setLoginStatus")) {
			Toast.makeText(this, loginStatus, Toast.LENGTH_LONG).show();
			connectivityTasks.checkTasks();
		}
	}
	private static boolean isNumeric(String str) {
	    for (char c : str.toCharArray()) {
	        if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}
	private boolean setFragment(String newFragmentName) {
		if(newFragmentName.equals("mainFragment")) actualFragment=mainFragment;
		else if(newFragmentName.equals("settingsFragment")) actualFragment=settingsFragment;
		else if(newFragmentName.equals("loginDialog")) actualFragment=loginDialog;
		else {
			Toast.makeText(this, "Debug: Unknown fragment", Toast.LENGTH_LONG).show();
			return false;
		}
		actualFragmentName = newFragmentName;
		return true;
	}
	public void goToFragment(String newFragmentName, boolean doAddToBackstack) {
		if(setFragment(newFragmentName)) {
			FragmentTransaction fragmentManagerTransaction;
			fragmentManagerTransaction = getFragmentManager().beginTransaction();
			fragmentManagerTransaction.replace(R.id.mainfragment_container, actualFragment);
			if(doAddToBackstack)
				fragmentManagerTransaction.addToBackStack(null);
			fragmentManagerTransaction.commit();
		}
	}

}
