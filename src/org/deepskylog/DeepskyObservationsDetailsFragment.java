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
	
	private Bundle stateBundle=null;

	private static View deepskyObservationsDetailsFragmentView;
	private static TextView objecttext_textview;
	private static TextView text2_textview;
	
	private static Integer deepskyObservationIdToGet;
	private static Integer deepskyObservationIdDetails;
	
	private BroadcastReceiver broadcastMaxDeepskyObservationIdReceiver=new BroadcastReceiver() { @Override  public void onReceive(Context context, Intent intent) { onReceiveMaxDeepskyObservationId(context, intent); } };
	private BroadcastReceiver broadcastNoDeepskyObservationReceiver=new BroadcastReceiver() {  @Override  public void onReceive(Context context, Intent intent) { onReceiveNoDeepskyObservation(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservation(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationSelectedForDetailsReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationSelectedForDetails(context, intent); } };
		
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		deepskyObservationsDetailsFragmentView=inflater.inflate(R.layout.deepskyobservationsdetailsfragment, container, false);
 		objecttext_textview=((TextView)deepskyObservationsDetailsFragmentView.findViewById(R.id.deepskyobservationsdetailsfragment_objecttext_textview_id));
		text2_textview=((TextView)deepskyObservationsDetailsFragmentView.findViewById(R.id.deepskyobservationsdetailsfragment_text2_textview_id));
 		text2_textview.setText("Observations come here");
 		text2_textview.setOnTouchListener(new OnSwipeTouchListener(MainActivity.mainActivity) {
 		    public void onSwipeTop() { }
 		    public void onSwipeRight() { getDeepskyObservation(--deepskyObservationIdToGet); }
 		    public void onSwipeLeft() { getDeepskyObservation(++deepskyObservationIdToGet); }
 		    public void onSwipeBottom() { }
		});
 		deepskyObservationIdToGet=MainActivity.preferences.getInt("deepskyObservationIdToGet", 0);
 		if(deepskyObservationIdToGet==0) deepskyObservationIdToGet=DeepskyFragment.deepskyObservationsMaxId;
		deepskyObservationIdDetails=MainActivity.preferences.getInt("deepskyObservationIdDetails", 0);
		if(deepskyObservationIdDetails==0) deepskyObservationIdDetails=DeepskyFragment.deepskyObservationsMaxId;
		DeepskyFragment.deepskyObservationSeenMaxId=MainActivity.preferences.getInt("deepskyObservationSeenMaxId", 0);
		DeepskyFragment.deepskyObservationsMaxId=MainActivity.preferences.getInt("deepskyObservationsMaxId", 0);
 		if(savedInstanceState!=null) {
 			stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(stateBundle!=null) {
 			text2_textview.setText(stateBundle.getString("text2_textview"));
 			deepskyObservationIdToGet=(stateBundle.getInt("deepskyObservationIdToGet"));
 			deepskyObservationIdDetails=(stateBundle.getInt("deepskyObservationIdDetails"));
 			DeepskyFragment.deepskyObservationSeenMaxId=(stateBundle.getInt("deepskyObservationSeenMaxId"));
 		}
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastMaxDeepskyObservationIdReceiver, new IntentFilter("org.deepskylog.broadcastmaxdeepskyobservationid"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastDeepskyObservationReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservation"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastNoDeepskyObservationReceiver, new IntentFilter("org.deepskylog.broadcastnodeepskyobservation"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastDeepskyObservationSelectedForDetailsReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationselectedfordetails"));
		DeepskyObservations.broadcastDeepskyObservationsMaxId();
 		if(deepskyObservationIdToGet>0) getDeepskyObservation(deepskyObservationIdToGet);
 		stateBundle=null;
 		return deepskyObservationsDetailsFragmentView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(broadcastMaxDeepskyObservationIdReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(broadcastDeepskyObservationReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(broadcastNoDeepskyObservationReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(broadcastDeepskyObservationSelectedForDetailsReceiver);
		super.onDestroyView();
	}

	private Bundle getStateBundle() {
        Bundle state = new Bundle();
        state.putString("text2_textview", text2_textview.getText().toString());
        state.putInt("deepskyObservationIdDetails", deepskyObservationIdDetails);
        state.putInt("deepskyObservationIdToGet", deepskyObservationIdToGet);
        return state;
    }
	
	private void onReceiveMaxDeepskyObservationId(Context context, Intent intent) {
		String result=intent.getStringExtra("org.deepskylog.resultRAW");
		Integer tempMax=0;
		try { 
			
			//TODO: unavailable url exception
			tempMax=Integer.valueOf(Utils.getTagContent(result, "result")); }
		catch (Exception e) { Toast.makeText(MainActivity.mainActivity, e.toString(), Toast.LENGTH_LONG).show(); }
		if(tempMax>DeepskyFragment.deepskyObservationsMaxId) { 
	    	DeepskyFragment.deepskyObservationsMaxId=tempMax;
	    	MainActivity.preferenceEditor.putInt("deepskyObservationSeenMaxId", DeepskyFragment.deepskyObservationsMaxId);
	       	MainActivity.preferenceEditor.commit();
		}
		if(deepskyObservationIdToGet==0) getDeepskyObservation(deepskyObservationIdToGet=DeepskyFragment.deepskyObservationsMaxId);
	}
        	    
	private void onReceiveNoDeepskyObservation(Context context, Intent intent) {
		try {
			String unavailableId=Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"), "deepskyObservationId");
			if(unavailableId.equals(deepskyObservationIdToGet.toString())) { 
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
			    }
		    	MainActivity.preferenceEditor.commit();
		    	displayDeepskyObservationDetails(Utils.getTagContent(result,"result"));
			}
		}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,e.toString(),Toast.LENGTH_LONG).show(); }
	}
	
	public void onReceiveDeepskyObservationSelectedForDetails(Context context, Intent intent) {
		Integer theId=intent.getIntExtra("org.deepskylog.deepskyobservationid",DeepskyFragment.deepskyObservationSeenMaxId);
		getDeepskyObservation(theId);
	}

    private static void getDeepskyObservation(Integer theId) {
   		deepskyObservationIdToGet=theId;
		MainActivity.preferenceEditor.putInt("deepskyObservationIdToGet", deepskyObservationIdToGet);
		MainActivity.preferenceEditor.commit();
  		if((DeepskyFragment.deepskyObservationsMaxId==0)||(deepskyObservationIdToGet<=DeepskyFragment.deepskyObservationsMaxId)) {
 			LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.deepskyobservationfetching").putExtra("org.deepskylog.deepskyobservationid", theId.toString()));
     		DeepskyObservations.broadcastDeepskyObservation(theId.toString());
       	}
    	else {
    		DeepskyObservations.broadcastDeepskyObservationsMaxId();
    		Toast.makeText(MainActivity.mainActivity, MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_checking1)+deepskyObservationIdToGet, Toast.LENGTH_LONG).show();
    	}
    }
    
    private static void displayDeepskyObservationDetails(String result) {
    	if(result.equals("[\"No data\"]")||result.equals("[]")||result.equals("")) {
    		text2_textview.setText(MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_observation)+deepskyObservationIdDetails+MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_observation_was_deleted));
    	}
    	else {
	    	try {
	    		JSONArray jsonArray = new JSONArray(result);
	    	    for(int i=0; i<jsonArray.length();i++) {
	    			JSONObject jsonObject=jsonArray.getJSONObject(i);
	    		   	if(jsonObject.getString("objectName").equals("No data")) {
	    	    		text2_textview.setText(MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_observation)+deepskyObservationIdDetails.toString()+MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_observation_was_deleted));
	    	    	}
	    		   	else {
	    		   		objecttext_textview.setText(jsonObject.getString("objectName"));
	    		   		text2_textview.setText(jsonObject.getString("observationDate"));
		    	    	text2_textview.setText(text2_textview.getText()+" - "+Html.fromHtml(jsonObject.getString("observerName")));
		    	    	text2_textview.setText(text2_textview.getText()+" - "+Html.fromHtml(jsonObject.getString("deepskyObservationId")));
		    	    	text2_textview.setText(text2_textview.getText()+"\n");
		    	    	text2_textview.setText(text2_textview.getText()+"\n");
		    	    	text2_textview.setText(text2_textview.getText().toString()+Html.fromHtml(jsonObject.getString("observationDescription")));
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
