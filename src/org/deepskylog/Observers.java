package org.deepskylog;

import org.deepskylog.DslDialog.DslDialogOnClickListener;
import org.deepskylog.LoginDialog.LoginDialogOnClickListener;

import android.widget.Toast;

public class Observers {
	
	private MainActivity mainActivity;
		
	public Observers(MainActivity theActivity) {
		super();
		mainActivity=theActivity;
	}
	
	private void tellAboutConfigurationMenu() {
	    DslDialog.newInstance(new DslDialogOnClickListener() {
	    						@Override public void onPositiveButtonClick() { }
	    						@Override public void onNeutralButtonClick() { }
	    						@Override public void onNegativeButtonClick() { }
	    					  }, 
	    					  mainActivity.getResources().getString(R.string.observers_tellaboutconfigurationmenu),
        					  mainActivity.getResources().getString(R.string.general_Ok),
        					  "",
        					  "")
       .show(mainActivity.getFragmentManager(), "dslDialog");		
	}
	
	private void askForRegistration() {
	    DslDialog.newInstance(new DslDialogOnClickListener() {
            					@Override public void onPositiveButtonClick() { Toast.makeText(mainActivity,"DEVELOP: implement registration fragment",Toast.LENGTH_LONG).show(); }
            					@Override public void onNeutralButtonClick() { }
            					@Override public void onNegativeButtonClick() { tellAboutConfigurationMenu();  }
        					  }, 
        					  mainActivity.getResources().getString(R.string.observers_asktocreatenewaccount),
        					  mainActivity.getResources().getString(R.string.general_Yes),
        					  "",
        					  mainActivity.getResources().getString(R.string.general_No))
        					  .show(mainActivity.getFragmentManager(), "dslDialog");				
	}

	public void login() {
	    LoginDialog.newInstance(new LoginDialogOnClickListener() {
            						@Override public void onPositiveButtonClick() { Toast.makeText(mainActivity, "DEVELOP: check logn? result", Toast.LENGTH_LONG).show(); }
            						@Override public void onNegativeButtonClick() { tellAboutConfigurationMenu(); }
        						})
        						.show(mainActivity.getFragmentManager(), "loginDialog");		
	}
	
	private void askForLogin() {
	    DslDialog.newInstance(new DslDialogOnClickListener() {
            					@Override public void onPositiveButtonClick() { login(); }
            					@Override public void onNeutralButtonClick() { }
            					@Override public void onNegativeButtonClick() { tellAboutConfigurationMenu(); }
        						},
        					  mainActivity.getResources().getString(R.string.observers_askenteringcredentials),
        					  mainActivity.getResources().getString(R.string.general_Yes),
        					  "",
        					  mainActivity.getResources().getString(R.string.general_No))
        					  .show(mainActivity.getFragmentManager(), "dslDialog");		
	}
	
	private void askForUseOfCredentials() {
	    DslDialog.newInstance(new DslDialogOnClickListener() {
            					@Override public void onPositiveButtonClick() { askForLogin(); }
            					@Override public void onNeutralButtonClick() { }
            					@Override public void onNegativeButtonClick() { askForRegistration(); }
        						},
        					  mainActivity.getResources().getString(R.string.observers_askaboutcredentials),
        					  mainActivity.getResources().getString(R.string.general_Yes),
        					  "",
        					  mainActivity.getResources().getString(R.string.general_No))
        					  .show(mainActivity.getFragmentManager(), "dslDialog");		
	}
		
	private void askForUseOfFirstRunTour() {
	    DslDialog.newInstance(new DslDialogOnClickListener() {
            					@Override public void onPositiveButtonClick() { askForUseOfCredentials(); }
            					@Override public void onNeutralButtonClick() { }
            					@Override public void onNegativeButtonClick() { tellAboutConfigurationMenu(); }
        						},
        					  mainActivity.getResources().getString(R.string.observers_askaboutfirstruntour),
        					  mainActivity.getResources().getString(R.string.general_Yes),
        					  "",
        					  mainActivity.getResources().getString(R.string.general_No))
        					  .show(mainActivity.getFragmentManager(), "dslDialog");		
	}
		
	public void firstRun() {
    	mainActivity.preferenceEditor.putString("loginId","");
    	mainActivity.preferenceEditor.putString("loginPassword","");
    	mainActivity.preferenceEditor.commit();
    	askForUseOfFirstRunTour();
	}

}
