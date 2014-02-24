package org.deepskylog;

import org.deepskylog.GetDslCommand.GetDslCommandOnResult;

import android.database.Cursor;
import android.widget.Toast;

public class Observations {

    public interface GetObservationOnResult {
        void onResultAvailable(String result);
    }

    public static GetObservationOnResult getObservationOnResult;

	public static void getObservation(String observationid, GetObservationOnResult theObservationOnResult) {
    	getObservationOnResult=theObservationOnResult;
		Cursor cursor=DslDatabase.getObservation(observationid);
		if(cursor.moveToFirst()) {
			getObservationOnResult.onResultAvailable("[ { \"observationid\":\""+cursor.getString(0)+"\", " +
				      "\"objectname\":\""+cursor.getString(1)+"\", "+
				      "\"observername\":\""+cursor.getString(2)+"\", "+
				      "\"observationdescription\":\""+cursor.getString(3)+"\", "+
				      "\"observationdate\":\""+cursor.getString(4)+"\""+
					  "} ]"
			);
		}
		else {
			GetDslCommand.getCommand("observationsfromto", "&from="+observationid+"&to="+observationid, new GetDslCommandOnResult() { @Override public void onResultAvailable(String result) { DslDatabase.insertObservation(result); getObservationOnResult.onResultAvailable(result); } });
		}
	}
	
}
