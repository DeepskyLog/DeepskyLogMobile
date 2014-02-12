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

	public  SharedPreferences preferences;
	public SharedPreferences.Editor preferenceEditor;
	
	public static String loggedPerson = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainactivity);
		mainFragment = new MainFragment();
		settingsFragment = new SettingsFragment();
		loginDialog = new LoginDialog();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);		 
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	preferenceEditor = preferences.edit();
		if(savedInstanceState==null) {
			actualFragmentName="mainFragment";		
			goToFragment(DONT_ADD_TO_BACKSTACK);			
		}
		else {
			actualFragmentName=savedInstanceState.getString(ACTUAL_FRAGMENT);	
			setFragment();
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
	    	actualFragment=settingsFragment;
			actualFragmentName="settingsFragment";
	    	break;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	    goToFragment(ADD_TO_BACKSTACK);
	    return true;
	}
	private static boolean isNumeric(String str) {
	    for (char c : str.toCharArray()) {
	        if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {    	
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
	protected void onResume() {
	    super.onResume();
	    preferences.registerOnSharedPreferenceChangeListener(this);
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
	
	private void setFragment() {
		if(actualFragmentName.equals("mainFragment")) actualFragment=mainFragment;
		else if(actualFragmentName.equals("settingsFragment")) actualFragment=settingsFragment;
		else if(actualFragmentName.equals("loginDialog")) actualFragment=loginDialog;
		else Toast.makeText(this, "Debug: Unknown fragment", Toast.LENGTH_LONG).show();
	}
	
	public void goToFragment(boolean doAddToBackstack) {
		FragmentTransaction fragmentManagerTransaction;
		fragmentManagerTransaction = getFragmentManager().beginTransaction();
		setFragment();
		fragmentManagerTransaction.replace(R.id.mainfragment_container, actualFragment);
		if(doAddToBackstack)
			fragmentManagerTransaction.addToBackStack(null);
		fragmentManagerTransaction.commit();	    	
	}

}
