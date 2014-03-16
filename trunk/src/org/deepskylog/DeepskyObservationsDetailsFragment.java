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

public class DeepskyObservationsDetailsFragment extends Fragment {

	private static final Integer DISPLAY_MODE_NORMAL=1;
	@SuppressWarnings("unused")
	private static final Integer DISPLAY_MODE_LIST  =2;
	
	private Bundle savedState=null;

	private static View deepskyObservationsDetailsFragmentView;
	private static TextView text1_textview;
	private static TextView text2_textview;
	private static TextView objecttext_textview;
	private static TextView dsobstosee_textview;
	private static Integer deepskyObservationIdToGet;
	private static Integer deepskyObservationIdDetails;
	
	private Integer displayMode;

	private BroadcastReceiver broadcastMaxDeepskyObservationIdReceiver=new BroadcastReceiver() { @Override  public void onReceive(Context context, Intent intent) { onReceiveMaxDeepskyObservationId(context, intent); } };
	private BroadcastReceiver broadcastNoDeepskyObservationReceiver=new BroadcastReceiver() {  @Override  public void onReceive(Context context, Intent intent) { onReceiveNoDeepskyObservation(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservation(context, intent); } };
		
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		deepskyObservationsDetailsFragmentView=inflater.inflate(R.layout.deepskyobservationsdetailsfragment, container, false);
		text1_textview=((TextView)deepskyObservationsDetailsFragmentView.findViewById(R.id.deepskyobservationsdetailsfragment_text1_textview_id));
		text1_textview.setText("Fetching observations...");
 		dsobstosee_textview=((TextView)deepskyObservationsDetailsFragmentView.findViewById(R.id.deepskyobservationsdetailsfragment_dsobstosee_textview_id));
		dsobstosee_textview.setText("");
		dsobstosee_textview.setClickable(true);
		dsobstosee_textview.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { getDeepskyObservation(DeepskyFragment.deepskyObservationSeenMaxId+1); } });
		text2_textview=((TextView)deepskyObservationsDetailsFragmentView.findViewById(R.id.deepskyobservationsdetailsfragment_text2_textview_id));
 		text2_textview.setText("Observations come here");
 		text2_textview.setOnTouchListener(new OnSwipeTouchListener(MainActivity.mainActivity) {
 		    public void onSwipeTop() { }
 		    public void onSwipeRight() { getDeepskyObservation(--deepskyObservationIdToGet); }
 		    public void onSwipeLeft() { getDeepskyObservation(++deepskyObservationIdToGet); }
 		    public void onSwipeBottom() { }
		});
 		objecttext_textview=((TextView)deepskyObservationsDetailsFragmentView.findViewById(R.id.deepskyobservationsdetailsfragment_objecttext_textview_id));
 		deepskyObservationIdToGet=MainActivity.preferences.getInt("deepskyObservationId", 0);
		deepskyObservationIdToGet=MainActivity.preferences.getInt("deepskyObservationIdToGet", 0);
		deepskyObservationIdDetails=MainActivity.preferences.getInt("deepskyObservationIdDetails", 0);
		DeepskyFragment.deepskyObservationSeenMaxId=MainActivity.preferences.getInt("deepskyObservationSeenMaxId", 0);
		DeepskyFragment.deepskyObservationMaxId=MainActivity.preferences.getInt("deepskyObservationMaxId", 0);
 		displayMode=DISPLAY_MODE_NORMAL;
 		if(savedInstanceState!=null) {
 			savedState=savedInstanceState.getBundle("savedState");
		}
 		if(savedState!=null) {
 			text1_textview.setText(savedState.getString("text1_textview"));
 			text2_textview.setText(savedState.getString("text2_textview"));
 			deepskyObservationIdToGet=(savedState.getInt("deepskyObservationIdToGet"));
 			deepskyObservationIdDetails=(savedState.getInt("deepskyObservationIdDetails"));
 			DeepskyFragment.deepskyObservationSeenMaxId=(savedState.getInt("deepskyObservationSeenMaxId"));
 			displayMode=(savedState.getInt("displayMode"));
 		}
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastMaxDeepskyObservationIdReceiver, new IntentFilter("org.deepskylog.broadcastmaxdeepskyobservationid"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastDeepskyObservationReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservation"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastNoDeepskyObservationReceiver, new IntentFilter("org.deepskylog.broadcastnodeepskyobservation"));
		DeepskyObservations.broadcastDeepskyObservationsMaxId();
 		if(deepskyObservationIdToGet!=0) getDeepskyObservation(deepskyObservationIdToGet);
		savedState=null;
 		return deepskyObservationsDetailsFragmentView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("savedState", saveState());
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(broadcastMaxDeepskyObservationIdReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(broadcastDeepskyObservationReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(broadcastNoDeepskyObservationReceiver);
		super.onDestroyView();
	}

	private Bundle saveState() {
        Bundle state = new Bundle();
        state.putString("text1_textview", text1_textview.getText().toString());
        state.putString("text2_textview", text2_textview.getText().toString());
        state.putInt("deepskyObservationIdDetails", deepskyObservationIdDetails);
        state.putInt("deepskyObservationIdToGet", deepskyObservationIdToGet);
        state.putInt("displayMode", displayMode);
        return state;
    }
	
	private void onReceiveMaxDeepskyObservationId(Context context, Intent intent) {
		String result=intent.getStringExtra("org.deepskylog.resultRAW");
		Integer tempMax=0;
		try { tempMax=Integer.valueOf(Utils.getTagContent(result, "result")); }
		catch (Exception e) { Toast.makeText(MainActivity.mainActivity, e.toString(), Toast.LENGTH_LONG).show(); }
		if(tempMax>DeepskyFragment.deepskyObservationMaxId) { 
	    	DeepskyFragment.deepskyObservationMaxId=tempMax;
	    	MainActivity.preferenceEditor.putInt("deepskyObservationSeenMaxId", DeepskyFragment.deepskyObservationMaxId);
	       	MainActivity.preferenceEditor.commit();
	    	dsobstosee_textview.setText(((Integer)(DeepskyFragment.deepskyObservationMaxId-DeepskyFragment.deepskyObservationSeenMaxId)).toString()+" to see");
		}
	}
        	    
	private void onReceiveNoDeepskyObservation(Context context, Intent intent) {
		try {
			String unavailableId=Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"), "deepskyObservationId");
			if(unavailableId.equals(deepskyObservationIdToGet.toString())) { 
    	    	text1_textview.setText("Details "+deepskyObservationIdDetails.toString()+(DeepskyFragment.deepskyObservationMaxId==0?"":" / "+DeepskyFragment.deepskyObservationMaxId.toString())+ " ("+unavailableId+" not available)");   	    	    
    		    MainActivity.mainActivity.setProgressBarIndeterminateVisibility(false);
			}
		}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,e.toString(),Toast.LENGTH_LONG).show(); }
	}

	
	public void onReceiveDeepskyObservation(Context context, Intent intent) {
		String result=intent.getStringExtra("org.deepskylog.resultRAW");
		try {
			if(Utils.getTagContent(result, "deepskyObservationId").equals(deepskyObservationIdToGet.toString())) { 
			    deepskyObservationIdDetails=deepskyObservationIdToGet;
				MainActivity.preferenceEditor.putInt("deepskyObservationIdToGet", deepskyObservationIdToGet);
			    if(deepskyObservationIdDetails>DeepskyFragment.deepskyObservationSeenMaxId) {
			    	DeepskyFragment.deepskyObservationSeenMaxId=deepskyObservationIdDetails;
			    	MainActivity.preferenceEditor.putInt("deepskyObservationSeenMaxId", deepskyObservationIdDetails);
			    	dsobstosee_textview.setText(((Integer)(DeepskyFragment.deepskyObservationMaxId-DeepskyFragment.deepskyObservationSeenMaxId)).toString()+" to see");
			    }
		    	MainActivity.preferenceEditor.commit();
		    	displayDeepskyObservationDetails(Utils.getTagContent(result,"result"));
			}
		}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,e.toString(),Toast.LENGTH_LONG).show(); }
	}

    private static void getDeepskyObservation(Integer theId) {
   		deepskyObservationIdToGet=theId;
		MainActivity.preferenceEditor.putInt("deepskyObservationIdToGet", deepskyObservationIdToGet);
		MainActivity.preferenceEditor.commit();
  		if(deepskyObservationIdToGet<=DeepskyFragment.deepskyObservationMaxId) {
     		text1_textview.setText("Fetching "+theId.toString()+(DeepskyFragment.deepskyObservationMaxId==0?"":" / "+DeepskyFragment.deepskyObservationMaxId.toString()));
     		DeepskyObservations.broadcastDeepskyObservation(theId.toString());
       	}
    	else {
    		DeepskyObservations.broadcastDeepskyObservationsMaxId();
    		Toast.makeText(MainActivity.mainActivity, "Checking for more observations to get "+deepskyObservationIdToGet, Toast.LENGTH_LONG).show();
    	}
    }
    
    private static void displayDeepskyObservationDetails(String result) {
    	if(result.equals("[\"No data\"]")||result.equals("[]")||result.equals("")) {
    		text2_textview.setText("Observation "+deepskyObservationIdDetails+" was deleted by the observer.");
    	}
    	else {
	    	try {
	    		JSONArray jsonArray = new JSONArray(result);
	    	    for(int i=0; i<jsonArray.length();i++) {
	    			JSONObject jsonObject=jsonArray.getJSONObject(i);
	    		   	if(jsonObject.getString("objectName").equals("No data")) {
	    	    		text2_textview.setText("Observation "+deepskyObservationIdDetails.toString()+" was deleted by the observer.");
	    	    	}
	    		   	else {
	    		   		objecttext_textview.setText(jsonObject.getString("objectName"));
	    		   		text2_textview.setText(jsonObject.getString("observationDate"));
		    	    	text2_textview.setText(text2_textview.getText()+" - "+Html.fromHtml(jsonObject.getString("observerName")));
		    	    	text2_textview.setText(text2_textview.getText()+" - "+Html.fromHtml(jsonObject.getString("deepskyObservationId")));
		    	    	text2_textview.setText(text2_textview.getText()+"\n");
		    	    	text2_textview.setText(text2_textview.getText()+"\n");
		    	    	text2_textview.setText(text2_textview.getText().toString()+Html.fromHtml(jsonObject.getString("observationDescription")));
		    	    	text1_textview.setText("Details "+deepskyObservationIdDetails.toString()+(DeepskyFragment.deepskyObservationMaxId==0?"":" / "+DeepskyFragment.deepskyObservationMaxId.toString()));   	    	    
	    		   	}
	    		}
	        } 
	    	catch (Exception e) {
	        	Toast.makeText(MainActivity.mainActivity, "DeepskyFragment Exception 1 "+e.toString(), Toast.LENGTH_LONG).show();
	        }
    	}
	    MainActivity.mainActivity.setProgressBarIndeterminateVisibility(false);
    }

}
