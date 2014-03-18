package org.deepskylog;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;


public class DeepskyObservations {

	public static void broadcastDeepskyObservationsMaxId() { GetDslCommand.getCommandAndBroadcast("maxDeepskyObservationId", "", "org.deepskylog.broadcastmaxdeepskyobservationid"); }
    public static void broadcastDeepskyObservation(String deepskydeepskyObservationId) { executeBroadcastDeepskyObservation(deepskydeepskyObservationId); }

	private static void broadcastDeepskyObservationResult(String resultRaw) {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservation").putExtra("org.deepskylog.resultRAW", resultRaw));
	}
	
    private static void executeBroadcastDeepskyObservation(String deepskydeepskyObservationId) {
    	Cursor cursor=DslDatabase.execSql("SELECT deepskyObservations.* FROM deepskyObservations WHERE deepskyObservationId=\""+deepskydeepskyObservationId+"\"");
    	if(cursor.moveToFirst()) {
			broadcastDeepskyObservationResult("<deepskyObservationId>"+cursor.getString(cursor.getColumnIndexOrThrow("deepskyObservationId"))+"</deepskyObservationId>" +
									   "<result>[ { \"deepskyObservationId\":\""+cursor.getString(cursor.getColumnIndexOrThrow("deepskyObservationId"))+"\", " +
									   "\"objectName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("objectName"))+"\", "+
					       			   "\"observerName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observerName"))+"\", "+
					       			   "\"observationDate\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observationDate"))+"\","+
					       			   "\"instrumentName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("instrumentName"))+"\","+
					       			   "\"observationDescription\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observationDescription")).replace("\"", "'")+"\" "+
					       			   "} ]>" +
					       			   "</result>");
		}
		else {
			MainActivity.mainActivity.setProgressBarIndeterminateVisibility(true);
		    GetDslCommand.getCommandAndInvokeClassMethod("deepskyObservationFromId", "&fromid="+deepskydeepskyObservationId, "org.deepskylog.DeepskyObservations", "storeDeepskyObservationToDbAndBroadcast");
		}
		cursor.close();
	}
	
	public static void storeDeepskyObservationToDbAndBroadcast(String observationRaw) {
		try {
    		String result=Utils.getTagContent(observationRaw,"result");
			if(result.startsWith("Unavailable url:")) {
				//TODO change second index of substring in next line
	    		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastnodeepskyobservation").putExtra("org.deepskylog.resultRAW", "<deepskyObservationId>"+result.substring(result.indexOf("fromid=")+7)+"</deepskyObservationId>"));
	    	}
			else { 
				//TO DO: change to deepskyObservationId
				String deepskyObservationId=Utils.getTagContent(observationRaw,"observationid");
				if(result.equals("[\"No data\"]")) {
			    	ContentValues initialValues = new ContentValues();
		        	initialValues.put("deepskyObservationId", deepskyObservationId);
		            initialValues.put("objectName", "No data");
		            initialValues.put("observerName", "No data");
		            initialValues.put("observationDate", "No data");
		            initialValues.put("instrumentName", "No data");
		            initialValues.put("observationDescription", "No data");
		            DslDatabase.insert("deepskyObservations", initialValues);
		    	}
		    	else {
		    		try {
				    	JSONArray jsonArray = new JSONArray(result);
				    	if(jsonArray.length()>0) {
						    JSONObject jsonObject=jsonArray.getJSONObject(0);
					    	ContentValues initialValues = new ContentValues();
				        	initialValues.put("deepskyObservationId", jsonObject.getString("observationid"));
				            initialValues.put("objectName", jsonObject.getString("objectname"));
				            initialValues.put("observerName", jsonObject.getString("observername"));
				            initialValues.put("observationDate", jsonObject.getString("observationdate"));
				            initialValues.put("instrumentName", jsonObject.getString("instrumentname"));
				            initialValues.put("observationDescription", jsonObject.getString("observationdescription").replace("\"", "'"));
				            DslDatabase.insert("deepskyObservations", initialValues);
				    	}
			        } 
		    		catch(Exception e) { Toast.makeText(MainActivity.mainActivity, "DeepskyObservations: Exception 1 "+e.toString(), Toast.LENGTH_LONG).show(); }
		    	}
			executeBroadcastDeepskyObservation(deepskyObservationId);
			}
    	}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"DeepskyObservations: Exception 2 "+e.toString(),Toast.LENGTH_LONG).show(); }
	}
}
