package org.deepskylog;

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
		if(theKey.equals("positive")) Toast.makeText(MainActivity.mainActivity,"DEVELOP: implement registration fragment",Toast.LENGTH_LONG).show();
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
	
	public static void LoginDialogOnClickListener1(String theKey) {
		if(theKey.equals("positive")) doLogin();
		if(theKey.equals("negative")) tellAboutConfigurationMenu();
	}
	
	private static void doLogin() {
		GetDslCommand.getCommandAndInvokeClassMethod("checkUser", "&userName="+MainActivity.preferences.getString("loginId", "")+"&password="+MainActivity.preferences.getString("loginPassword", ""), "org.deepskylog.Observers","onLoginResult");	
	}
	
	public static void onLoginResult(String resultRaw) {
		//Toast.makeText(MainActivity.mainActivity, resultRaw, Toast.LENGTH_LONG).show();
		try {
			String result=Utils.getTagContent(resultRaw, "result");
			if(result.startsWith("loggedUser:")) MainActivity.loggedPerson=result.substring(11);
			else MainActivity.loggedPerson="";
			MainActivity.preferenceEditor.putString("loggedPerson", MainActivity.loggedPerson).commit();
			MainActivity.mainActivity.setActionBar();
		}
		catch(Exception e) { Toast.makeText(MainActivity.mainActivity, "Observers Exception 1: "+e.toString(), Toast.LENGTH_LONG).show(); }
	}
	
	public static void login() {
	    LoginDialog.newInstance("org.deepskylog.Observers","LoginDialogOnClickListener1",true).show(MainActivity.fragmentManager, "loginDialog");		
	}
	
	public static void ObserversDslDialogListener3(String theKey) {
		if(theKey.equals("positive")) login();
		if(theKey.equals("negative")) tellAboutConfigurationMenu();
	}
	
	@SuppressWarnings("unused")
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
		//if(theKey.equals("positive")) askForLogin();
		if(theKey.equals("positive")) login();
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
    	if(theKey.equals("positive")) askForUseOfCredentials();
		if(theKey.equals("negative")) tellAboutConfigurationMenu();
	}
	
	@SuppressWarnings("unused")
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
    	MainActivity.preferenceEditor.putString("loginId","").putString("loginPassword","").commit();
    	//askForUseOfFirstRunTour();
    	askForUseOfCredentials();
	}

}
