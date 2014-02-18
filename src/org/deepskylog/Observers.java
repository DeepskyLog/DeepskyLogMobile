package org.deepskylog;

import org.deepskylog.DslDialog.DslDialogOnClickListener;
import org.deepskylog.LoginDialog.LoginDialogOnClickListener;

import android.widget.Toast;

public class Observers {
	
	MainActivity mainActivity;
	
	public DslDialogOnClickListener dslOnClickListener;
	public LoginDialogOnClickListener loginOnClickListener;
	
	public Observers(MainActivity theActivity) {
		super();
		mainActivity=theActivity;
	}
	
	public void tellAboutConfigurationMenu() {
	    dslOnClickListener = new DslDialogOnClickListener() {
            @Override public void onPositiveButtonClick() { }
            @Override public void onNegativeButtonClick() { }
        };
        mainActivity.dslDialog=DslDialog.newInstance(dslOnClickListener, "You can always enter your credentials via the configurations menu.", "Ok", "");
        mainActivity.dslDialog.show(mainActivity.getFragmentManager(), "dslDialog");		
	}
	
	public void askForRegistration() {
	    dslOnClickListener = new DslDialogOnClickListener() {
            @Override public void onPositiveButtonClick() { Toast.makeText(mainActivity,"DEVELOP: implement registration fragment",Toast.LENGTH_LONG).show(); }
            @Override public void onNegativeButtonClick() { tellAboutConfigurationMenu();  }
        };
        mainActivity.dslDialog=DslDialog.newInstance(dslOnClickListener, "Do you want to register a new account?", "Yes", "No");
        mainActivity.dslDialog.show(mainActivity.getFragmentManager(), "dslDialog");				
	}

	public void login() {
	    loginOnClickListener = new LoginDialogOnClickListener() {
            @Override public void onPositiveButtonClick() { Toast.makeText(mainActivity, "DEVELOP: check logn? result", Toast.LENGTH_LONG).show(); }
            @Override public void onNegativeButtonClick() { tellAboutConfigurationMenu(); }
        };
        mainActivity.loginDialog=LoginDialog.newInstance(loginOnClickListener);
        mainActivity.loginDialog.show(mainActivity.getFragmentManager(), "loginDialog");		
	}
	
	public void askForLogin() {
	    dslOnClickListener = new DslDialogOnClickListener() {
            @Override public void onPositiveButtonClick() { login(); }
            @Override public void onNegativeButtonClick() { tellAboutConfigurationMenu(); }
        };
        mainActivity.dslDialog=DslDialog.newInstance(dslOnClickListener,"Do you want to enter DSL credentials and log in?","Yes","No");
        mainActivity.dslDialog.show(mainActivity.getFragmentManager(), "dslDialog");		
	}
	
	public void askForUseOfCredentials() {
	    dslOnClickListener = new DslDialogOnClickListener() {
            @Override public void onPositiveButtonClick() { askForLogin(); }
            @Override public void onNegativeButtonClick() { askForRegistration(); }
        };
        mainActivity.dslDialog=DslDialog.newInstance(dslOnClickListener,"Do you already have DSL credentials?","Yes","No");
        mainActivity.dslDialog.show(mainActivity.getFragmentManager(), "dslDialog");		
	}
		
	public void firstRun() {
    	mainActivity.preferenceEditor.putString("loginId","");
    	mainActivity.preferenceEditor.putString("loginPassword","");
    	mainActivity.preferenceEditor.commit();
		askForUseOfCredentials();
	}

}
