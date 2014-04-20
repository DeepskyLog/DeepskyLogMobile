package org.deepskylog;

import java.io.File;

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
    public static void broadcastDeepskyObservationDetails(String deepskyObservationId) { DeepskyObservations.executeBroadcastDeepskyObservationDetails(deepskyObservationId); }
    public static void broadcastDeepskyObservationDrawing(String deepskyObservationId) { DeepskyObservations.executeBroadcastDeepskyObservationDrawing(deepskyObservationId); }
    public static void broadcastDeepskyObservationsListFromIdToId(String fromId, String toId) { DeepskyObservations.executeBroadcastDeepskyObservationsListFromIdToId(fromId, toId); }
    public static void broadcastDeepskyObservationsListFromDateToDate(String fromDate, String toDate) { DeepskyObservations.executeBroadcastDeepskyObservationsListFromDateToDate(fromDate, toDate); }
    public static void broadcastDeepskyObservationsListDaysFromDateToDate(String fromDate, String toDate) { DeepskyObservations.executeBroadcastDeepskyObservationsListDaysFromDateToDate(fromDate, toDate); }

	// deepskyObservationsMaxId
    private static void executeBroadcastDeepskyObservationsMaxId() {
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
			GetDslCommand.getCommandAndInvokeClassMethod("deepskyObservationFromId", "&fromId="+deepskydeepskyObservationId, "org.deepskylog.DeepskyObservations", "storeDeepskyObservationToDbAndBroadcast");
		}
		cursor.close();
	}
	
    private static void executeBroadcastDeepskyObservationDetails(String deepskydeepskyObservationId) {
    	Cursor cursor=DslDatabase.execSql("SELECT deepskyObservations.* FROM deepskyObservations WHERE ((deepskyObservationId=\""+deepskydeepskyObservationId+"\") AND (hasDrawing!=''))");
    	if(cursor.moveToFirst()) {
    		String resultRaw=("<deepskyObservationId>"+cursor.getString(cursor.getColumnIndexOrThrow("deepskyObservationId"))+"</deepskyObservationId>" +
									   "<result>[ { \"deepskyObservationId\":\""+cursor.getString(cursor.getColumnIndexOrThrow("deepskyObservationId"))+"\", " +
									   "\"deepskyObjectName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("deepskyObjectName"))+"\", "+
					       			   "\"observerName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observerName"))+"\", "+
					       			   "\"deepskyObservationDate\":\""+cursor.getString(cursor.getColumnIndexOrThrow("deepskyObservationDate"))+"\","+
					       			   "\"instrumentName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("instrumentName"))+"\","+
					       			   "\"deepskyObservationDescription\":\""+cursor.getString(cursor.getColumnIndexOrThrow("deepskyObservationDescription")).replace("\"", "'")+"\", "+
					       			   "\"deepskyObservationTime\":\""+cursor.getString(cursor.getColumnIndexOrThrow("deepskyObservationTime")).replace("\"", "'")+"\", "+
					       			   "\"eyepieceName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("eyepieceName")).replace("\"", "'")+"\", "+
					       			   "\"filterName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("filterName")).replace("\"", "'")+"\", "+
					       			   "\"lensName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("lensName")).replace("\"", "'")+"\", "+
					       			   "\"seeing\":\""+cursor.getString(cursor.getColumnIndexOrThrow("seeing")).replace("\"", "'")+"\", "+
					       			   "\"limitingMagnitude\":\""+cursor.getString(cursor.getColumnIndexOrThrow("limitingMagnitude")).replace("\"", "'")+"\", "+
					       			   "\"visibility\":\""+cursor.getString(cursor.getColumnIndexOrThrow("visibility")).replace("\"", "'")+"\", "+
					       			   "\"SQM\":\""+cursor.getString(cursor.getColumnIndexOrThrow("SQM")).replace("\"", "'")+"\", "+
					       			   "\"hasDrawing\":\""+cursor.getString(cursor.getColumnIndexOrThrow("hasDrawing")).replace("\"", "'")+"\", "+
					       			   "\"locationName\":\""+cursor.getString(cursor.getColumnIndexOrThrow("locationName")).replace("\"", "'")+"\" "+
							      	   "} ]>" +
					       			   "</result>");
	    	//Toast.makeText(MainActivity.mainActivity, "WP details fetching locally "+resultRaw, Toast.LENGTH_LONG).show();
	    	LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationdetails").putExtra("org.deepskylog.resultRAW", resultRaw));
		}
		else {
			GetDslCommand.getCommandAndInvokeClassMethod("deepskyObservationDetailsFromId", "&fromId="+deepskydeepskyObservationId, "org.deepskylog.DeepskyObservations", "storeDeepskyObservationDetailsToDbAndBroadcast");
		}
		cursor.close();
	}
	
    private static void executeBroadcastDeepskyObservationDrawing(String deepskydeepskyObservationId) {
    	Cursor cursor=DslDatabase.execSql("SELECT deepskyObservations.hasDrawing FROM deepskyObservations WHERE ((deepskyObservationId=\""+deepskydeepskyObservationId+"\"))");
    	if(!cursor.moveToFirst()) {
			Toast.makeText(MainActivity.mainActivity, "Stopping to try to get drawing of not-loaded observation ", Toast.LENGTH_LONG).show();
    	}
		else {
			if(cursor.getString(cursor.getColumnIndexOrThrow("hasDrawing")).equals("0"))
				Toast.makeText(MainActivity.mainActivity, "No drawing for this observation", Toast.LENGTH_LONG).show();
			else if (cursor.getString(cursor.getColumnIndexOrThrow("hasDrawing")).equals("-1")) {
				//Toast.makeText(MainActivity.mainActivity, "Getting drawing for this observation from dsl", Toast.LENGTH_LONG).show();
				GetDslCommand.getDeepskyDrawingAndInvokeClassMethod(deepskydeepskyObservationId, "org.deepskylog.DeepskyObservations", "storeDeepskyObservationDrawingToDbAndBroadcast");				
			}
			else {
				File file = new File(MainActivity.storagePath+"/deepsky/images/"+String.valueOf(deepskydeepskyObservationId)+".jpg");
				if(file.exists()) {
			    	LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationdrawing").putExtra("org.deepskylog.resultRAW", "<deepskyObservationId>"+deepskydeepskyObservationId+"</deepskyObservationId>"));
				}
				else {
					//Toast.makeText(MainActivity.mainActivity, "Getting drawing for this observation from dsl", Toast.LENGTH_LONG).show();
					GetDslCommand.getDeepskyDrawingAndInvokeClassMethod(deepskydeepskyObservationId, "org.deepskylog.DeepskyObservations", "storeDeepskyObservationDrawingToDbAndBroadcast");				
				}
			}		
		}
		cursor.close();
	}

    
	public static void storeDeepskyObservationToDbAndBroadcast(String observationRaw) {
		//Toast.makeText(MainActivity.mainActivity, "Fetched DSL observation", Toast.LENGTH_LONG).show();
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
				            DslDatabase.delete("deepskyObservations","deepskyObservationId='"+deepskyObservationId+"'",null);
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
	
	
	public static void storeDeepskyObservationDetailsToDbAndBroadcast(String observationRaw) {
		try {
    		String result=Utils.getTagContent(observationRaw,"result");
			if(result.startsWith("Unavailable url:")) {
				//TODO change second index of substring in next line
	    		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationdetailsnone").putExtra("org.deepskylog.resultRAW", "<deepskyObservationId>"+result.substring(result.indexOf("fromid=")+7)+"</deepskyObservationId>"));
	    	}
			else { 
				//TODO: change to deepskyObservationId
				String deepskyObservationId=Utils.getTagContent(observationRaw,"deepskyObservationId");
				if(result.equals("[\"No data\"]")) {
			    	DslDatabase.delete("deepskyObservations","deepskyObservationId='"+deepskyObservationId+"'",null);
			    	ContentValues initialValues = new ContentValues();
			    	initialValues.put("deepskyObservationId", deepskyObservationId);
		            initialValues.put("deepskyObjectName", "No data");
		            initialValues.put("observerName", "No data");
		            initialValues.put("deepskyObservationDate", "No data");
		            initialValues.put("instrumentName", "No data");
		            initialValues.put("deepskyObservationDescription", "No data");
		            initialValues.put("deepskyObservationTime", "No data");
		            initialValues.put("eyepieceName", "No data");
		            initialValues.put("filterName", "No data");
		            initialValues.put("lensName", "No data");
		            initialValues.put("locationName", "No data");
		            initialValues.put("seeing", "No data");
		            initialValues.put("limitingMagnitude", "No data");
		            initialValues.put("visibility", "No data");
		            initialValues.put("SQM", "No data");
		            initialValues.put("hasDrawing", "No data");
		            DslDatabase.insert("deepskyObservations", initialValues);
		    	}
		    	else {
		    		try {
		    			//Toast.makeText(MainActivity.mainActivity, "WP details storing "+observationRaw, Toast.LENGTH_LONG).show();
		    			JSONArray jsonArray = new JSONArray(result);
				    	if(jsonArray.length()>0) {
						    JSONObject jsonObject=jsonArray.getJSONObject(0);
				            DslDatabase.delete("deepskyObservations","deepskyObservationId='"+deepskyObservationId+"'",null);
					    	ContentValues initialValues = new ContentValues();
					    	initialValues.put("deepskyObservationId", jsonObject.getString("deepskyObservationId"));
				            initialValues.put("deepskyObjectName", jsonObject.getString("deepskyObjectName"));
				            initialValues.put("observerName", jsonObject.getString("observerName"));
				            initialValues.put("deepskyObservationDate", jsonObject.getString("deepskyObservationDate"));
				            initialValues.put("instrumentName", jsonObject.getString("instrumentName"));
				            initialValues.put("deepskyObservationDescription", jsonObject.getString("deepskyObservationDescription"));
				            String tempTime;
				            tempTime=jsonObject.getString("deepskyObservationTime");
				            String theTime;
				            if(tempTime.equals("-9999"))
				            	theTime="-";
				            else if(tempTime.length()==4)
			            		theTime=tempTime.substring(0, 2)+":"+tempTime.substring(2);
			            	else if(tempTime.length()==3)
			            		theTime="0"+tempTime.substring(0, 1)+":"+tempTime.substring(1);
			            	else if(tempTime.length()==2)
			            		theTime="00"+":"+tempTime;
			            	else if(tempTime.length()==1)
			            		theTime="00:0"+tempTime;
			            	else
			            		theTime="";
				            initialValues.put("deepskyObservationTime", theTime);
				            initialValues.put("eyepieceName", jsonObject.getString("eyepieceName"));
				            initialValues.put("filterName", jsonObject.getString("filterName"));
				            initialValues.put("lensName", jsonObject.getString("lensName"));
				            initialValues.put("locationName", jsonObject.getString("locationName"));
				            initialValues.put("seeing", jsonObject.getString("seeing"));
				            initialValues.put("limitingMagnitude", jsonObject.getString("limitingMagnitude"));
				            initialValues.put("visibility", jsonObject.getString("visibility"));
				            initialValues.put("SQM", jsonObject.getString("SQM"));
				            initialValues.put("hasDrawing", jsonObject.getString("hasDrawing"));
				            DslDatabase.insert("deepskyObservations", initialValues);
				    	}
			        } 
		    		catch(Exception e) { Toast.makeText(MainActivity.mainActivity, "DeepskyObservations: Exception 8 "+e.toString(), Toast.LENGTH_LONG).show(); }
		    	}
			executeBroadcastDeepskyObservationDetails(deepskyObservationId);
			//Toast.makeText(MainActivity.mainActivity, "broadcast", Toast.LENGTH_SHORT).show();
			}
    	}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"DeepskyObservations: Exception 7 "+e.toString(),Toast.LENGTH_LONG).show(); }
		finally { MainActivity.mainActivity.setProgressBarIndeterminateVisibility(false); }
	}
	
	public static void storeDeepskyObservationDrawingToDbAndBroadcast(String observationRaw) {
		try {
    		String result=Utils.getTagContent(observationRaw,"result");
			if(result.startsWith("Download failure")) {
				//TODO change second index of substring in next line
	    		//LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationdetailsnone").putExtra("org.deepskylog.resultRAW", "<deepskyObservationId>"+result.substring(result.indexOf("fromid=")+7)+"</deepskyObservationId>"));
				Toast.makeText(MainActivity.mainActivity, "broadcast download failure "+result, Toast.LENGTH_SHORT).show();
			}
			else { 
				//TODO: change to deepskyObservationId
				String deepskyObservationId=Utils.getTagContent(observationRaw,"deepskyObservationId");
	    		try {
    			    //Toast.makeText(MainActivity.mainActivity, "WP drawing storing "+observationRaw, Toast.LENGTH_LONG).show();
			    	ContentValues initialValues = new ContentValues();
			    	initialValues.put("hasDrawing", deepskyObservationId+".jpg");
		            DslDatabase.update("deepskyObservations", initialValues,"deepskyObservationId="+deepskyObservationId,null);
			    	LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationdrawing").putExtra("org.deepskylog.resultRAW", "<deepskyObservationId>"+deepskyObservationId+"</deepskyObservationId>"));
			    	//Toast.makeText(MainActivity.mainActivity, "broadcast jpg available", Toast.LENGTH_SHORT).show();
		        } 
		    	catch(Exception e) { Toast.makeText(MainActivity.mainActivity, "DeepskyObservations: Exception 8 "+e.toString(), Toast.LENGTH_LONG).show(); }
		    }
    	}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"DeepskyObservations: Exception 7 "+e.toString(),Toast.LENGTH_LONG).show(); }
		finally { MainActivity.mainActivity.setProgressBarIndeterminateVisibility(false); }
	}

	
	
	private static void executeBroadcastDeepskyObservationsListFromIdToId(String fromId, String toId) {
		GetDslCommand.getCommandAndInvokeClassMethod("deepskyObservationsListFromIdToId", "&fromId="+fromId+"&toId="+toId, "org.deepskylog.DeepskyObservations", "storeDeepskyObservationsListToDbAndBroadcast");		
	}
	
	private static void executeBroadcastDeepskyObservationsListFromDateToDate(String fromDate, String toDate) {
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
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"DeepskyObservations: Exception 4 "+e.toString(),Toast.LENGTH_LONG).show(); }
		finally { MainActivity.mainActivity.setProgressBarIndeterminateVisibility(false); }
	}
	
	private static void executeBroadcastDeepskyObservationsListDaysFromDateToDate(String fromDate, String toDate) {
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
