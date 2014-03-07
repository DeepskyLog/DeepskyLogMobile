package org.deepskylog;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class Observations {

	public static void broadcastDeepskyObservationsMaxId() { GetDslCommand.getCommandAndBroadcast("maxobservationid", "", "org.deepskylog.broadcastmaxobservationid"); }
    public static void broadcastDeepskyObservation(String observationid) { executeBroadcastObservation(observationid); }

	private static void broadcastObservationResult(String resultRaw) {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastobservation").putExtra("org.deepskylog.resultRAW", resultRaw));
	}
	
    private static void executeBroadcastObservation(String observationid) {
    	Cursor cursor=DslDatabase.execSql("SELECT observations.* FROM observations WHERE observationid=\""+observationid+"\"");
    	if(cursor.moveToFirst()) {
			broadcastObservationResult("<observationid>"+cursor.getString(cursor.getColumnIndexOrThrow("observationid"))+"</observationid>" +
									   "<result>[ { \"observationid\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observationid"))+"\", " +
									   "\"objectname\":\""+cursor.getString(cursor.getColumnIndexOrThrow("objectname"))+"\", "+
					       			   "\"observername\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observername"))+"\", "+
					       			   "\"observationdate\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observationdate"))+"\","+
					       			   "\"instrumentname\":\""+cursor.getString(cursor.getColumnIndexOrThrow("instrumentname"))+"\","+
					       			   "\"observationdescription\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observationdescription")).replace("\"", "'")+"\" "+
					       			   "} ]>" +
					       			   "</result>");
		}
		else {
			MainActivity.mainActivity.setProgressBarIndeterminateVisibility(true);
		    GetDslCommand.getCommandRaw("observationfromid", "&fromid="+observationid, "org.deepskylog.Observations", "storeObservationToDbAndBroadcast");
		}
		cursor.close();
	}
	
	public static void storeObservationToDbAndBroadcast(String observationRaw) {
    	String result=Utils.getTagContent(observationRaw,"result");
		if(result.startsWith("Unavailable url:")) {
    		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastnoobservation").putExtra("org.deepskylog.resultRAW", "<observationid>"+result.substring(result.indexOf("fromid=")+7)+"</observationid>"));
    	}
		else { 
			String observationId=Utils.getTagContent(observationRaw,"observationid");
			if(result.equals("[\"No data\"]")) {
		    	ContentValues initialValues = new ContentValues();
	        	initialValues.put("observationid", observationId);
	            initialValues.put("objectname", "No data");
	            initialValues.put("observername", "No data");
	            initialValues.put("observationdate", "No data");
	            initialValues.put("instrumentname", "No data");
	            initialValues.put("observationdescription", "No data");
	            DslDatabase.insert("observations", initialValues);
	    	}
	    	else {
	    		try {
			    	JSONArray jsonArray = new JSONArray(result);
			    	if(jsonArray.length()>0) {
					    JSONObject jsonObject=jsonArray.getJSONObject(0);
				    	ContentValues initialValues = new ContentValues();
			        	initialValues.put("observationid", jsonObject.getString("observationid"));
			            initialValues.put("objectname", jsonObject.getString("objectname"));
			            initialValues.put("observername", jsonObject.getString("observername"));
			            initialValues.put("observationdate", jsonObject.getString("observationdate"));
			            initialValues.put("instrumentname", jsonObject.getString("instrumentname"));
			            initialValues.put("observationdescription", jsonObject.getString("observationdescription").replace("\"", "'"));
			            DslDatabase.insert("observations", initialValues);
			    	}
		        } catch (Exception e) {
		            Toast.makeText(MainActivity.mainActivity, "Observations Exception 1 "+e.toString(), Toast.LENGTH_LONG).show();
		        }
	    	}
			executeBroadcastObservation(observationId);
		}
	}
	
}
