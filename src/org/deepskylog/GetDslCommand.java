package org.deepskylog;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class GetDslCommand {
	
	private static final String SERVER_URL="http://www.deepskylog.org/";
	        
   	public static void getCommandAndInvokeClassMethod(String command, String params, String getDslCommandOnResultClass, String getDslCommandOnResultMethod) {
   		MainActivity.mainActivity.setProgressBarIndeterminateVisibility(true);
	    new getCommandAndInvokeClassMethodTask().execute(SERVER_URL+"appgetcommand.php?command="+command+params,getDslCommandOnResultClass,getDslCommandOnResultMethod);
  	};
  	
    private static class getCommandAndInvokeClassMethodTask extends AsyncTask<String, Void, String> {
    	@Override protected String doInBackground(String... urls) { return "<onResultClass>"+urls[1]+"</onResultClass>"+
    																	   "<onResultMethod>"+urls[2]+"</onResultMethod>"+
    																	   Utils.downloadUrl(urls[0]); }
        @Override protected void onPostExecute(String result) { Utils.invokeClassMethodWithResult(result.trim()); }
    }

    public static void getCommandAndBroadcast(String command, String params, String broadcastIntent) {
    	MainActivity.mainActivity.setProgressBarIndeterminateVisibility(true);
	    new getCommandAndBroadcastTask().execute(SERVER_URL+"appgetcommand.php?command="+command,broadcastIntent);
  	};
  	
    private static class getCommandAndBroadcastTask extends AsyncTask<String, Void, String> {
    	@Override protected String doInBackground(String... urls) { return "<broadcastIntent>"+urls[1]+"</broadcastIntent>"+Utils.downloadUrl(urls[0]); }
        @Override protected void onPostExecute(String result) { 
        	try { LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent(Utils.getTagContent(result, "broadcastIntent")).putExtra("org.deepskylog.resultRAW", result.trim())); }
	    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,e.toString(),Toast.LENGTH_LONG).show(); }
 			}
    }
}
