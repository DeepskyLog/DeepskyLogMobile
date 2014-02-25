package org.deepskylog;

import android.os.AsyncTask;

public class GetDslCommand {
	
  	public static void getCommand(String command, String params, String getDslCommandOnResultClass, String getDslCommandOnResultMethod) {
  		params="&onResultMethod="+getDslCommandOnResultMethod+params;
  		params="&onResultClass="+getDslCommandOnResultClass+params;
  		new getCommandTask().execute("http://"+ConnectivityTasks.serverUrl+"appgetcommand.php?command="+command+params);
  	};
        
    private static class getCommandTask extends AsyncTask<String, Void, String> {
    	@Override protected String doInBackground(String... urls) { return Utils.downloadUrl(urls[0]); }
        @Override protected void onPostExecute(String result) { Utils.onResult(result.trim()); }
    }
    
}
