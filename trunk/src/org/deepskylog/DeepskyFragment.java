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
import android.text.Html;
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
	private static TextView dsobstosee_textview;
	private static Integer deepskyObservationId;
	private static Integer deepskyObservationMaxId;
	private static Integer deepskyObservationSeenMaxId;
	
	private Integer displayMode;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastMaxObservationIdReceiver, new IntentFilter("org.deepskylog.broadcastmaxobservationid"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastObservationReceiver, new IntentFilter("org.deepskylog.broadcastobservation"));
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		deepskyFragmentView=inflater.inflate(R.layout.deepskyfragment, container, false);
		text1_textview=((TextView)deepskyFragmentView.findViewById(R.id.deepskyfragment_text1_textview_id));
		text1_textview.setText("Fetching observations...");
 		dsobstosee_textview=((TextView)deepskyFragmentView.findViewById(R.id.deepskyfragment_dsobstosee_textview_id));
		dsobstosee_textview.setText("");
		dsobstosee_textview.setOnTouchListener(new OnSwipeTouchListener(MainActivity.mainActivity) {
 		    public void onSwipeTop() { }
 		    public void onSwipeRight() { }
 		    public void onSwipeLeft() { getDeepskyObservation(deepskyObservationSeenMaxId+1); }
 		    public void onSwipeBottom() { }
		});
		text2_textview=((TextView)deepskyFragmentView.findViewById(R.id.deepskyfragment_text2_textview_id));
 		text2_textview.setText("Observations come here");
 		text2_textview.setOnTouchListener(new OnSwipeTouchListener(MainActivity.mainActivity) {
 		    public void onSwipeTop() { }
 		    public void onSwipeRight() { previousObservation(); }
 		    public void onSwipeLeft() { nextObservation(); }
 		    public void onSwipeBottom() { }
		});
 		deepskyObservationId=MainActivity.preferences.getInt("deepskyObservationId", 0);
 		deepskyObservationSeenMaxId=MainActivity.preferences.getInt("deepskyObservationSeenMaxId", 0);
 		deepskyObservationMaxId=MainActivity.preferences.getInt("deepskyObservationMaxId", 0);
 		displayMode=DISPLAY_MODE_NORMAL;
 		if(savedInstanceState!=null) {
 			savedState=savedInstanceState.getBundle("savedState");
		}
 		if(savedState!=null) {
 			text1_textview.setText(savedState.getString("text1_textview"));
 			text2_textview.setText(savedState.getString("text2_textview"));
 			deepskyObservationId=(savedState.getInt("deepskyObservationId"));
 			deepskyObservationSeenMaxId=(savedState.getInt("deepskyObservationSeenMaxId"));
 			displayMode=(savedState.getInt("displayMode"));
 		}
		Observations.broadcastDeepskyObservationsMaxId();
 		if(deepskyObservationId!=0) getDeepskyObservation(deepskyObservationId);
		savedState=null;
 		return deepskyFragmentView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    savedInstanceState.putBundle("savedState", saveState());
	}
	
	@Override
	public void onDestroy() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(broadcastMaxObservationIdReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(broadcastObservationReceiver);
		super.onDestroy();
	}

	private BroadcastReceiver broadcastMaxObservationIdReceiver=new BroadcastReceiver() {
	    @Override 
	    public void onReceive(Context context, Intent intent) { 
	    	deepskyObservationMaxId=Integer.valueOf(Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"),"result"));
			dsobstosee_textview.setText(((Integer)(deepskyObservationMaxId-deepskyObservationSeenMaxId)).toString()+" to see");
	    	if(deepskyObservationId==0) {
				deepskyObservationId=deepskyObservationMaxId;
			}
			getDeepskyObservation(deepskyObservationId);
		}
	};
	    
	private BroadcastReceiver broadcastObservationReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String result=intent.getStringExtra("org.deepskylog.resultRAW");
			//Toast.makeText(MainActivity.mainActivity, "DSF"+result, Toast.LENGTH_LONG).show();
			if(Utils.getTagContent(result, "observationid").equals(deepskyObservationId.toString())) { 
			    MainActivity.mainActivity.setProgressBarIndeterminateVisibility(true);
			    MainActivity.preferenceEditor.putInt("deepskyObservationId", deepskyObservationId);
			    if(deepskyObservationId>deepskyObservationSeenMaxId) MainActivity.preferenceEditor.putInt("deepskyObservationSeenMaxId", (deepskyObservationSeenMaxId=deepskyObservationId));
		    	MainActivity.preferenceEditor.commit();
		    	displayObservationDetails(Utils.getTagContent(result,"result"));
			}
		}
	};

	private Bundle saveState() {
        Bundle state = new Bundle();
        state.putString("text1_textview", text1_textview.getText().toString());
        state.putString("text2_textview", text2_textview.getText().toString());
        state.putInt("deepskyObservationId", deepskyObservationId);
        state.putInt("deepskyObservationSeenMaxId", deepskyObservationSeenMaxId);
        state.putInt("displayMode", displayMode);
        return state;
    }
    
    private void previousObservation() {
    	getDeepskyObservation(--deepskyObservationId);
    }
    
    private void nextObservation() {
    	if(deepskyObservationId<deepskyObservationMaxId) {
    		getDeepskyObservation(++deepskyObservationId);
    	}
    	else {
    		Toast.makeText(MainActivity.mainActivity, "No more observations", Toast.LENGTH_LONG).show();
    	}
    }
    
    private static void getDeepskyObservation(Integer theId) {
    	deepskyObservationId=theId;
    	text1_textview.setText("Fetching observation: "+theId.toString()+(deepskyObservationMaxId==0?"":" of "+deepskyObservationMaxId.toString()));
    	Observations.broadcastDeepskyObservation(theId.toString());
    }
    
    
    private static void displayObservationDetails(String result) {
    	if(result.equals("[\"No data\"]")||result.equals("[]")||result.equals("")) {
    		text2_textview.setText("Observation "+deepskyObservationId+" was deleted by the observer.");
    	}
    	else {
	    	try {
	    		JSONArray jsonArray = new JSONArray(result);
	    	    for(int i=0; i<jsonArray.length();i++) {
	    			JSONObject jsonObject=jsonArray.getJSONObject(i);
	    		   	if(jsonObject.getString("objectname").equals("No data")) {
	    	    		text2_textview.setText("Observation "+deepskyObservationId+" was deleted by the observer.");
	    	    	}
	    		   	else {
	    		   		text2_textview.setText(jsonObject.getString("observationdate"));
		    	    	text2_textview.setText(text2_textview.getText()+" - "+Html.fromHtml(jsonObject.getString("objectname")));
		    	    	text2_textview.setText(text2_textview.getText()+" - "+Html.fromHtml(jsonObject.getString("observername")));
		    	    	text2_textview.setText(text2_textview.getText()+" - "+Html.fromHtml(jsonObject.getString("observationid")));
		    	    	text2_textview.setText(text2_textview.getText()+"\n");
		    	    	text2_textview.setText(text2_textview.getText()+"\n");
		    	    	text2_textview.setText(text2_textview.getText()+" "+Html.fromHtml(jsonObject.getString("observationdescription")));
		    	    	text1_textview.setText("Observation: "+deepskyObservationId.toString()+(deepskyObservationMaxId==0?"":" of "+deepskyObservationMaxId.toString()));   	    	    
	    		   	}
	    		}
	        } catch (Exception e) {
	        	Toast.makeText(MainActivity.mainActivity, "DeepskyFragment Exception 1 "+e.toString(), Toast.LENGTH_LONG).show();
	        }
    	}
	    MainActivity.mainActivity.setProgressBarIndeterminateVisibility(false);
    }
}
