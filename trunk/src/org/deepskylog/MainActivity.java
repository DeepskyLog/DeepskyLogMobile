package org.deepskylog;

import java.io.File;

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
import android.os.Environment;
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
	
	private static MainFragment mainFragment;
	private static DeepskyFragment deepskyFragment;
	private static DeepskyObservationsFragment deepskyObservationsFragment;
	private static DeepskyObservationsDetailsFragment deepskyObservationsDetailsFragment;
	private static DeepskyObservationsListFragment deepskyObservationsListFragment;
	private static DeepskyObservationsQueryFragment deepskyObservationsQueryFragment;
	private static DeepskyObjectsQueryFragment deepskyObjectsQueryFragment;
	private static CometFragment cometFragment;
	private static CometObservationsFragment cometObservationsFragment;
	private static CometObservationsDetailsFragment cometObservationsDetailsFragment;
	private static CometObservationsListFragment cometObservationsListFragment;
	private static CometObservationsQueryFragment cometObservationsQueryFragment;
	private static CometObjectsQueryFragment cometObjectsQueryFragment;
	private static ObserversFragment observersFragment;
	private static EphemeridesFragment ephemeridesFragment;
	private static SettingsFragment settingsFragment;
	
	public static Fragment actualFragment;
	public static String actualFragmentName;

	public static String storagePath;

	private static ActionBar actionBar;
	private static String onLine="offLine";
	public static String loggedPerson="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.mainactivity);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
		checkStateObjects();
		setStateParametersFromPreferences();
    	actualFragmentName="mainFragment";
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
		/*Toast.makeText(this, "dir create "+Environment.getExternalStorageDirectory()+File.separator+"dsl", Toast.LENGTH_LONG).show();
		File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"dsl");
		directory.mkdirs();
		directory = new File(Environment.getExternalStorageDirectory()+File.separator+"dsl"+File.separator+"deepsky");
		directory.mkdirs();
		directory = new File(Environment.getExternalStorageDirectory()+File.separator+"dsl"+File.separator+"deepsky"+File.separator+"images");
		directory.mkdirs();
		directory = new File(Environment.getExternalStorageDirectory()+File.separator+"dsl"+File.separator+"comet");
		directory.mkdirs();
		directory = new File(Environment.getExternalStorageDirectory()+File.separator+"dsl"+File.separator+"comet"+File.separator+"images");
		directory.mkdirs();
		*/
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
	    	Toast.makeText(this, "To implement", Toast.LENGTH_LONG).show();
    		//ConnectivityTasks.checkLogin();	
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
		if(key.equals("loginId")||key.equals("loginPassword")) {
	    	GetDslCommand.getCommandAndInvokeClassMethod("checkUser", "&userName="+MainActivity.preferences.getString("loginId", "")+"&password="+MainActivity.preferences.getString("loginPassword", ""), "org.deepskylog.Observers","onLoginResult");	
		}
	}
	
	public void setActionBar() {
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
		storagePath=Environment.getExternalStorageDirectory().getPath()+"/dsl";
    	
    	DslDatabase.open();
    	DeepskyObservations.init();
    	
    	if(mainFragment==null) mainFragment=new MainFragment();
    	if(deepskyFragment==null) deepskyFragment=new DeepskyFragment();
    	if(deepskyObservationsFragment==null) deepskyObservationsFragment=new DeepskyObservationsFragment();
    	if(deepskyObservationsDetailsFragment==null) deepskyObservationsDetailsFragment=new DeepskyObservationsDetailsFragment();
    	if(deepskyObservationsListFragment==null) deepskyObservationsListFragment=new DeepskyObservationsListFragment();
    	if(deepskyObservationsQueryFragment==null) deepskyObservationsQueryFragment=new DeepskyObservationsQueryFragment();
    	if(deepskyObjectsQueryFragment==null) deepskyObjectsQueryFragment=new DeepskyObjectsQueryFragment();
    	
    	
    	if(cometFragment==null) cometFragment=new CometFragment();
    	if(cometObservationsFragment==null) cometObservationsFragment=new CometObservationsFragment();
    	if(cometObservationsDetailsFragment==null) cometObservationsDetailsFragment=new CometObservationsDetailsFragment();
    	if(cometObservationsListFragment==null) cometObservationsListFragment=new CometObservationsListFragment();
    	if(cometObservationsQueryFragment==null) cometObservationsQueryFragment=new CometObservationsQueryFragment();
    	if(cometObjectsQueryFragment==null) cometObjectsQueryFragment=new CometObjectsQueryFragment();
    	
  	if(observersFragment==null) observersFragment=new ObserversFragment();
    	if(ephemeridesFragment==null) ephemeridesFragment=new EphemeridesFragment();
		if(settingsFragment==null) settingsFragment=new SettingsFragment();

	}
	
	private static void setStateParametersFromPreferences() {
     	loggedPerson=preferences.getString("loggedPerson", "");
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putString(ACTUAL_FRAGMENT, (actualFragmentName==null?"":actualFragmentName));
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	private static boolean setFragment(String newFragmentName) {
		if(newFragmentName.equals("mainFragment")) actualFragment=mainFragment;
		else if(newFragmentName.equals("deepskyFragment")) actualFragment=deepskyFragment;
		else if(newFragmentName.equals("deepskyObservationsFragment")) actualFragment=deepskyObservationsFragment;
		else if(newFragmentName.equals("deepskyObservationsFragment")) actualFragment=deepskyFragment;
		else if(newFragmentName.equals("deepskyObservationsDetailsFragment")) { actualFragment=deepskyObservationsDetailsFragment; }
		else if(newFragmentName.equals("deepskyObservationsListFragment")) { actualFragment=deepskyObservationsListFragment; }
		else if(newFragmentName.equals("deepskyObservationsQueryFragment")) { actualFragment=deepskyObservationsQueryFragment; }
		else if(newFragmentName.equals("deepskyObjectsQueryFragment")) { actualFragment=deepskyObjectsQueryFragment; }
		else if(newFragmentName.equals("cometFragment")) actualFragment=cometFragment;
		else if(newFragmentName.equals("cometObservationsFragment")) actualFragment=cometObservationsFragment;
		else if(newFragmentName.equals("cometObservationsFragment")) actualFragment=cometFragment;
		else if(newFragmentName.equals("cometObservationsDetailsFragment")) { actualFragment=cometObservationsDetailsFragment; }
		else if(newFragmentName.equals("cometObservationsListFragment")) { actualFragment=cometObservationsListFragment; }
		else if(newFragmentName.equals("cometObservationsQueryFragment")) { actualFragment=cometObservationsQueryFragment; }
		else if(newFragmentName.equals("cometObjectsQueryFragment")) { actualFragment=cometObjectsQueryFragment; }
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
			File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"dsl");
			directory.mkdirs();
			directory = new File(Environment.getExternalStorageDirectory()+File.separator+"dsl"+File.separator+"deepsky");
			directory.mkdirs();
			directory = new File(Environment.getExternalStorageDirectory()+File.separator+"dsl"+File.separator+"deepsky"+File.separator+"images");
			directory.mkdirs();
			directory = new File(Environment.getExternalStorageDirectory()+File.separator+"dsl"+File.separator+"comet");
			directory.mkdirs();
			directory = new File(Environment.getExternalStorageDirectory()+File.separator+"dsl"+File.separator+"comet"+File.separator+"images");
			directory.mkdirs();
			Observers.firstRun();
			preferenceEditor.putBoolean("firstrun", false).commit();
	    }
	}
	
}
