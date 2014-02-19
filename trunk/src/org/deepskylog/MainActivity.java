package org.deepskylog;

import org.deepskylog.DslDialog.DslDialogOnClickListener;

import android.app.ActionBar;
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

public class MainActivity 	extends 	Activity 
							implements 	OnSharedPreferenceChangeListener {

	private static final String ACTUAL_FRAGMENT = "ACTUAL_FRAGMENT";
	public static final boolean ADD_TO_BACKSTACK = true;
	public static final boolean DONT_ADD_TO_BACKSTACK = false;
	
	public SharedPreferences preferences;
	public SharedPreferences.Editor preferenceEditor;

	public static String loggedPerson = "";

	public ActionBar actionBar;
	
	public MainFragment mainFragment;
	public DeepskyFragment deepskyFragment;
	public CometsFragment cometsFragment;
	public ObserversFragment observersFragment;
	public EphemeridesFragment ephemeridesFragment;
	public SettingsFragment settingsFragment;
	
	public ConnectivityTasks connectivityTasks;
	public Database database;
	public Observers observers;
		
	public Fragment actualFragment;
	public String actualFragmentName;

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
			setFragment(savedInstanceState.getString(ACTUAL_FRAGMENT));
		}
    	setStateParameters();
		actionBar.setTitle(getResources().getString(R.string.actionbar_title_text));
		actionBar.setSubtitle(getResources().getString(R.string.actionbar_connectivity_N));
    	checkFirstRun();
		connectivityTasks.checkAutoConnectivityStatus();
	}

	@Override 
	protected void onStart() {
    	super.onStart();
		//connectivityTasks.checkAutoConnectivityStatus();
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
	protected void onPause() {
	    super.onPause();
	    preferences.unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    savedInstanceState.putString(ACTUAL_FRAGMENT, actualFragmentName);
	}
	
		
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {    	
	Toast.makeText(this, "DEVELOP: code for changes in login name, password or auto login etc",Toast.LENGTH_LONG).show();
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
	
	private void checkStateObjects() {
		if(actionBar==null) actionBar=getActionBar();
    	if(mainFragment==null) mainFragment=new MainFragment();
    	if(deepskyFragment==null) deepskyFragment=new DeepskyFragment();
    	if(cometsFragment==null) cometsFragment=new CometsFragment();
    	if(observersFragment==null) observersFragment=new ObserversFragment();
    	if(ephemeridesFragment==null) ephemeridesFragment=new EphemeridesFragment();
		if(settingsFragment==null) settingsFragment=new SettingsFragment();
		
        if(preferences==null) preferences=PreferenceManager.getDefaultSharedPreferences(this);
    	if(preferenceEditor==null) preferenceEditor=preferences.edit();		

    	if(database==null) database=new Database();
		if(connectivityTasks==null) connectivityTasks=new ConnectivityTasks(this);
        if(observers==null) observers=new Observers(this);
	}
	
	private void setStateParameters() {
     	loggedPerson=preferences.getString("loggedPerson", "");
	}
	
	private boolean setFragment(String newFragmentName) {
		if(newFragmentName.equals("mainFragment")) actualFragment=mainFragment;
		else if(newFragmentName.equals("deepskyFragment")) actualFragment=deepskyFragment;
		else if(newFragmentName.equals("cometsFragment")) actualFragment=cometsFragment;
		else if(newFragmentName.equals("observersFragment")) actualFragment=observersFragment;
		else if(newFragmentName.equals("ephemeridesFragment")) actualFragment=ephemeridesFragment;
		else if(newFragmentName.equals("settingsFragment")) actualFragment=settingsFragment;
		else {
			Toast.makeText(this, "Debug: Unknown fragment "+newFragmentName, Toast.LENGTH_LONG).show();
			return false;
		}
		actualFragmentName=newFragmentName;
		return true;
	}
	
	public void goToFragment(String newFragmentName, boolean doAddToBackstack) {
		if(setFragment(newFragmentName)) {
			FragmentTransaction fragmentManagerTransaction;
			fragmentManagerTransaction = getFragmentManager().beginTransaction();
			fragmentManagerTransaction.replace(R.id.mainfragment_container, actualFragment);
			if(doAddToBackstack) {
				fragmentManagerTransaction.addToBackStack(actualFragmentName);
			}
			fragmentManagerTransaction.commit();
		}
	}	

	public void checkFirstRun() {
		if (preferences.getBoolean("firstrun", true)) {
			database.firstRun();
			observers.firstRun();
			preferenceEditor.putBoolean("firstrun", false).commit();
	    }
	}
	
}
