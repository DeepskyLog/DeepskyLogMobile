package org.deepskylog;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnSharedPreferenceChangeListener {

	private static final String ACTUAL_FRAGMENT = "ACTUAL_FRAGMENT";
	public static final boolean ADD_TO_BACKSTACK = true;
	public static final boolean DONT_ADD_TO_BACKSTACK = false;
	public static final String LOGGED_PERSON = "LOGGED_PERSON";
	
	private ActionBar actionBar;
	
	public MainFragment mainFragment;
	public SettingsFragment settingsFragment;
	public LoginDialog loginDialog;

	public ConnectivityTasks connectivityTasks;
	
	public SharedPreferences preferences;
	public SharedPreferences.Editor preferenceEditor;

	public Fragment actualFragment;
	public String actualFragmentName;
	
	public static String serverUrl;
	public static String networkStatus;
	public static String serverStatus;
	public static String loginStatus;
	
	public static boolean autoLogin;
	public static String loginId;
	public static String loginPassword;
	
	public static String loggedPerson = "";
	
	public String actualCommand;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainactivity);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
		checkStateObjects();
    	if(savedInstanceState==null) {
    		goToFragment("mainFragment",DONT_ADD_TO_BACKSTACK);			
		}
		else {
			loggedPerson=savedInstanceState.getString(LOGGED_PERSON);
			setFragment(savedInstanceState.getString(ACTUAL_FRAGMENT));
		}
    	setStateParameters();
    	checkFirstRun();
		actionBar.setTitle(getResources().getString(R.string.actionbar_title_text));
		actionBar.setSubtitle(getResources().getString(R.string.actionbar_connectivity_X));
	}
	
	@Override 
	protected void onStart() {
		super.onStart();
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
	    savedInstanceState.putString(LOGGED_PERSON, loggedPerson);
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	private void checkStateObjects() {
    	if(actionBar==null) actionBar=getActionBar();
		if(mainFragment==null) mainFragment=new MainFragment();
		if(settingsFragment==null) settingsFragment=new SettingsFragment();
		if(loginDialog==null) loginDialog=new LoginDialog();
        if(connectivityTasks==null) connectivityTasks=new ConnectivityTasks(this);
        if(preferences==null) preferences=PreferenceManager.getDefaultSharedPreferences(this);
    	if(preferenceEditor==null) preferenceEditor=preferences.edit();		
	}
	
	private void setStateParameters() {
    	serverUrl="www.deepskylog.be/";
    	autoLogin=preferences.getBoolean("autoLogin", true);
     	loginId=preferences.getString("loginId", "");
    	loginPassword=preferences.getString("loginPassword", "");
	}
	
	private void checkFirstRun() {
		if(preferences.getBoolean("firstRun", true)) {
			loginDialog.show(getFragmentManager(), "loginDialog");
		}
		preferenceEditor.putBoolean("firstRun", false);
	}
	
	public void onTaskFinished(String theTask) {
		if(theTask.equals("setNetworkAvailabilityStatus")) {
			if((networkStatus.equals("mobile"))||(networkStatus.equals("WIFI"))) {
				if(autoLogin) connectivityTasks.addTaskCheckTasks("setServerAvailabilityStatus");
			}
			else
				actionBar.setSubtitle(getResources().getString(R.string.actionbar_connectivity_X));
		}
		if(theTask.equals("setServerAvailabilityStatus")) {
			if(serverStatus.equals("alive")) {
				actionBar.setSubtitle(getResources().getString(R.string.actionbar_connectivity_S));
				if(autoLogin) connectivityTasks.addTaskCheckTasks("setLoginStatus");				
			}
			else
				actionBar.setSubtitle(getResources().getString(R.string.actionbar_connectivity_X));				
		}
		if(theTask.equals("setLoginStatus")) {
			if(loginStatus.equals("invalid credentials")) {
				actionBar.setSubtitle(getResources().getString(R.string.actionbar_connectivity_V));				
			}
			else if(loginStatus.equals("")) {
				actionBar.setSubtitle(getResources().getString(R.string.actionbar_connectivity_X));				
			}
			else {
				actionBar.setSubtitle(getResources().getString(R.string.actionbar_connectivity_L)+loggedPerson);								
			}
			if(autoLogin) {
				actualCommand="newobservationcount";
				connectivityTasks.getCommand(actualCommand, "&since=20140101");
			}
		}
	}
	public void onGetCommandFinished(String theResult) {
		if(actualCommand.equals("newobservationcount")) Toast.makeText(this, "New observations since 20140101: "+theResult, Toast.LENGTH_LONG).show();
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
			Toast.makeText(this, "Debug: Unknown fragment "+newFragmentName, Toast.LENGTH_LONG).show();
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
