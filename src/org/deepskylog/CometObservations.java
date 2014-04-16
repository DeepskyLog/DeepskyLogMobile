package org.deepskylog;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class CometObservations {
	
	public static int cometObservationsMaxId;
	public static int cometObservationSeenMaxId;

	public static void init() {
		CometObservations.cometObservationsMaxId=MainActivity.preferences.getInt("cometObservationsMaxId", 0);
		CometObservations.cometObservationSeenMaxId=MainActivity.preferences.getInt("cometObservationSeenMaxId", 0);
		if(CometObservations.cometObservationSeenMaxId==0) CometObservations.cometObservationSeenMaxId=CometObservations.cometObservationsMaxId;
	}
	
	public static void broadcastCometObservationsMaxIdUpdate() { CometObservations.executeBroadcastCometObservationsMaxId(); }
    public static void broadcastCometObservation(String cometObservationId) { CometObservations.executeBroadcastCometObservation(cometObservationId); }
    public static void broadcastCometObservationsListFromIdToId(String fromId, String toId) { CometObservations.executeBroadcastCometObservationsListFromIdToId(fromId, toId); }
    public static void broadcastCometObservationsListFromDateToDate(String fromDate, String toDate) { CometObservations.executeBroadcastCometObservationsListFromDateToDate(fromDate, toDate); }
    public static void broadcastCometObservationsListDaysFromDateToDate(String fromDate, String toDate) { CometObservations.executeBroadcastCometObservationsListDaysFromDateToDate(fromDate, toDate); }

	// cometObservationsMaxId
    private static void executeBroadcastCometObservationsMaxId() {
		GetDslCommand.getCommandAndInvokeClassMethod("cometObservationMaxId", "", "org.deepskylog.CometObservations", "cometObservationsMaxIdBroadcast");		
	}

	public static void cometObservationsMaxIdBroadcast(String resultRaw) {
		String tempMaxStr="0";
		try { tempMaxStr=Utils.getTagContent(resultRaw, "result"); }
		catch (Exception e) { Toast.makeText(MainActivity.mainActivity, e.toString(), Toast.LENGTH_LONG).show(); }
		finally { MainActivity.mainActivity.setProgressBarIndeterminateVisibility(false); }
		if(Utils.isNumeric(tempMaxStr)&&(Integer.valueOf(tempMaxStr)>cometObservationsMaxId)) {
	    	cometObservationsMaxId=Integer.valueOf(tempMaxStr);
			MainActivity.preferenceEditor.putInt("cometObservationsMaxId", cometObservationsMaxId).commit();
			LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastcometobservationsmaxidchanged"));
		}
	}
	
	//cometObservation
    private static void executeBroadcastCometObservation(String cometcometObservationId) {
    	Cursor cursor=DslDatabase.execSql("SELECT cometObservations.* FROM cometObservations WHERE cometObservationId=\""+cometcometObservationId+"\"");
    	if(cursor.moveToFirst()) {
    		String resultRaw=("<cometObservationId>"+cursor.getString(cursor.getColumnIndexOrThrow("cometObservationId"))+"</cometObservationId>" +
									   "<result>[ { \"cometObservationId\":\""+cursor.getString(cursor.getColumnIndexOrThrow("cometObservationId"))+"\", " +
									   "\"cometObjectName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("cometObjectName"))+"\", "+
					       			   "\"observerName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observerName"))+"\", "+
					       			   "\"cometObservationDate\":\""+cursor.getString(cursor.getColumnIndexOrThrow("cometObservationDate"))+"\","+
					       			   "\"instrumentName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("instrumentName"))+"\","+
					       			   "\"cometObservationDescription\":\""+cursor.getString(cursor.getColumnIndexOrThrow("cometObservationDescription")).replace("\"", "'")+"\" "+
					       			   "} ]>" +
					       			   "</result>");
			LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastcometobservation").putExtra("org.deepskylog.resultRAW", resultRaw));
		}
		else {
			GetDslCommand.getCommandAndInvokeClassMethod("cometObservationFromId", "&fromId="+cometcometObservationId, "org.deepskylog.CometObservations", "storeCometObservationToDbAndBroadcast");
		}
		cursor.close();
	}
	
	public static void storeCometObservationToDbAndBroadcast(String observationRaw) {
		try {
    		String result=Utils.getTagContent(observationRaw,"result");
			if(result.startsWith("Unavailable url:")) {
				//TODO change second index of substring in next line
	    		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastcometobservationnone").putExtra("org.deepskylog.resultRAW", "<cometObservationId>"+result.substring(result.indexOf("fromid=")+7)+"</cometObservationId>"));
	    	}
			else { 
				//TODO: change to cometObservationId
				String cometObservationId=Utils.getTagContent(observationRaw,"cometObservationId");
				if(result.equals("[\"No data\"]")) {
			    	DslDatabase.delete("cometObservations","cometObservationId='"+cometObservationId+"'",null);
			    	ContentValues initialValues = new ContentValues();
			    	initialValues.put("cometObservationId", cometObservationId);
		            initialValues.put("cometObjectName", "No data");
		            initialValues.put("observerName", "No data");
		            initialValues.put("cometObservationDate", "No data");
		            initialValues.put("instrumentName", "No data");
		            initialValues.put("cometObservationDescription", "No data");
		            DslDatabase.insert("cometObservations", initialValues);
		    	}
		    	else {
		    		try {
				    	JSONArray jsonArray = new JSONArray(result);
				    	if(jsonArray.length()>0) {
						    JSONObject jsonObject=jsonArray.getJSONObject(0);
				            DslDatabase.execSql("DELETE FROM cometObservations WHERE cometObservationId="+jsonObject.getString("cometObservationId")+";");
				            ContentValues initialValues = new ContentValues();
					    	initialValues.put("cometObservationId", jsonObject.getString("cometObservationId"));
				            initialValues.put("cometObjectName", jsonObject.getString("cometObjectName"));
				            initialValues.put("observerName", jsonObject.getString("observerName"));
				            initialValues.put("cometObservationDate", jsonObject.getString("cometObservationDate"));
				            initialValues.put("instrumentName", jsonObject.getString("instrumentName"));
				            initialValues.put("cometObservationDescription", jsonObject.getString("cometObservationDescription"));
				            DslDatabase.insert("cometObservations", initialValues);
				    	}
			        } 
		    		catch(Exception e) { Toast.makeText(MainActivity.mainActivity, "CometObservations: Exception 1 "+e.toString(), Toast.LENGTH_LONG).show(); }
		    	}
			executeBroadcastCometObservation(cometObservationId);
			}
    	}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"CometObservations: Exception 2 "+e.toString(),Toast.LENGTH_LONG).show(); }
		finally { MainActivity.mainActivity.setProgressBarIndeterminateVisibility(false); }
	}
	

	
	
	
	
	private static void executeBroadcastCometObservationsListFromIdToId(String fromId, String toId) {
		GetDslCommand.getCommandAndInvokeClassMethod("cometObservationsListFromIdToId", "&fromId="+fromId+"&toId="+toId, "org.deepskylog.CometObservations", "storeCometObservationsListToDbAndBroadcast");		
	}
	
	private static void executeBroadcastCometObservationsListFromDateToDate(String fromDate, String toDate) {
		GetDslCommand.getCommandAndInvokeClassMethod("cometObservationsListFromDateToDate", "&fromDate="+fromDate+"&toDate="+toDate, "org.deepskylog.CometObservations", "storeCometObservationsListToDbAndBroadcast");		
	}
	
	public static void storeCometObservationsListToDbAndBroadcast(String observationRaw) {
		//Toast.makeText(MainActivity.mainActivity, "Fetched DSL List for proposed date: "+observationRaw, Toast.LENGTH_LONG).show();
		try {
    		String result=Utils.getTagContent(observationRaw,"result");
			if(result.startsWith("Unavailable url:")) {
				//TODO change second index of substring in next line
	    		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastcometobservationslistnone"));
	    	}
			else { 
				//TODO: change to cometObservationId
				if(result.equals("[\"No data\"]")) {

		    	}
		    	else {
		    		try {
		    			JSONArray jsonArray = new JSONArray(result);
				    	int listLength=jsonArray.length();
				    	for(int i=0;i<listLength;i++) {
						    JSONObject jsonObject=jsonArray.getJSONObject(i);
					    	DslDatabase.delete("cometObservationsList","cometObservationId='"+jsonObject.getString("cometObservationId")+"'",null);
					    	ContentValues initialValues = new ContentValues();
					    	initialValues.put("cometObservationId", jsonObject.getString("cometObservationId"));
				            initialValues.put("cometObjectName", jsonObject.getString("cometObjectName"));
				            initialValues.put("observerName", jsonObject.getString("observerName"));
				            initialValues.put("cometObservationDate", jsonObject.getString("cometObservationDate"));
				            DslDatabase.insert("cometObservationsList", initialValues);
				    	}
		    		} 
		    		catch(Exception e) { Toast.makeText(MainActivity.mainActivity, "CometObservations: Exception 3 "+e.toString(), Toast.LENGTH_LONG).show(); }
		    	}
				LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastcometobservationslist"));
			}
    	}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"CometObservations: Exception 4 "+e.toString(),Toast.LENGTH_LONG).show();Toast.makeText(MainActivity.mainActivity,"CometObservations: Exception 4 "+e.toString(),Toast.LENGTH_LONG).show();Toast.makeText(MainActivity.mainActivity,"CometObservations: Exception 4 "+e.toString(),Toast.LENGTH_LONG).show();Toast.makeText(MainActivity.mainActivity,"CometObservations: Exception 4 "+e.toString(),Toast.LENGTH_LONG).show();Toast.makeText(MainActivity.mainActivity,"CometObservations: Exception 4 "+e.toString(),Toast.LENGTH_LONG).show(); }
		finally { MainActivity.mainActivity.setProgressBarIndeterminateVisibility(false); }
	}
	
	private static void executeBroadcastCometObservationsListDaysFromDateToDate(String fromDate, String toDate) {
		GetDslCommand.getCommandAndInvokeClassMethod("cometObservationsListDaysFromDateToDate", "&fromDate="+fromDate+"&toDate="+toDate, "org.deepskylog.CometObservations", "storeCometObservationsListDaysToDbAndBroadcast");		
	}
	
	public static void storeCometObservationsListDaysToDbAndBroadcast(String observationRaw) {
		//Toast.makeText(MainActivity.mainActivity, observationRaw, Toast.LENGTH_LONG).show();
		try {
 			String result=Utils.getTagContent(observationRaw,"result");
 			if(result.startsWith("Unavailable url:")) {
				//TODO change second index of substring in next line
	    		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastcometobservationslistnone"));
	    	}
			else { 
				//TODO: change to cometObservationId
				if(result.equals("[\"No data\"]")) {

		    	}
		    	else {
		    		try {
				    	JSONArray jsonArray = new JSONArray(result);
				    	int listLength=jsonArray.length();
				    	for(int i=0;i<listLength;i++) {
						    JSONObject jsonObject=jsonArray.getJSONObject(i);
						    Cursor temp=DslDatabase.execSql("SELECT cometObservationsListDate FROM cometObservationsListDays WHERE cometObservationsListDate="+jsonObject.getString("cometObservationsListDate"));
					    	if(temp.getCount()==0) { 
					    		DslDatabase.delete("cometObservationsListDays","cometObservationsListDate='"+jsonObject.getString("cometObservationsListDate")+"'",null);
						    	ContentValues initialValues = new ContentValues();
					    		initialValues.put("cometObservationsListDate", jsonObject.getString("cometObservationsListDate"));
					    		initialValues.put("cometObservationsListDateCount", jsonObject.getString("cometObservationsListDateCount"));
					    		DslDatabase.insert("cometObservationsListDays", initialValues);
					    	}
					    	temp.close();
				    	}
		    		} 
		    		catch(Exception e) { Toast.makeText(MainActivity.mainActivity, "CometObservations: Exception 5 "+e.toString(), Toast.LENGTH_LONG).show(); }
		    	}
				LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastcometobservationslistdays"));
			}
    	}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"CometObservations: Exception 6 "+e.toString(),Toast.LENGTH_LONG).show(); }
		finally { MainActivity.mainActivity.setProgressBarIndeterminateVisibility(false); }
	}
	
	
}
