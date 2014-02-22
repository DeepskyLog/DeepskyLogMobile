package org.deepskylog;

import android.os.AsyncTask;

public class GetDslCommand {
	
    public interface GetDslCommandOnResult {
        void onResultAvailable(String result);
    }
	 
    public static GetDslCommandOnResult getDslCommandOnResult;
		
  	public static void getCommand(String command, String params, GetDslCommandOnResult theDslCommandOnResult) {
  		getDslCommandOnResult=theDslCommandOnResult;
  		new getCommandTask().execute("http://"+ConnectivityTasks.serverUrl+"appgetcommand.php?command="+command+params);
  	};
        
    private static class getCommandTask extends AsyncTask<String, Void, String> {
    	@Override protected String doInBackground(String... urls) { return Utils.downloadUrl(urls[0]); }
        @Override protected void onPostExecute(String result) { getDslCommandOnResult.onResultAvailable(result.trim()); }
    }   

}
