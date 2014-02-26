package org.deepskylog;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class DeepskyFragment extends Fragment {

	private static final Integer DISPLAY_MODE_NORMAL=1;
	@SuppressWarnings("unused")
	private static final Integer DISPLAY_MODE_LIST  =2;
	
	private Bundle savedState = null;

	private static View deepskyFragmentView;
	private static TextView text1_textview;
	private static TextView text2_textview;
	
	private static Integer observationId;
	private static Integer observationMaxId;
	
	private Integer displayMode;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(observationsMaxIdBroadcastReceiver, new IntentFilter("observationsMaxIdBroadcastReceiver"));
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		deepskyFragmentView=inflater.inflate(R.layout.deepskyfragment, container, false);
		text1_textview=((TextView)deepskyFragmentView.findViewById(R.id.deepskyfragment_text1_textview_id));
		text2_textview=((TextView)deepskyFragmentView.findViewById(R.id.deepskyfragment_text2_textview_id));
 		text1_textview.setText("Fetching observations...");
 		text2_textview.setText("Observations come here");
 		text2_textview.setOnTouchListener(new OnSwipeTouchListener(MainActivity.mainActivity) {
 		    public void onSwipeTop() { }
 		    public void onSwipeRight() { previousObservation(); }
 		    public void onSwipeLeft() { nextObservation(); }
 		    public void onSwipeBottom() { }
		});
 		observationId=MainActivity.preferences.getInt("observationId", 0);
 		displayMode=DISPLAY_MODE_NORMAL;
    	if(savedInstanceState==null) {
	    }
		else {
			savedState=savedInstanceState.getBundle("savedState");
		}
 		if(savedState!=null) {
 			text1_textview.setText(savedState.getString("text1_textview"));
 			text2_textview.setText(savedState.getString("text2_textview"));
 			observationId=(savedState.getInt("observationId"));
 			displayMode=(savedState.getInt("displayMode"));
 		}
 		if(observationId==0)
 			getObservationsMaxIdAndObservation();
 		else {
 		    getObservationsMaxId();
 			getObservationsFromId();
 		}
 		savedState=null;
 		getObservationsMaxIdAndBroadcast();
 		return deepskyFragmentView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    savedInstanceState.putBundle("savedState", saveState());
	}
	
	@Override
	public void onDestroy() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(observationsMaxIdBroadcastReceiver);
		super.onDestroy();
	}
	
    private Bundle saveState() {
        Bundle state = new Bundle();
        state.putString("text1_textview", text1_textview.getText().toString());
        state.putString("text2_textview", text2_textview.getText().toString());
        state.putInt("observationId", observationId);
        state.putInt("displayMode", displayMode);
        return state;
    }
    
    private void previousObservation() {
    	observationId--;
		text1_textview.setText("Fetching observation: "+observationId.toString()+" of "+observationMaxId.toString());
    	getObservationsFromId();
    }
    
    private void nextObservation() {
    	if(observationId<observationMaxId) {
    		observationId++;
    		text1_textview.setText("Fetching observation: "+observationId.toString()+" of "+observationMaxId.toString());
        	getObservationsFromId();
    	}
    	else {
    		Toast.makeText(MainActivity.mainActivity, "No more observations", Toast.LENGTH_LONG).show();
    	}
    }
    
    public static void getObservationFromIdDsl(String result) {
    	String observation=Utils.getTagContent(result,"result");
    	Observations.storeObservationToDb(observation);
    	displayObservations(observation);
    }
    
    public static void getObservationFromIdDb(String result) {
    	if(Utils.getTagContent(result,"fromDb").equals("true")) {
    		displayObservations(Utils.getTagContent(result,"result"));
    	}
    	else {
        	Observations.getObservationFromDSLRaw(observationId.toString(), "org.deepskylog.DeepskyFragment", "getObservationFromIdDsl");
    	}
    }
    
    private static void getObservationsFromId() {
    	MainActivity.mainActivity.setProgressBarIndeterminateVisibility(true);
    	text1_textview.setText("Fetching observation: "+observationId.toString()+" of "+observationMaxId.toString());
    	MainActivity.preferenceEditor.putInt("observationId", observationId);
    	Observations.getObservationFromDbRaw(observationId.toString(), "org.deepskylog.DeepskyFragment", "getObservationFromIdDb");
    }
    
    public static void getObservationsMaxIdAndObservationOnResult1(String result) {
    	observationMaxId=Integer.valueOf(Utils.getTagContent(result,"result"));
    	observationId=observationMaxId;
    	getObservationsFromId();
    }
    
    private static void getObservationsMaxIdAndObservation() {
    	Observations.getObservationsMaxIdRaw("org.deepskylog.DeepskyFragment", "getObservationsMaxIdAndObservationOnResult1");
    }
    
    public static void getObservationsMaxIdOnResult1(String result) {
    	MainActivity.mainFragment.setText("ObservationsMaxId"+Utils.getTagContent(result,"result"));
    	observationMaxId=Integer.valueOf(Utils.getTagContent(result, "result"));
    }
    
	
	public BroadcastReceiver observationsMaxIdBroadcastReceiver=new BroadcastReceiver() {
		  @Override public void onReceive(Context context, Intent intent) { Toast.makeText(MainActivity.mainActivity, "Broadcast observationsMaxId: "+ Utils.getTagContent(intent.getStringExtra("resultRAW"),"result"), Toast.LENGTH_LONG).show(); }
	};
	
    private static void getObservationsMaxIdAndBroadcast() {
    	Observations.getObservationsMaxIdAndBroadcast("observationsMaxIdBroadcastReceiver");
    }
    
    private static void getObservationsMaxId() {
    	Observations.getObservationsMaxIdRaw("org.deepskylog.DeepskyFragment", "getObservationsMaxIdOnResult1");
    }
    
    private static void displayObservations(String result) {
    	try {
    		JSONArray jsonArray = new JSONArray(result);
    	    for(int i=0; i<jsonArray.length();i++) {
    			JSONObject jsonObject=jsonArray.getJSONObject(i);
    			text2_textview.setText(jsonObject.getString("observationdate"));
    	    	text2_textview.setText(text2_textview.getText()+" - "+jsonObject.getString("objectname"));
    	    	text2_textview.setText(text2_textview.getText()+" - "+jsonObject.getString("observername"));
    	    	text2_textview.setText(text2_textview.getText()+" - "+jsonObject.getString("observationid"));
    	    	text2_textview.setText(text2_textview.getText()+"\n");
    	    	text2_textview.setText(text2_textview.getText()+" "+jsonObject.getString("observationdescription"));
    	    	text1_textview.setText("Observation: "+observationId.toString()+" of "+observationMaxId.toString());   	    	    
    	    }
        } catch (Exception e) {
            Toast.makeText(MainActivity.mainActivity, "DeepskyFragment Exception 1 "+e.toString(), Toast.LENGTH_LONG).show();
        }
    	MainActivity.mainActivity.setProgressBarIndeterminateVisibility(false);
    }
}
