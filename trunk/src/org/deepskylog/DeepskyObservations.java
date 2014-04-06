package org.deepskylog;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class DeepskyObservations {
	
	public static int deepskyObservationsMaxId;
	public static int deepskyObservationSeenMaxId;

	public static void init() {
		DeepskyObservations.deepskyObservationsMaxId=MainActivity.preferences.getInt("deepskyObservationsMaxId", 0);
		DeepskyObservations.deepskyObservationSeenMaxId=MainActivity.preferences.getInt("deepskyObservationSeenMaxId", 0);
		if(DeepskyObservations.deepskyObservationSeenMaxId==0) DeepskyObservations.deepskyObservationSeenMaxId=DeepskyObservations.deepskyObservationsMaxId;
	}
	
	public static void broadcastDeepskyObservationsMaxIdUpdate() { DeepskyObservations.executeBroadcastDeepskyObservationsMaxId(); }
    public static void broadcastDeepskyObservation(String deepskyObservationId) { DeepskyObservations.executeBroadcastDeepskyObservation(deepskyObservationId); }
    public static void broadcastDeepskyObservationsListFromIdToId(String fromId, String toId) { DeepskyObservations.executeBroadcastDeepskyObservationsListFromIdToId(fromId, toId); }
    public static void broadcastDeepskyObservationsListFromDateToDate(String fromDate, String toDate) { DeepskyObservations.executeBroadcastDeepskyObservationsListFromDateToDate(fromDate, toDate); }
    public static void broadcastDeepskyObservationsListDaysFromDateToDate(String fromDate, String toDate) { DeepskyObservations.executeBroadcastDeepskyObservationsListDaysFromDateToDate(fromDate, toDate); }

	// deepskyObservationsMaxId
    private static void executeBroadcastDeepskyObservationsMaxId() {
		MainActivity.mainActivity.setProgressBarIndeterminateVisibility(true);
		GetDslCommand.getCommandAndInvokeClassMethod("deepskyObservationMaxId", "", "org.deepskylog.DeepskyObservations", "deepskyObservationsMaxIdBroadcast");		
	}

	public static void deepskyObservationsMaxIdBroadcast(String resultRaw) {
		String tempMaxStr="0";
		try { tempMaxStr=Utils.getTagContent(resultRaw, "result"); }
		catch (Exception e) { Toast.makeText(MainActivity.mainActivity, e.toString(), Toast.LENGTH_LONG).show(); }
		finally { MainActivity.mainActivity.setProgressBarIndeterminateVisibility(false); }
		if(Utils.isNumeric(tempMaxStr)&&(Integer.valueOf(tempMaxStr)>deepskyObservationsMaxId)) {
	    	deepskyObservationsMaxId=Integer.valueOf(tempMaxStr);
			MainActivity.preferenceEditor.putInt("deepskyObservationsMaxId", deepskyObservationsMaxId).commit();
			LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationsmaxidchanged"));
		}
	}
	
	//deepskyObservation
    private static void executeBroadcastDeepskyObservation(String deepskydeepskyObservationId) {
    	Cursor cursor=DslDatabase.execSql("SELECT deepskyObservations.* FROM deepskyObservations WHERE deepskyObservationId=\""+deepskydeepskyObservationId+"\"");
    	if(cursor.moveToFirst()) {
    		String resultRaw=("<deepskyObservationId>"+cursor.getString(cursor.getColumnIndexOrThrow("deepskyObservationId"))+"</deepskyObservationId>" +
									   "<result>[ { \"deepskyObservationId\":\""+cursor.getString(cursor.getColumnIndexOrThrow("deepskyObservationId"))+"\", " +
									   "\"deepskyObjectName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("deepskyObjectName"))+"\", "+
					       			   "\"observerName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observerName"))+"\", "+
					       			   "\"deepskyObservationDate\":\""+cursor.getString(cursor.getColumnIndexOrThrow("deepskyObservationDate"))+"\","+
					       			   "\"instrumentName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("instrumentName"))+"\","+
					       			   "\"deepskyObservationDescription\":\""+cursor.getString(cursor.getColumnIndexOrThrow("deepskyObservationDescription")).replace("\"", "'")+"\" "+
					       			   "} ]>" +
					       			   "</result>");
			LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservation").putExtra("org.deepskylog.resultRAW", resultRaw));
		}
		else {
			MainActivity.mainActivity.setProgressBarIndeterminateVisibility(true);
		    GetDslCommand.getCommandAndInvokeClassMethod("deepskyObservationFromId", "&fromId="+deepskydeepskyObservationId, "org.deepskylog.DeepskyObservations", "storeDeepskyObservationToDbAndBroadcast");
		}
		cursor.close();
	}
	
	public static void storeDeepskyObservationToDbAndBroadcast(String observationRaw) {
		try {
    		String result=Utils.getTagContent(observationRaw,"result");
			if(result.startsWith("Unavailable url:")) {
				//TODO change second index of substring in next line
	    		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationnone").putExtra("org.deepskylog.resultRAW", "<deepskyObservationId>"+result.substring(result.indexOf("fromid=")+7)+"</deepskyObservationId>"));
	    	}
			else { 
				//TODO: change to deepskyObservationId
				String deepskyObservationId=Utils.getTagContent(observationRaw,"deepskyObservationId");
				if(result.equals("[\"No data\"]")) {
			    	DslDatabase.execSql("DELETE FROM deepskyObservations WHERE deepskyObservationId="+deepskyObservationId+";");
		            DslDatabase.execSql("INSERT INTO deepskyObservations (deepskyObservationId,deepskyObjectName,observerName,deepskyObservationDate,instrumentName,deepskyObservationDescription) "
		            		+ "VALUES("+deepskyObservationId+","
	            			   +"'No data',"
	            			   +"'No data',"
	            			   +"'No data'"
	            			   +"'No data'"
	            			   +"'No data'"
	            			   + ")");
		            DslDatabase.delete("deepskyObservations","deepskyObservationId='"+deepskyObservationId+"'",null);
			    	ContentValues initialValues = new ContentValues();
			    	initialValues.put("deepskyObservationId", deepskyObservationId);
		            initialValues.put("deepskyObjectName", "No data");
		            initialValues.put("observerName", "No data");
		            initialValues.put("deepskyObservationDate", "No data");
		            initialValues.put("instrumentName", "No data");
		            initialValues.put("deepskyObservationDescription", "No data");
		            DslDatabase.insert("deepskyObservations", initialValues);
		    	}
		    	else {
		    		try {
				    	JSONArray jsonArray = new JSONArray(result);
				    	if(jsonArray.length()>0) {
						    JSONObject jsonObject=jsonArray.getJSONObject(0);
				            DslDatabase.execSql("DELETE FROM deepskyObservations WHERE deepskyObservationId="+jsonObject.getString("deepskyObservationId")+";");
				            ContentValues initialValues = new ContentValues();
					    	initialValues.put("deepskyObservationId", jsonObject.getString("deepskyObservationId"));
				            initialValues.put("deepskyObjectName", jsonObject.getString("deepskyObjectName"));
				            initialValues.put("observerName", jsonObject.getString("observerName"));
				            initialValues.put("deepskyObservationDate", jsonObject.getString("deepskyObservationDate"));
				            initialValues.put("instrumentName", jsonObject.getString("instrumentName"));
				            initialValues.put("deepskyObservationDescription", jsonObject.getString("deepskyObservationDescription"));
				            DslDatabase.insert("deepskyObservations", initialValues);
				    	}
			        } 
		    		catch(Exception e) { Toast.makeText(MainActivity.mainActivity, "DeepskyObservations: Exception 1 "+e.toString(), Toast.LENGTH_LONG).show(); }
		    	}
			executeBroadcastDeepskyObservation(deepskyObservationId);
			}
    	}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"DeepskyObservations: Exception 2 "+e.toString(),Toast.LENGTH_LONG).show(); }
		finally { MainActivity.mainActivity.setProgressBarIndeterminateVisibility(false); }
	}
	

	
	
	
	
	private static void executeBroadcastDeepskyObservationsListFromIdToId(String fromId, String toId) {
		MainActivity.mainActivity.setProgressBarIndeterminateVisibility(true);
		GetDslCommand.getCommandAndInvokeClassMethod("deepskyObservationsListFromIdToId", "&fromId="+fromId+"&toId="+toId, "org.deepskylog.DeepskyObservations", "storeDeepskyObservationsListToDbAndBroadcast");		
	}
	
	private static void executeBroadcastDeepskyObservationsListFromDateToDate(String fromDate, String toDate) {
		MainActivity.mainActivity.setProgressBarIndeterminateVisibility(true);
		//Toast.makeText(MainActivity.mainActivity, "Fetching DSL List for proposed date: "+fromDate, Toast.LENGTH_SHORT).show();
		GetDslCommand.getCommandAndInvokeClassMethod("deepskyObservationsListFromDateToDate", "&fromDate="+fromDate+"&toDate="+toDate, "org.deepskylog.DeepskyObservations", "storeDeepskyObservationsListToDbAndBroadcast");		
	}
	
	public static void storeDeepskyObservationsListToDbAndBroadcast(String observationRaw) {
		//Toast.makeText(MainActivity.mainActivity, "Fetched DSL List for proposed date: "+observationRaw, Toast.LENGTH_LONG).show();
		try {
    		String result=Utils.getTagContent(observationRaw,"result");
			if(result.startsWith("Unavailable url:")) {
				//TODO change second index of substring in next line
	    		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationslistnone"));
	    	}
			else { 
				//TODO: change to deepskyObservationId
				if(result.equals("[\"No data\"]")) {

		    	}
		    	else {
		    		try {
		    			JSONArray jsonArray = new JSONArray(result);
				    	int listLength=jsonArray.length();
				    	for(int i=0;i<listLength;i++) {
						    JSONObject jsonObject=jsonArray.getJSONObject(i);
					    	DslDatabase.delete("deepskyObservationsList","deepskyObservationId='"+jsonObject.getString("deepskyObservationId")+"'",null);
					    	ContentValues initialValues = new ContentValues();
					    	initialValues.put("deepskyObservationId", jsonObject.getString("deepskyObservationId"));
				            initialValues.put("deepskyObjectName", jsonObject.getString("deepskyObjectName"));
				            initialValues.put("observerName", jsonObject.getString("observerName"));
				            initialValues.put("deepskyObservationDate", jsonObject.getString("deepskyObservationDate"));
				            DslDatabase.insert("deepskyObservationsList", initialValues);
				    	}
		    		} 
		    		catch(Exception e) { Toast.makeText(MainActivity.mainActivity, "DeepskyObservations: Exception 3 "+e.toString(), Toast.LENGTH_LONG).show(); }
		    	}
				LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationslist"));
			}
    	}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"DeepskyObservations: Exception 4 "+e.toString(),Toast.LENGTH_LONG).show();Toast.makeText(MainActivity.mainActivity,"DeepskyObservations: Exception 4 "+e.toString(),Toast.LENGTH_LONG).show();Toast.makeText(MainActivity.mainActivity,"DeepskyObservations: Exception 4 "+e.toString(),Toast.LENGTH_LONG).show();Toast.makeText(MainActivity.mainActivity,"DeepskyObservations: Exception 4 "+e.toString(),Toast.LENGTH_LONG).show();Toast.makeText(MainActivity.mainActivity,"DeepskyObservations: Exception 4 "+e.toString(),Toast.LENGTH_LONG).show(); }
		finally { MainActivity.mainActivity.setProgressBarIndeterminateVisibility(false); }
	}
	
	private static void executeBroadcastDeepskyObservationsListDaysFromDateToDate(String fromDate, String toDate) {
		MainActivity.mainActivity.setProgressBarIndeterminateVisibility(true);
		GetDslCommand.getCommandAndInvokeClassMethod("deepskyObservationsListDaysFromDateToDate", "&fromDate="+fromDate+"&toDate="+toDate, "org.deepskylog.DeepskyObservations", "storeDeepskyObservationsListDaysToDbAndBroadcast");		
	}
	
	public static void storeDeepskyObservationsListDaysToDbAndBroadcast(String observationRaw) {
		//Toast.makeText(MainActivity.mainActivity, observationRaw, Toast.LENGTH_LONG).show();
		try {
 			String result=Utils.getTagContent(observationRaw,"result");
 			if(result.startsWith("Unavailable url:")) {
				//TODO change second index of substring in next line
	    		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationslistnone"));
	    	}
			else { 
				//TODO: change to deepskyObservationId
				if(result.equals("[\"No data\"]")) {

		    	}
		    	else {
		    		try {
				    	JSONArray jsonArray = new JSONArray(result);
				    	int listLength=jsonArray.length();
				    	for(int i=0;i<listLength;i++) {
						    JSONObject jsonObject=jsonArray.getJSONObject(i);
						    Cursor temp=DslDatabase.execSql("SELECT deepskyObservationsListDate FROM deepskyObservationsListDays WHERE deepskyObservationsListDate="+jsonObject.getString("deepskyObservationsListDate"));
					    	if(temp.getCount()==0) { 
					    		DslDatabase.delete("deepskyObservationsListDays","deepskyObservationsListDate='"+jsonObject.getString("deepskyObservationsListDate")+"'",null);
						    	ContentValues initialValues = new ContentValues();
					    		initialValues.put("deepskyObservationsListDate", jsonObject.getString("deepskyObservationsListDate"));
					    		initialValues.put("deepskyObservationsListDateCount", jsonObject.getString("deepskyObservationsListDateCount"));
					    		DslDatabase.insert("deepskyObservationsListDays", initialValues);
					    	}
					    	temp.close();
				    	}
		    		} 
		    		catch(Exception e) { Toast.makeText(MainActivity.mainActivity, "DeepskyObservations: Exception 5 "+e.toString(), Toast.LENGTH_LONG).show(); }
		    	}
				LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationslistdays"));
			}
    	}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"DeepskyObservations: Exception 6 "+e.toString(),Toast.LENGTH_LONG).show(); }
		finally { MainActivity.mainActivity.setProgressBarIndeterminateVisibility(false); }
	}
	
	
}
