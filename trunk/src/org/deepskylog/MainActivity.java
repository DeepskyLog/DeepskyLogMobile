package org.deepskylog;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

public class MainActivity 	extends 	Activity 
							implements 	OnSharedPreferenceChangeListener {
	
	private static final String ACTUAL_FRAGMENT="ACTUAL_FRAGMENT";
	public static final boolean ADD_TO_BACKSTACK= true;
	public static final boolean DONT_ADD_TO_BACKSTACK=false;
	
	public static MainActivity mainActivity;
	
	public static FragmentManager fragmentManager;
	public static Resources resources;
	public static SharedPreferences preferences;
	public static SharedPreferences.Editor preferenceEditor;
	
	public static MainFragment mainFragment;
	public static DeepskyFragment deepskyFragment;
	public static CometsFragment cometsFragment;
	public static ObserversFragment observersFragment;
	public static EphemeridesFragment ephemeridesFragment;
	public static SettingsFragment settingsFragment;
	
	public static Fragment actualFragment;
	public static String actualFragmentName;


	//public ConnectivityTasks connectivityTasks;
	//public static DslDatabase dslDatabase;
	//public Observers observers;

	private static ActionBar actionBar;
	private static String onLine="offLine";
	private static String loggedPerson="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.mainactivity);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
		checkStateObjects();
		setStateParametersFromPreferences();
    	if(savedInstanceState==null) {
         	goToFragment("mainFragment",DONT_ADD_TO_BACKSTACK);			
		}
		else {
			setFragment(savedInstanceState.getString(ACTUAL_FRAGMENT));
		}
    	setActionBar();
    	LocalBroadcastManager.getInstance(this).registerReceiver(actionBarSubTitleReceiver1, new IntentFilter("org.deepskylog.online"));
		LocalBroadcastManager.getInstance(this).registerReceiver(actionBarSubTitleReceiver2, new IntentFilter("org.deepskylog.loggedperson"));
		setProgressBarIndeterminateVisibility(false);
    	checkFirstRun();
	}

	@Override 
	protected void onStart() {
    	super.onStart();
		checkStateObjects();
		setStateParametersFromPreferences();
	}

	@Override
	protected void onResume() {
	    super.onResume();
	    preferences.registerOnSharedPreferenceChangeListener(this);
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
    	case R.id.mainmenu_login_id:
	    	ConnectivityTasks.checkLogin();	
			break;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	    return true;
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    preferences.unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(actionBarSubTitleReceiver1);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(actionBarSubTitleReceiver2);
		DslDatabase.close();
		super.onStop();
	}	
		
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {    	
    //	Toast.makeText(this, "DEVELOP: code for changes in login name, password or auto login etc",Toast.LENGTH_LONG).show();
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
	
	private void setActionBar() {
		if(MainActivity.onLine.equals("online")) {
			if(loggedPerson.equals("")) actionBar.setSubtitle(getResources().getString(R.string.actionbar_connectivity_V)+resources.getString(R.string.actionbar_subtitle_text_online));
			else actionBar.setSubtitle(loggedPerson+resources.getString(R.string.actionbar_subtitle_text_online));
		}
		else if(MainActivity.onLine.equals("offline")) {
			if(loggedPerson.equals("")) actionBar.setSubtitle(getResources().getString(R.string.actionbar_connectivity_V)+resources.getString(R.string.actionbar_subtitle_text_offline));
			else actionBar.setSubtitle(loggedPerson+resources.getString(R.string.actionbar_subtitle_text_offline));
		}
		else {
			if(loggedPerson.equals("")) actionBar.setSubtitle(getResources().getString(R.string.actionbar_connectivity_V)+resources.getString(R.string.actionbar_subtitle_text_offline));
			else actionBar.setSubtitle(loggedPerson);
		}
	}
	
	public BroadcastReceiver actionBarSubTitleReceiver1=new BroadcastReceiver() {
		@Override 
		public void onReceive(Context context, Intent intent) {
			MainActivity.onLine=intent.getStringExtra("org.deepskylog.online");
			setActionBar();
		}
	};
	public BroadcastReceiver actionBarSubTitleReceiver2=new BroadcastReceiver() {
		@Override 
		public void onReceive(Context context, Intent intent) {
			MainActivity.loggedPerson=intent.getStringExtra("org.deepskylog.loggedperson");
			setActionBar();
		}
	};
	
	private void checkStateObjects() {
		mainActivity=this;
		actionBar=getActionBar();
		fragmentManager=getFragmentManager();
        preferences=PreferenceManager.getDefaultSharedPreferences(this);
    	preferenceEditor=preferences.edit();		
		resources=getResources();
    	
		if(mainFragment==null) mainFragment=new MainFragment();
    	if(deepskyFragment==null) deepskyFragment=new DeepskyFragment();
    	if(cometsFragment==null) cometsFragment=new CometsFragment();
    	if(observersFragment==null) observersFragment=new ObserversFragment();
    	if(ephemeridesFragment==null) ephemeridesFragment=new EphemeridesFragment();
		if(settingsFragment==null) settingsFragment=new SettingsFragment();
		
    	ConnectivityTasks.initConnectivityTasks();
    	DslDatabase.open();

	}
	
	private static void setStateParametersFromPreferences() {
     	loggedPerson=preferences.getString("loggedPerson", "");
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    savedInstanceState.putString(ACTUAL_FRAGMENT, actualFragmentName);
	}
	
	private static boolean setFragment(String newFragmentName) {
		if(newFragmentName.equals("mainFragment")) actualFragment=mainFragment;
		else if(newFragmentName.equals("deepskyFragment")) actualFragment=deepskyFragment;
		else if(newFragmentName.equals("cometsFragment")) actualFragment=cometsFragment;
		else if(newFragmentName.equals("observersFragment")) actualFragment=observersFragment;
		else if(newFragmentName.equals("ephemeridesFragment")) actualFragment=ephemeridesFragment;
		else if(newFragmentName.equals("settingsFragment")) actualFragment=settingsFragment;
		else {
			Toast.makeText(mainActivity, "Debug: Unknown fragment "+newFragmentName, Toast.LENGTH_LONG).show();
			return false;
		}
		actualFragmentName=newFragmentName;
		return true;
	}
	
	public static void goToFragment(String newFragmentName, boolean doAddToBackstack) {
		if(setFragment(newFragmentName)) {
			FragmentTransaction fragmentManagerTransaction;
			fragmentManagerTransaction = fragmentManager.beginTransaction();
			fragmentManagerTransaction.replace(R.id.mainfragment_container, actualFragment);
			if(doAddToBackstack) {
				fragmentManagerTransaction.addToBackStack(actualFragmentName);
			}
			fragmentManagerTransaction.commit();
		}
	}	

	public static void checkFirstRun() {
		if (preferences.getBoolean("firstrun", true)) {
			Observers.firstRun();
			preferenceEditor.putBoolean("firstrun", false).commit();
	    }
	}
	
}
