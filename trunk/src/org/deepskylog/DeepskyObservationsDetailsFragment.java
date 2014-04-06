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

public class DeepskyObservationsDetailsFragment extends Fragment {
	
	private View deepskyObservationsDetailsFragmentView;
	private Bundle stateBundle=null;

	private TextView text1_textview;
	private TextView dsobstosee_textview;
	private TextView objecttext_textview;
	private TextView details_textview;
	
	private Integer deepskyObservationIdToGet;
	private Integer deepskyObservationIdDetails;
	
	private String deepskyObservationDate;
	
	private BroadcastReceiver broadcastDeepskyObservationNoneReceiver=new BroadcastReceiver() {  @Override  public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationNone(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservation(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationSelectedForDetailsReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationSelectedForDetails(context, intent); } };
		
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
		this.deepskyObservationsDetailsFragmentView=inflater.inflate(R.layout.deepskyobservationsdetailsfragment, container, false);
		this.text1_textview=((TextView)this.deepskyObservationsDetailsFragmentView.findViewById(R.id.deepskyobservationsdetailsfragment_text1_textview_id));
		this.text1_textview.setText("");
		this.dsobstosee_textview=((TextView)this.deepskyObservationsDetailsFragmentView.findViewById(R.id.deepskyobservationsdetailsfragment_dsobstosee_textview_id));
		this.dsobstosee_textview.setText("");
		this.objecttext_textview=((TextView)this.deepskyObservationsDetailsFragmentView.findViewById(R.id.deepskyobservationsdetailsfragment_objecttext_textview_id));
		this.objecttext_textview.setText("");
		this.dsobstosee_textview.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { dsobstoseeOnClick(); } });
		this.details_textview=((TextView)this.deepskyObservationsDetailsFragmentView.findViewById(R.id.deepskyobservationsdetailsfragment_details_textview_id));
		this.details_textview.setText("");
		this.details_textview.setOnTouchListener(new OnSwipeTouchListener(MainActivity.mainActivity) {
 		    public void onSwipeTop() { Toast.makeText(getActivity(), "Furtherdetails will come  here", Toast.LENGTH_LONG).show(); }
 		    public void onSwipeRight() { goRight(); }
 		    public void onSwipeLeft() { goLeft(); }
 		    public void onSwipeBottom() { switchToDeepskyObservationsList(); }
		});
 		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(this.stateBundle!=null) {
 			this.text1_textview.setText(stateBundle.getString("text1_textview"));
 			this.dsobstosee_textview.setText(stateBundle.getString("dsobstosee_textview"));
 			this.objecttext_textview.setText(stateBundle.getString("objecttext_textview"));
 			this.details_textview.setText(stateBundle.getString("details_textview"));
 			this.deepskyObservationIdToGet=(stateBundle.getInt("deepskyObservationIdToGet"));
 			this.deepskyObservationIdDetails=(stateBundle.getInt("deepskyObservationIdDetails"));
 			this.deepskyObservationDate=stateBundle.getString("deepskyObservationDate");
 		}
 		else {
 			this.deepskyObservationIdToGet=MainActivity.preferences.getInt("deepskyObservationIdToGet", 0);
 			this.deepskyObservationDate=(new SimpleDateFormat("yyyyMMdd")).format(new Date());
 	 		if(this.deepskyObservationIdToGet==0) this.deepskyObservationIdToGet=DeepskyObservations.deepskyObservationsMaxId;
 	 		this.deepskyObservationIdDetails=MainActivity.preferences.getInt("deepskyObservationIdDetails", 0);
 			if(this.deepskyObservationIdDetails==0) this.deepskyObservationIdDetails=DeepskyObservations.deepskyObservationsMaxId;			
 		}
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservation"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationNoneReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationnone"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationSelectedForDetailsReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationselectedfordetails"));
		DeepskyObservations.broadcastDeepskyObservationsMaxIdUpdate();
 		if(this.deepskyObservationIdToGet>0) this.getDeepskyObservation(this.deepskyObservationIdToGet);
 		this.stateBundle=null;
 		return this.deepskyObservationsDetailsFragmentView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", this.getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationNoneReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationSelectedForDetailsReceiver);
		super.onDestroyView();
	}

	private Bundle getStateBundle() {
        Bundle state = new Bundle();
        state.putString("text1_textview", this.text1_textview.getText().toString());
        state.putString("dsobstosee_textview", this.dsobstosee_textview.getText().toString());
        state.putString("objecttext_textview", this.objecttext_textview.getText().toString());
        state.putString("details_textview", this.details_textview.getText().toString());
        state.putInt("deepskyObservationIdDetails", this.deepskyObservationIdDetails);
        state.putInt("deepskyObservationIdToGet", this.deepskyObservationIdToGet);
        return state;
    }
	
	private void switchToDeepskyObservationsList() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationswitchtolist"));		
	}
	       	    
	private void dsobstoseeOnClick() {
    	this.getDeepskyObservation(DeepskyObservations.deepskyObservationSeenMaxId+1);
	}	
	
	private void onReceiveDeepskyObservationNone(Context context, Intent intent) {
		try {
			if(Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"), "deepskyObservationId").equals(this.deepskyObservationIdToGet.toString())) {
				this.deepskyObservationIdDetails=this.deepskyObservationIdToGet;
				this.displayDeepskyObservationDetails("Unavailable");
			}
		}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,e.toString(),Toast.LENGTH_LONG).show(); }
	}

	
	public void onReceiveDeepskyObservation(Context context, Intent intent) {
		try {
			if(Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"), "deepskyObservationId").equals(this.deepskyObservationIdToGet.toString())) { 
				this.deepskyObservationIdDetails=this.deepskyObservationIdToGet;
			    displayDeepskyObservationDetails(Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"),"result"));
			}
		}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,e.toString(),Toast.LENGTH_LONG).show(); }
	}
	
	public void onReceiveDeepskyObservationSelectedForDetails(Context context, Intent intent) {
		Integer theId=intent.getIntExtra("org.deepskylog.deepskyObservationId",DeepskyObservations.deepskyObservationSeenMaxId);
		getDeepskyObservation(theId);
	}

    private void getDeepskyObservation(Integer theId) {
    	this.deepskyObservationIdToGet=theId;
		MainActivity.preferenceEditor.putInt("deepskyObservationIdToGet", this.deepskyObservationIdToGet).commit();
  		if((DeepskyObservations.deepskyObservationsMaxId==0)||(this.deepskyObservationIdToGet<=DeepskyObservations.deepskyObservationsMaxId)) {
			this.text1_textview.setText(MainActivity.resources.getString(R.string.deepskyfragment_deepskyobservations_fetching)+this.deepskyObservationIdToGet.toString()+(DeepskyObservations.deepskyObservationsMaxId==0?"":" / "+Integer.valueOf(DeepskyObservations.deepskyObservationsMaxId)));   	    	    
     		DeepskyObservations.broadcastDeepskyObservation(theId.toString());
       	}
    	else {
    		DeepskyObservations.broadcastDeepskyObservationsMaxIdUpdate();
    		Toast.makeText(MainActivity.mainActivity, MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_checking1)+deepskyObservationIdToGet, Toast.LENGTH_LONG).show();
    	}
    }
    
	private void goRight() {
 		if(DeepskyObservationsFragment.sortMode.equals("By Date")) {
 			Cursor iCursor=DslDatabase.execSql("SELECT deepskyObservationId FROM deepskyObservationsList WHERE ((deepskyObservationDate<='"+this.deepskyObservationDate+"') AND (deepskyObservationId<"+this.deepskyObservationIdDetails+")) ORDER BY deepskyObservationDate DESC ,deepskyObservationId DESC ;");
 			iCursor.moveToFirst();
 			this.deepskyObservationIdToGet=iCursor.getInt(0);
 			getDeepskyObservation(this.deepskyObservationIdToGet);
 		}
 		else
 			getDeepskyObservation(--deepskyObservationIdToGet); 
	}
	
	@SuppressLint("SimpleDateFormat")
	private void goLeft() {
		if(DeepskyObservationsFragment.sortMode.equals("By Date")) {
 			Cursor iCursor=DslDatabase.execSql("SELECT deepskyObservationId FROM deepskyObservationsList WHERE ((deepskyObservationDate>='"+this.deepskyObservationDate+"') AND (deepskyObservationId>"+this.deepskyObservationIdDetails+")) ORDER BY deepskyObservationDate ASC ,deepskyObservationId ASC ;");
 			iCursor.moveToFirst();
 			this.deepskyObservationIdToGet=iCursor.getInt(0);
 			getDeepskyObservation(this.deepskyObservationIdToGet);
 		}
 		else
 			getDeepskyObservation(++deepskyObservationIdToGet);
	}

	private void displayDeepskyObservationDetails(String result) {
    	if(result.equals("Unavailable")) {
	   		this.objecttext_textview.setText("");
       		this.details_textview.setText(MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_observation)+this.deepskyObservationIdDetails+MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_observation_unavailable));
			//TODO: literal text
       		this.text1_textview.setText("Observation unavailable");   	    	    
    	}
    	else {
		    if(this.deepskyObservationIdDetails>DeepskyObservations.deepskyObservationSeenMaxId) {
		    	DeepskyObservations.deepskyObservationSeenMaxId=this.deepskyObservationIdDetails;
		    	MainActivity.preferenceEditor.putInt("deepskyObservationSeenMaxId", this.deepskyObservationIdDetails);
		    }
    		MainActivity.preferenceEditor.putInt("deepskyObservationIdDetails", this.deepskyObservationIdDetails).commit();
	    	if((DeepskyObservations.deepskyObservationsMaxId-DeepskyObservations.deepskyObservationSeenMaxId)>1) dsobstosee_textview.setText(((Integer)(DeepskyObservations.deepskyObservationsMaxId-DeepskyObservations.deepskyObservationSeenMaxId)).toString()+MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_to_see));
	    	if((DeepskyObservations.deepskyObservationsMaxId-DeepskyObservations.deepskyObservationSeenMaxId)==1) dsobstosee_textview.setText("1"+MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_to_see1));
	    	if((DeepskyObservations.deepskyObservationsMaxId-DeepskyObservations.deepskyObservationSeenMaxId)==0) dsobstosee_textview.setText(MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_to_see0));
	       	if(result.equals("[\"No data\"]")||result.equals("[]")||result.equals("")) {
		    	this.objecttext_textview.setText("");
		   		this.details_textview.setText(MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_observation)+deepskyObservationIdDetails+MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_observation_was_deleted));
				//TODO: literal text
				this.text1_textview.setText("Observation deleted");   	    	    
	    	}
	       	else {
	       		MainActivity.preferenceEditor.putInt("deepskyObservationIdDetails", this.deepskyObservationIdDetails).commit();
		    	try {
		    		JSONArray jsonArray = new JSONArray(result);
		    	    for(int i=0; i<jsonArray.length();i++) {
		    			JSONObject jsonObject=jsonArray.getJSONObject(i);
		    		   	if(jsonObject.getString("deepskyObjectName").equals("No data")) {
		    		   		this.objecttext_textview.setText("");
		    		   		this.details_textview.setText(MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_observation)+deepskyObservationIdDetails.toString()+MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_observation_was_deleted));
		    	    	}
		    		   	else {
		    		   		this.objecttext_textview.setText(jsonObject.getString("deepskyObjectName"));
		    		   		this.deepskyObservationDate=jsonObject.getString("deepskyObservationDate");
		    		   		this.details_textview.setText(this.deepskyObservationDate);
		    		   		this.details_textview.setText(this.details_textview.getText()+" - "+Html.fromHtml(jsonObject.getString("observerName")));
		    		   		this.details_textview.setText(this.details_textview.getText()+" - "+Html.fromHtml(jsonObject.getString("deepskyObservationId")));
		    		   		this.details_textview.setText(this.details_textview.getText()+"\n");
		    		   		this.details_textview.setText(this.details_textview.getText()+"\n");
		    		   		this.details_textview.setText(this.details_textview.getText().toString()+Html.fromHtml(jsonObject.getString("deepskyObservationDescription")));
		    		   	}
		    			this.text1_textview.setText(MainActivity.resources.getString(R.string.deepskyfragment_deepskyobservations_details)+this.deepskyObservationIdDetails.toString()+(DeepskyObservations.deepskyObservationsMaxId==0?"":" / "+Integer.valueOf(DeepskyObservations.deepskyObservationsMaxId)));   	    	    
		    		}
		        } 
		    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity, "DeepskyObservationsDetailsFragment Exception 1 "+e.toString(), Toast.LENGTH_LONG).show(); }
	       	}
    	}
    }    

}
