package org.deepskylog;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class GetDslCommand {
	
  	public static void getCommandUpacked(String command, String params, String getDslCommandOnResultClass, String getDslCommandOnResultMethod) {
  		params="&onResultMethod="+getDslCommandOnResultMethod+params;
  		params="&onResultClass="+getDslCommandOnResultClass+params;
  		new getCommandTaskUnpacked().execute("http://"+ConnectivityTasks.serverUrl+"appgetcommand.php?command="+command+params);
  	};
        
    private static class getCommandTaskUnpacked extends AsyncTask<String, Void, String> {
    	@Override protected String doInBackground(String... urls) { return Utils.downloadUrl(urls[0]); }
        @Override protected void onPostExecute(String result) {  Utils.onResultUpacked(result.trim()); }
    }
    
  	public static void getCommandRaw(String command, String params, String getDslCommandOnResultClass, String getDslCommandOnResultMethod) {
  		params="&onResultMethod="+getDslCommandOnResultMethod+params;
  		params="&onResultClass="+getDslCommandOnResultClass+params;
  		Toast.makeText(MainActivity.mainActivity, "http://"+ConnectivityTasks.serverUrl+"appgetcommand.php?command="+command+params, Toast.LENGTH_LONG).show();
  		new getCommandTaskRaw().execute("http://"+ConnectivityTasks.serverUrl+"appgetcommand.php?command="+command+params);
  	};
  	
   private static class getCommandTaskRaw extends AsyncTask<String, Void, String> {
    	@Override protected String doInBackground(String... urls) { return Utils.downloadUrl(urls[0]); }
        @Override protected void onPostExecute(String result) {  Utils.onResultRaw(result.trim()); }
    }
    
  	public static void getCommandTest(String command, String params, String getDslCommandOnResultClass, String getDslCommandOnResultMethod) {
  		params="&onResultMethod="+getDslCommandOnResultMethod+params;
  		params="&onResultClass="+getDslCommandOnResultClass+params;
  		new getCommandTaskTest().execute("http://"+ConnectivityTasks.serverUrl+"appgetcommand.php?command="+command+params);
  	};
        
    private static class getCommandTaskTest extends AsyncTask<String, Void, String> {
    	@Override protected String doInBackground(String... urls) { return Utils.downloadUrl(urls[0]); }
        @Override protected void onPostExecute(String result) { Utils.onResultTest(result.trim()); }
    }
 
  	public static void getCommandAndBroadcast(String command, String params, String broadcastIntent) {
  		new getCommandAndBroadcastTask().execute("http://"+ConnectivityTasks.serverUrl+"appgetcommand.php?command="+command,broadcastIntent);
  	};
  	
    private static class getCommandAndBroadcastTask extends AsyncTask<String, Void, String> {
    	@Override protected String doInBackground(String... urls) { return "<broadcastIntent>"+urls[1]+"</broadcastIntent>"+Utils.downloadUrl(urls[0]); }
        @Override protected void onPostExecute(String result) {     
        	Intent intent = new Intent(Utils.getTagContent(result, "broadcastIntent"));
        	intent.putExtra("resultRAW", result.trim());
        	LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(intent);
        	}
    }

    
}
