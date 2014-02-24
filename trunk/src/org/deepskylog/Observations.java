package org.deepskylog;

import org.deepskylog.GetDslCommand.GetDslCommandOnResult;

import android.database.Cursor;

public class Observations {

    public interface GetObservationOnResult {
        void onResultAvailable(String result);
    }

	public static void getObservation(final String observationid, final GetObservationOnResult getObservationOnResult) {
    	Cursor cursor=DslDatabase.getObservation(observationid);
		if(cursor.moveToFirst()) {
			getObservationOnResult.onResultAvailable("[ { \"observationid\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observationid"))+"\", " +
				      "\"objectname\":\""+cursor.getString(cursor.getColumnIndexOrThrow("objectname"))+"\", "+
				      "\"observername\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observername"))+"\", "+
				      "\"observationdescription\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observationdescription"))+"\", "+
				      "\"observationdate\":\""+cursor.getString(cursor.getColumnIndexOrThrow("observationdate"))+"\""+
					  "} ]"
			);
		}
		else {
			GetDslCommand.getCommand("observationsfromto", "&from="+observationid+"&to="+observationid, new GetDslCommandOnResult() { @Override public void onResultAvailable(String result) { DslDatabase.insertObservation(result,observationid); getObservationOnResult.onResultAvailable(result); } });
		}
	}
	
}
