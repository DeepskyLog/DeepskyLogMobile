package org.deepskylog;

import org.deepskylog.LoginDialog.LoginDialogOnClickListener;

import android.widget.Toast;

public class Observers {
		
	private static void tellAboutConfigurationMenu() {
	    DslDialog.newInstance("","", 
	    					  MainActivity.resources.getString(R.string.observers_tellaboutconfigurationmenu),
        					  MainActivity.resources.getString(R.string.general_Ok),
        					  "",
        					  "",
        					  true)
       .show(MainActivity.fragmentManager, "dslDialog");		
	}
	
	public static void ObserversDslDialogListener4(String theKey) {
		if(theKey.equals("positive"))Toast.makeText(MainActivity.mainActivity,"DEVELOP: implement registration fragment",Toast.LENGTH_LONG).show();
		if(theKey.equals("negative")) tellAboutConfigurationMenu();
	}
	private static void askForRegistration() {
	    DslDialog.newInstance("org.deepskylog.Observers","ObserversDslDialogListener4", 
        					  MainActivity.resources.getString(R.string.observers_asktocreatenewaccount),
        					  MainActivity.resources.getString(R.string.general_Yes),
        					  "",
        					  MainActivity.resources.getString(R.string.general_No),
        					  true)
        					  .show(MainActivity.fragmentManager, "dslDialog");				
	}

	public static void login() {
	    LoginDialog.newInstance(new LoginDialogOnClickListener() {
            						@Override public void onPositiveButtonClick() { Toast.makeText(MainActivity.mainActivity, "DEVELOP: check logn? result", Toast.LENGTH_LONG).show(); }
            						@Override public void onNegativeButtonClick() { tellAboutConfigurationMenu(); }
        						})
        						.show(MainActivity.fragmentManager, "loginDialog");		
	}
	
	public static void ObserversDslDialogListener3(String theKey) {
		if(theKey.equals("positive")) login();
		if(theKey.equals("negative")) tellAboutConfigurationMenu();
	}
	private static void askForLogin() {
	    DslDialog.newInstance("org.deepskylog.Observers","ObserversDslDialogListener3",
        					  MainActivity.resources.getString(R.string.observers_askenteringcredentials),
        					  MainActivity.resources.getString(R.string.general_Yes),
        					  "",
        					  MainActivity.resources.getString(R.string.general_No),
        					  true)
        					  .show(MainActivity.fragmentManager, "dslDialog");		
	}
	
	public static void ObserversDslDialogListener2(String theKey) {
		if(theKey.equals("positive")) askForLogin();
		if(theKey.equals("negative")) askForRegistration();
	}
	private static void askForUseOfCredentials() {
	    DslDialog.newInstance("org.deepskylog.Observers","ObserversDslDialogListener2",
        					  MainActivity.resources.getString(R.string.observers_askaboutcredentials),
        					  MainActivity.resources.getString(R.string.general_Yes),
        					  "",
        					  MainActivity.resources.getString(R.string.general_No),
        					  true)
        					  .show(MainActivity.fragmentManager, "dslDialog");		
	}
	
	public static void ObserversDslDialogListener1(String theKey) {
		Toast.makeText(MainActivity.mainActivity,"WP", Toast.LENGTH_SHORT).show();
    	if(theKey.equals("positive")) askForUseOfCredentials();
		if(theKey.equals("negative")) tellAboutConfigurationMenu();
	}
	private static void askForUseOfFirstRunTour() {
	    DslDialog.newInstance("org.deepskylog.Observers","ObserversDslDialogListener1",
        					  MainActivity.resources.getString(R.string.observers_askaboutfirstruntour),
        					  MainActivity.resources.getString(R.string.general_Yes),
        					  "",
        					  MainActivity.resources.getString(R.string.general_No),
        					  true )
        					  .show(MainActivity.fragmentManager, "dslDialog");		
	}
		
	public static void firstRun() {
    	MainActivity.preferenceEditor.putString("loginId","");
    	MainActivity.preferenceEditor.putString("loginPassword","");
    	MainActivity.preferenceEditor.commit();
    	askForUseOfFirstRunTour();
	}

}
