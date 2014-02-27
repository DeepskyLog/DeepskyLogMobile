package org.deepskylog;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.widget.Toast;

public class Observations {

 	public static void getObservationFromDbRaw(String observationid, String getObservationOnResultClass, String getObservationOnResultMethod) {
    	Cursor cursor=DslDatabase.getObservation(observationid);
		if(cursor.moveToFirst()) {
			Utils.onResultRaw("<onResultClass>"+getObservationOnResultClass+"</onResultClass>" +
					          "<onResultMethod>"+getObservationOnResultMethod+"</onResultMethod>" +
					          "<fromDb>true</fromDb>" +
					          "<result>[ { \"observationid\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observationid"))+"\", " +
					       			   "\"objectname\":\""+cursor.getString(cursor.getColumnIndexOrThrow("objectname"))+"\", "+
					       			   "\"observername\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observername"))+"\", "+
					       			   "\"observationdescription\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observationdescription"))+"\", "+
					       			   "\"observationdate\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observationdate"))+"\""+
					       			   "} ]>" +
					          "</result>"
					          );
		}
		else {
			Utils.onResultRaw("<onResultClass>"+getObservationOnResultClass+"</onResultClass>" +
			       	   	      "<onResultMethod>"+getObservationOnResultMethod+"</onResultMethod>" +
			       	   	      "<fromDb>false</fromDb>" +
			       	   	      "<result></result>"
			       	   	      );
		}
		cursor.close();
	}
	
 	public static void getObservationFromDSLRaw(String observationid, String getObservationOnResultClass, String getObservationOnResultMethod) {
    	GetDslCommand.getCommandRaw("observationsfromto", "&from="+observationid+"&to="+observationid, getObservationOnResultClass, getObservationOnResultMethod);
	}
	
	public static void storeObservationToDb(String observation, String observationId) {
    	if(observation.equals("[\"No data\"]")||observation.equals("[]")||observation.equals("")) {
	    	ContentValues initialValues = new ContentValues();
        	initialValues.put("observationid", observationId);
            initialValues.put("objectname", "No data");
            initialValues.put("observername", "No data");
            initialValues.put("observationdescription", "No data");
            initialValues.put("observationdate", "No data");
            DslDatabase.insert("observations", initialValues);
    	}
    	else {
    		try {
		    	JSONArray jsonArray = new JSONArray(observation);
		    	if(jsonArray.length()>0) {
				    JSONObject jsonObject=jsonArray.getJSONObject(0);
			    	ContentValues initialValues = new ContentValues();
		        	initialValues.put("observationid", jsonObject.getString("observationid"));
		            initialValues.put("objectname", jsonObject.getString("objectname"));
		            initialValues.put("observername", jsonObject.getString("observername"));
		            initialValues.put("observationdescription", jsonObject.getString("observationdescription"));
		            initialValues.put("observationdate", jsonObject.getString("observationdate"));
		            DslDatabase.insert("observations", initialValues);
		    	}
	        } catch (Exception e) {
	            Toast.makeText(MainActivity.mainActivity, "Observations Exception 1 "+e.toString(), Toast.LENGTH_LONG).show();
	        }
    	}
    }
	public static void getObservationsMaxIdRaw(String getObservationOnResultClass, String getObservationOnResultMethod) {
    	GetDslCommand.getCommandRaw("maxobservationid", "", getObservationOnResultClass, getObservationOnResultMethod);
	}
	
	public static void getObservationsMaxIdAndBroadcast(String observationsMaxIdBroadcastReceiver) {
    	GetDslCommand.getCommandAndBroadcast("maxobservationid", "", observationsMaxIdBroadcastReceiver);
	}

}
