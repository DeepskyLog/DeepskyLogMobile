package org.deepskylog;

import org.deepskylog.DslDialog.DslDialogOnClickListener;
import org.deepskylog.LoginDialog.LoginDialogOnClickListener;

import android.widget.Toast;

public class Observers {
		
	private static void tellAboutConfigurationMenu() {
	    DslDialog.newInstance(new DslDialogOnClickListener() {
	    						@Override public void onPositiveButtonClick() { }
	    						@Override public void onNeutralButtonClick() { }
	    						@Override public void onNegativeButtonClick() { }
	    					  }, 
	    					  MainActivity.resources.getString(R.string.observers_tellaboutconfigurationmenu),
        					  MainActivity.resources.getString(R.string.general_Ok),
        					  "",
        					  "")
       .show(MainActivity.fragmentManager, "dslDialog");		
	}
	
	private static void askForRegistration() {
	    DslDialog.newInstance(new DslDialogOnClickListener() {
            					@Override public void onPositiveButtonClick() { Toast.makeText(MainActivity.mainActivity,"DEVELOP: implement registration fragment",Toast.LENGTH_LONG).show(); }
            					@Override public void onNeutralButtonClick() { }
            					@Override public void onNegativeButtonClick() { tellAboutConfigurationMenu();  }
        					  }, 
        					  MainActivity.resources.getString(R.string.observers_asktocreatenewaccount),
        					  MainActivity.resources.getString(R.string.general_Yes),
        					  "",
        					  MainActivity.resources.getString(R.string.general_No))
        					  .show(MainActivity.fragmentManager, "dslDialog");				
	}

	public static void login() {
	    LoginDialog.newInstance(new LoginDialogOnClickListener() {
            						@Override public void onPositiveButtonClick() { Toast.makeText(MainActivity.mainActivity, "DEVELOP: check logn? result", Toast.LENGTH_LONG).show(); }
            						@Override public void onNegativeButtonClick() { tellAboutConfigurationMenu(); }
        						})
        						.show(MainActivity.fragmentManager, "loginDialog");		
	}
	
	private static void askForLogin() {
	    DslDialog.newInstance(new DslDialogOnClickListener() {
            					@Override public void onPositiveButtonClick() { login(); }
            					@Override public void onNeutralButtonClick() { }
            					@Override public void onNegativeButtonClick() { tellAboutConfigurationMenu(); }
        						},
        					  MainActivity.resources.getString(R.string.observers_askenteringcredentials),
        					  MainActivity.resources.getString(R.string.general_Yes),
        					  "",
        					  MainActivity.resources.getString(R.string.general_No))
        					  .show(MainActivity.fragmentManager, "dslDialog");		
	}
	
	private static void askForUseOfCredentials() {
	    DslDialog.newInstance(new DslDialogOnClickListener() {
            					@Override public void onPositiveButtonClick() { askForLogin(); }
            					@Override public void onNeutralButtonClick() { }
            					@Override public void onNegativeButtonClick() { askForRegistration(); }
        						},
        					  MainActivity.resources.getString(R.string.observers_askaboutcredentials),
        					  MainActivity.resources.getString(R.string.general_Yes),
        					  "",
        					  MainActivity.resources.getString(R.string.general_No))
        					  .show(MainActivity.fragmentManager, "dslDialog");		
	}
		
	private static void askForUseOfFirstRunTour() {
	    DslDialog.newInstance(new DslDialogOnClickListener() {
            					@Override public void onPositiveButtonClick() { askForUseOfCredentials(); }
            					@Override public void onNeutralButtonClick() { }
            					@Override public void onNegativeButtonClick() { tellAboutConfigurationMenu(); }
        						},
        					  MainActivity.resources.getString(R.string.observers_askaboutfirstruntour),
        					  MainActivity.resources.getString(R.string.general_Yes),
        					  "",
        					  MainActivity.resources.getString(R.string.general_No))
        					  .show(MainActivity.fragmentManager, "dslDialog");		
	}
		
	public static void firstRun() {
    	MainActivity.preferenceEditor.putString("loginId","");
    	MainActivity.preferenceEditor.putString("loginPassword","");
    	MainActivity.preferenceEditor.commit();
    	askForUseOfFirstRunTour();
	}

}
