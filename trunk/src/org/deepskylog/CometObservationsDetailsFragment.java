package org.deepskylog;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CometObservationsDetailsFragment extends Fragment {
	private View cometObservationsDetailsFragmentView;
	private Bundle stateBundle=null;

	private TextView text1_textview;
	private TextView dsobstosee_textview;
	private TextView objecttext_textview;
	private TextView details_textview;
	
	private Integer cometObservationIdToGet=1;
	private Integer cometObservationIdDetails=1;
	
	private String cometObservationDate="";
	
	private BroadcastReceiver broadcastCometObservationNoneReceiver=new BroadcastReceiver() {  @Override  public void onReceive(Context context, Intent intent) { onReceiveCometObservationNone(context, intent); } };
	private BroadcastReceiver broadcastCometObservationReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveCometObservation(context, intent); } };
	private BroadcastReceiver broadcastCometObservationSelectedReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveCometObservationSelected(context, intent); } };
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}		
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.cometObservationsDetailsFragmentView=inflater.inflate(R.layout.cometobservationsdetailsfragment, container, false);
		this.text1_textview=((TextView)this.cometObservationsDetailsFragmentView.findViewById(R.id.cometobservationsdetailsfragment_text1_textview_id));
		this.text1_textview.setText("");
		this.dsobstosee_textview=((TextView)this.cometObservationsDetailsFragmentView.findViewById(R.id.cometobservationsdetailsfragment_dsobstosee_textview_id));
		this.dsobstosee_textview.setText("");
		this.dsobstosee_textview.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { dsobstoseeOnClick(); } });
		this.objecttext_textview=((TextView)this.cometObservationsDetailsFragmentView.findViewById(R.id.cometobservationsdetailsfragment_objecttext_textview_id));
		this.objecttext_textview.setText("");
		this.details_textview=((TextView)this.cometObservationsDetailsFragmentView.findViewById(R.id.cometobservationsdetailsfragment_details_textview_id));
		this.details_textview.setText("");
		this.details_textview.setOnTouchListener(new OnSwipeTouchListener(MainActivity.mainActivity) {
 		    public void onSwipeTop() { Toast.makeText(getActivity(), "Furtherdetails will come  here", Toast.LENGTH_LONG).show(); }
 		    public void onSwipeRight() { goRight(); }
 		    public void onSwipeLeft() { goLeft(); }
 		    public void onSwipeBottom() { switchToCometObservationsList(); }
		});
 		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(this.stateBundle!=null) {
 			this.text1_textview.setText(stateBundle.getString("text1_textview"));
 			this.dsobstosee_textview.setText(stateBundle.getString("dsobstosee_textview"));
 			this.objecttext_textview.setText(stateBundle.getString("objecttext_textview"));
 			this.details_textview.setText(stateBundle.getString("details_textview"));
 			this.cometObservationIdToGet=(stateBundle.getInt("cometObservationIdToGet"));
 			this.cometObservationIdDetails=(stateBundle.getInt("cometObservationIdDetails"));
 			this.cometObservationDate=stateBundle.getString("cometObservationDate");
 		}
 		else {
 			this.cometObservationIdToGet=MainActivity.preferences.getInt("cometObservationIdToGet", 0);
 			this.cometObservationDate=(new SimpleDateFormat("yyyyMMdd")).format(new Date());
 	 		this.cometObservationIdDetails=MainActivity.preferences.getInt("cometObservationIdDetails", 0);
 		}
		if(this.cometObservationIdDetails==0) this.cometObservationIdDetails=CometObservations.cometObservationsMaxId;			
 		if(this.cometObservationIdToGet==0) this.cometObservationIdToGet=CometObservations.cometObservationsMaxId;
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastCometObservationReceiver, new IntentFilter("org.deepskylog.broadcastcometobservation"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastCometObservationNoneReceiver, new IntentFilter("org.deepskylog.broadcastcometobservationnone"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastCometObservationSelectedReceiver, new IntentFilter("org.deepskylog.broadcastcometobservationselected"));
		CometObservations.broadcastCometObservationsMaxIdUpdate();
 		if(this.cometObservationIdToGet>0) this.getCometObservation(this.cometObservationIdToGet);
 		this.stateBundle=null;
 		return this.cometObservationsDetailsFragmentView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", this.getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastCometObservationReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastCometObservationNoneReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastCometObservationSelectedReceiver);
		super.onDestroyView();
	}

	private Bundle getStateBundle() {
        Bundle state = new Bundle();
        state.putString("text1_textview", (this.text1_textview!=null?this.text1_textview.getText().toString():""));
        state.putString("dsobstosee_textview", (this.dsobstosee_textview!=null?this.dsobstosee_textview.getText().toString():""));
        state.putString("objecttext_textview", (this.objecttext_textview!=null?this.objecttext_textview.getText().toString():""));
        state.putString("details_textview", (this.details_textview!=null?this.details_textview.getText().toString():""));
        state.putInt("cometObservationIdDetails", this.cometObservationIdDetails);
        state.putInt("cometObservationIdToGet", this.cometObservationIdToGet);
        state.putString("cometObservationDate", this.cometObservationDate);
        return state;
    }
	
	private void switchToCometObservationsList() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastcometobservationswitchtolist"));		
	}
	       	    
	private void dsobstoseeOnClick() {
    	this.getCometObservation(CometObservations.cometObservationSeenMaxId+1);
	}	
	
	private void onReceiveCometObservationNone(Context context, Intent intent) {
		try {
			if(Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"), "cometObservationId").equals(this.cometObservationIdToGet.toString())) {
				this.cometObservationIdDetails=this.cometObservationIdToGet;
				this.displayCometObservationDetails("Unavailable");
			}
		}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,e.toString(),Toast.LENGTH_LONG).show(); }
	}

	
	public void onReceiveCometObservation(Context context, Intent intent) {
		try {
			if(Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"), "cometObservationId").equals(this.cometObservationIdToGet.toString())) { 
				this.cometObservationIdDetails=this.cometObservationIdToGet;
			    displayCometObservationDetails(Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"),"result"));
			}
		}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,e.toString(),Toast.LENGTH_LONG).show(); }
	}
	
	public void onReceiveCometObservationSelected(Context context, Intent intent) {
		Integer theId=intent.getIntExtra("org.deepskylog.cometObservationId",CometObservations.cometObservationSeenMaxId);
		getCometObservation(theId);
	}

    private void getCometObservation(Integer theId) {
    	this.cometObservationIdToGet=theId;
		MainActivity.preferenceEditor.putInt("cometObservationIdToGet", this.cometObservationIdToGet).commit();
  		if((CometObservations.cometObservationsMaxId==0)||(this.cometObservationIdToGet<=CometObservations.cometObservationsMaxId)) {
			this.text1_textview.setText(MainActivity.resources.getString(R.string.cometfragment_cometobservations_fetching)+this.cometObservationIdToGet.toString()+(CometObservations.cometObservationsMaxId==0?"":" / "+Integer.valueOf(CometObservations.cometObservationsMaxId)));   	    	    
     		CometObservations.broadcastCometObservation(theId.toString());
       	}
    	else {
    		CometObservations.broadcastCometObservationsMaxIdUpdate();
    		Toast.makeText(MainActivity.mainActivity, MainActivity.resources.getString(R.string.cometobservationsdetailsfragment_checking1)+cometObservationIdToGet, Toast.LENGTH_LONG).show();
    	}
    }
    
	private void goRight() {
 		if(CometObservationsFragment.sortMode.equals("By Date")) {
 			Cursor iCursor=DslDatabase.execSql("SELECT cometObservationId FROM cometObservationsList WHERE ((cometObservationDate<='"+this.cometObservationDate+"') AND (cometObservationId<"+this.cometObservationIdDetails+")) ORDER BY cometObservationDate DESC ,cometObservationId DESC ;");
 			iCursor.moveToFirst();
 			this.cometObservationIdToGet=iCursor.getInt(0);
 			getCometObservation(this.cometObservationIdToGet);
 		}
 		else
 			getCometObservation(--cometObservationIdToGet); 
	}
	
	@SuppressLint("SimpleDateFormat")
	private void goLeft() {
		if(CometObservationsFragment.sortMode.equals("By Date")) {
 			Cursor iCursor=DslDatabase.execSql("SELECT cometObservationId FROM cometObservationsList WHERE ((cometObservationDate>='"+this.cometObservationDate+"') AND (cometObservationId>"+this.cometObservationIdDetails+")) ORDER BY cometObservationDate ASC ,cometObservationId ASC ;");
 			iCursor.moveToFirst();
 			this.cometObservationIdToGet=iCursor.getInt(0);
 			getCometObservation(this.cometObservationIdToGet);
 		}
 		else
 			getCometObservation(++cometObservationIdToGet);
	}

	private void displayCometObservationDetails(String result) {
    	if(result.equals("Unavailable")) {
	   		this.objecttext_textview.setText("");
       		this.details_textview.setText(MainActivity.resources.getString(R.string.cometobservationsdetailsfragment_observation)+this.cometObservationIdDetails+MainActivity.resources.getString(R.string.cometobservationsdetailsfragment_observation_unavailable));
			//TODO: literal text
       		this.text1_textview.setText("Observation unavailable");   	    	    
    	}
    	else {
		    if(this.cometObservationIdDetails>CometObservations.cometObservationSeenMaxId) {
		    	CometObservations.cometObservationSeenMaxId=this.cometObservationIdDetails;
		    	MainActivity.preferenceEditor.putInt("cometObservationSeenMaxId", this.cometObservationIdDetails);
		    }
    		MainActivity.preferenceEditor.putInt("cometObservationIdDetails", this.cometObservationIdDetails).commit();
	    	if((CometObservations.cometObservationsMaxId-CometObservations.cometObservationSeenMaxId)>1) dsobstosee_textview.setText(((Integer)(CometObservations.cometObservationsMaxId-CometObservations.cometObservationSeenMaxId)).toString()+MainActivity.resources.getString(R.string.cometobservationsdetailsfragment_to_see));
	    	if((CometObservations.cometObservationsMaxId-CometObservations.cometObservationSeenMaxId)==1) dsobstosee_textview.setText("1"+MainActivity.resources.getString(R.string.cometobservationsdetailsfragment_to_see1));
	    	if((CometObservations.cometObservationsMaxId-CometObservations.cometObservationSeenMaxId)==0) dsobstosee_textview.setText(MainActivity.resources.getString(R.string.cometobservationsdetailsfragment_to_see0));
	       	if(result.equals("[\"No data\"]")||result.equals("[]")||result.equals("")) {
		    	this.objecttext_textview.setText("");
		   		this.details_textview.setText(MainActivity.resources.getString(R.string.cometobservationsdetailsfragment_observation)+cometObservationIdDetails+MainActivity.resources.getString(R.string.cometobservationsdetailsfragment_observation_was_deleted));
				//TODO: literal text
				this.text1_textview.setText("Observation deleted");   	    	    
	    	}
	       	else {
	       		MainActivity.preferenceEditor.putInt("cometObservationIdDetails", this.cometObservationIdDetails).commit();
		    	try {
		    		JSONArray jsonArray = new JSONArray(result);
		    	    for(int i=0; i<jsonArray.length();i++) {
		    			JSONObject jsonObject=jsonArray.getJSONObject(i);
		    		   	if(jsonObject.getString("cometObjectName").equals("No data")) {
		    		   		this.objecttext_textview.setText("");
		    		   		this.details_textview.setText(MainActivity.resources.getString(R.string.cometobservationsdetailsfragment_observation)+cometObservationIdDetails.toString()+MainActivity.resources.getString(R.string.cometobservationsdetailsfragment_observation_was_deleted));
		    	    	}
		    		   	else {
		    		   		this.objecttext_textview.setText(jsonObject.getString("cometObjectName"));
		    		   		this.cometObservationDate=jsonObject.getString("cometObservationDate");
		    		   		this.details_textview.setText(this.cometObservationDate);
		    		   		this.details_textview.setText(this.details_textview.getText()+" - "+Html.fromHtml(jsonObject.getString("observerName")));
		    		   		this.details_textview.setText(this.details_textview.getText()+" - "+Html.fromHtml(jsonObject.getString("cometObservationId")));
		    		   		this.details_textview.setText(this.details_textview.getText()+"\n");
		    		   		this.details_textview.setText(this.details_textview.getText()+"\n");
		    		   		this.details_textview.setText(this.details_textview.getText().toString()+Html.fromHtml(jsonObject.getString("cometObservationDescription")));
		    		   	}
		    			this.text1_textview.setText(MainActivity.resources.getString(R.string.cometfragment_cometobservations_details)+this.cometObservationIdDetails.toString()+(CometObservations.cometObservationsMaxId==0?"":" / "+Integer.valueOf(CometObservations.cometObservationsMaxId)));   	    	    
		    		}
		        } 
		    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity, "CometObservationsDetailsFragment Exception 1 "+e.toString(), Toast.LENGTH_LONG).show(); }
	       	}
    	}
    }    

}
