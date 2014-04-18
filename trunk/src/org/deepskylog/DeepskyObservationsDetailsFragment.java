package org.deepskylog;

import java.io.File;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DeepskyObservationsDetailsFragment extends Fragment {
	
	private View deepskyObservationsDetailsFragmentView;
	private Bundle stateBundle=null;

	private TextView text1_textview;
	private TextView dsobstosee_textview;
	private TextView objecttext_textview;
	private TextView details_textview;
	private ImageView drawing_imageview;
	
	private Integer deepskyObservationIdToGet;
	private Integer deepskyObservationIdDetails;
	
	private String deepskyObservationDate;
	private Boolean inDetails;
	
	private String[] seeings;
	private String[] visibilities;
	
	private BroadcastReceiver broadcastDeepskyObservationNoneReceiver=new BroadcastReceiver() {  @Override  public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationNone(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservation(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationDetailsReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationDetails(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationDrawingReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationDrawing(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationSelectedReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationSelected(context, intent); } };
		
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
 		    public void onSwipeTop() { getDeepskyObservationDetails(deepskyObservationIdDetails); }
 		    public void onSwipeRight() { goRight(); }
 		    public void onSwipeLeft() { goLeft(); }
 		    public void onSwipeBottom() { goDown(); }
		});
		this.drawing_imageview=((ImageView)this.deepskyObservationsDetailsFragmentView.findViewById(R.id.deepskyobservationsdetailsfragment_drawing_imageview));
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
 			this.inDetails=stateBundle.getBoolean("inDetails");
 		}
 		else {
 			this.deepskyObservationIdToGet=MainActivity.preferences.getInt("deepskyObservationIdToGet", 0);
 			this.deepskyObservationDate=(new SimpleDateFormat("yyyyMMdd")).format(new Date());
 	 		this.deepskyObservationIdDetails=MainActivity.preferences.getInt("deepskyObservationIdDetails", 0);
 	 		this.inDetails=false;
 		}
		this.seeings=MainActivity.resources.getStringArray(R.array.seeings);
		this.visibilities=MainActivity.resources.getStringArray(R.array.visibilities);
 		if(this.deepskyObservationIdDetails==0) this.deepskyObservationIdDetails=DeepskyObservations.deepskyObservationsMaxId;			
 		if(this.deepskyObservationIdToGet==0) this.deepskyObservationIdToGet=DeepskyObservations.deepskyObservationsMaxId;
 		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservation"));
 		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationDetailsReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationdetails"));
 		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationDrawingReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationdrawing"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationNoneReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationnone"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationSelectedReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationselected"));
		DeepskyObservations.broadcastDeepskyObservationsMaxIdUpdate();
 		if(this.deepskyObservationIdToGet>0) 
 			if(inDetails)
 				this.getDeepskyObservationDetails(this.deepskyObservationIdToGet);
 			else
 				this.getDeepskyObservation(this.deepskyObservationIdToGet);
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
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationDetailsReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationDrawingReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationNoneReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationSelectedReceiver);
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
        state.putBoolean("inDetails", this.inDetails);
        return state;
    }
	
	private void dsobstoseeOnClick() {
    	this.getDeepskyObservation(DeepskyObservations.deepskyObservationSeenMaxId+1);
	}	
	
	private void onReceiveDeepskyObservationNone(Context context, Intent intent) {
		try {
			if(Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"), "deepskyObservationId").equals(this.deepskyObservationIdToGet.toString())) {
				this.deepskyObservationIdDetails=this.deepskyObservationIdToGet;
				this.displayDeepskyObservation("Unavailable");
			}
		}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,e.toString(),Toast.LENGTH_LONG).show(); }
	}

	
	public void onReceiveDeepskyObservation(Context context, Intent intent) {
		try {
			if(Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"), "deepskyObservationId").equals(this.deepskyObservationIdToGet.toString())) { 
				this.deepskyObservationIdDetails=this.deepskyObservationIdToGet;
			    displayDeepskyObservation(Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"),"result"));
			}
		}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,e.toString(),Toast.LENGTH_LONG).show(); }
	}
	
	public void onReceiveDeepskyObservationSelected(Context context, Intent intent) {
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
    
    public void onReceiveDeepskyObservationDetails(Context context, Intent intent) {
		try {
			if(Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"), "deepskyObservationId").equals(this.deepskyObservationIdToGet.toString())) { 
				this.deepskyObservationIdDetails=this.deepskyObservationIdToGet;
			    displayDeepskyObservationDetails(Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"),"result"));
			}
		}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,e.toString(),Toast.LENGTH_LONG).show(); }
	}
	
    public void onReceiveDeepskyObservationDrawing(Context context, Intent intent) {
		//Toast.makeText(MainActivity.mainActivity, "Broadcast drawing received", Toast.LENGTH_LONG).show();
    	try {
			if(Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"), "deepskyObservationId").equals(this.deepskyObservationIdToGet.toString())) { 
				BitmapFactory.Options options = new BitmapFactory.Options();
		        options.inSampleSize = 2;
		        Bitmap bm = BitmapFactory.decodeFile(MainActivity.storagePath+"/deepsky/images/"+String.valueOf(this.deepskyObservationIdToGet)+".jpg", options);
		        this.drawing_imageview.setImageBitmap(bm); 
			}
		}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,e.toString(),Toast.LENGTH_LONG).show(); }
	}
	
//TODO to Comet
    private void getDeepskyObservationDetails(Integer theId) {
    	this.deepskyObservationIdToGet=theId;
		this.text1_textview.setText("Fetching all details.");   	    	    
   		DeepskyObservations.broadcastDeepskyObservationDetails(theId.toString());
    }
    
	private void goDown() {
		if(this.inDetails)
			getDeepskyObservation(this.deepskyObservationIdDetails);
		else
			LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationswitchtolist"));		
	}
		       	    
	private void goRight() {
 		if(DeepskyObservationsFragment.sortMode.equals("By Date")) {
 			//Toast.makeText(MainActivity.mainActivity, "WP 1 "+"SELECT deepskyObservationId FROM deepskyObservationsList WHERE ((deepskyObservationDate<='"+this.deepskyObservationDate+"') AND (deepskyObservationId<"+this.deepskyObservationIdDetails+")) ORDER BY deepskyObservationDate DESC,deepskyObservationId DESC ;", Toast.LENGTH_LONG).show();
 			Cursor iCursor=DslDatabase.execSql("SELECT deepskyObservationId FROM deepskyObservationsList WHERE ((deepskyObservationDate<='"+this.deepskyObservationDate+"') AND (deepskyObservationId<"+this.deepskyObservationIdDetails+")) ORDER BY deepskyObservationDate DESC,deepskyObservationId DESC ;");
 			iCursor.moveToFirst();
 			this.deepskyObservationIdToGet=iCursor.getInt(0);
 			if(inDetails)
 				this.getDeepskyObservationDetails(this.deepskyObservationIdToGet);
 			else
 				this.getDeepskyObservation(this.deepskyObservationIdToGet);
 		}
 		else
 			if(inDetails)
 				this.getDeepskyObservationDetails(--this.deepskyObservationIdToGet);
 			else
 				this.getDeepskyObservation(--this.deepskyObservationIdToGet);
	}
	
	@SuppressLint("SimpleDateFormat")
	private void goLeft() {
		if(DeepskyObservationsFragment.sortMode.equals("By Date")) {
			//Toast.makeText(MainActivity.mainActivity, "WP 1 "+"SELECT deepskyObservationId FROM deepskyObservationsList WHERE ((deepskyObservationDate>='"+this.deepskyObservationDate+"') AND (deepskyObservationId>"+this.deepskyObservationIdDetails+")) ORDER BY deepskyObservationDate ASC,deepskyObservationId ASC ;", Toast.LENGTH_LONG).show();
 			Cursor iCursor=DslDatabase.execSql("SELECT deepskyObservationId FROM deepskyObservationsList WHERE ((deepskyObservationDate>='"+this.deepskyObservationDate+"') AND (deepskyObservationId>"+this.deepskyObservationIdDetails+")) ORDER BY deepskyObservationDate ASC,deepskyObservationId ASC ;");
 			iCursor.moveToFirst();
 			this.deepskyObservationIdToGet=iCursor.getInt(0);
 			if(inDetails)
 				this.getDeepskyObservationDetails(this.deepskyObservationIdToGet);
 			else
 				this.getDeepskyObservation(this.deepskyObservationIdToGet);
 		}
 		else
 			if(inDetails)
 				this.getDeepskyObservationDetails(++this.deepskyObservationIdToGet);
 			else
 				this.getDeepskyObservation(++this.deepskyObservationIdToGet);
	}

	private void displayDeepskyObservation(String result) {
		this.inDetails=false;
		this.drawing_imageview.setImageBitmap(null);
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
		    		   		this.details_textview.setText(Utils.toUiDate(this.deepskyObservationDate));
		    		   		this.details_textview.setText(this.details_textview.getText()+" - "+Html.fromHtml(jsonObject.getString("observerName")));
		    		   		this.details_textview.setText(this.details_textview.getText()+" - "+Html.fromHtml(jsonObject.getString("deepskyObservationId")));
		    		   		this.details_textview.setText(this.details_textview.getText()+"\n");
		    		   		this.details_textview.setText(this.details_textview.getText()+"\n");
		    		   		this.details_textview.setText(this.details_textview.getText().toString()+Html.fromHtml(jsonObject.getString("deepskyObservationDescription")));
		    		   	}
		    			this.text1_textview.setText(MainActivity.resources.getString(R.string.deepskyfragment_deepskyobservations_observation)+this.deepskyObservationIdDetails.toString()+(DeepskyObservations.deepskyObservationsMaxId==0?"":" / "+Integer.valueOf(DeepskyObservations.deepskyObservationsMaxId)));   	    	    
		    		}
		        } 
		    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity, "DeepskyObservationsDetailsFragment Exception 1 "+e.toString(), Toast.LENGTH_LONG).show(); }
	       	}
    	}
    }    

	private void displayDeepskyObservationDetails(String result) {
		this.inDetails=true;
		this.drawing_imageview.setImageBitmap(null);
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
		    		   		this.details_textview.setText(Utils.toUiDate(this.deepskyObservationDate)+(jsonObject.getString("deepskyObservationTime").equals("-")?"":" "+jsonObject.getString("deepskyObservationTime")));
		    		   		this.details_textview.setText(this.details_textview.getText()+" - "+Html.fromHtml(jsonObject.getString("observerName")));
		    		   		this.details_textview.setText(this.details_textview.getText()+" - "+Html.fromHtml(jsonObject.getString("deepskyObservationId")));
		    		   		this.details_textview.setText(this.details_textview.getText()+"\n");
		    		   		this.details_textview.setText(this.details_textview.getText()+"\n");
		    		   		this.details_textview.setText(this.details_textview.getText().toString()+Html.fromHtml(jsonObject.getString("locationName"))+
		    		   																				(jsonObject.getString("seeing").equals("0")?"":" / "+"Seeing: "+seeings[Integer.valueOf(jsonObject.getString("seeing"))])+
		    		   																				(jsonObject.getString("limitingMagnitude").equals("")?"":" /"+" LimMag: "+Html.fromHtml(jsonObject.getString("limitingMagnitude")))+
		    		   																				(jsonObject.getString("SQM").equals("-1")?"":" / "+"SQM: "+Html.fromHtml(jsonObject.getString("SQM"))));
		    		   		this.details_textview.setText(this.details_textview.getText()+"\n");
		    		   		this.details_textview.setText(this.details_textview.getText()+"\n");
		    		   		this.details_textview.setText(this.details_textview.getText().toString()+Html.fromHtml(jsonObject.getString("instrumentName"))+
																									(jsonObject.getString("eyepieceName").equals("")?"":" / "+Html.fromHtml(jsonObject.getString("eyepieceName")))+
																									(jsonObject.getString("filterName").equals("")?"":" / "+Html.fromHtml(jsonObject.getString("filterName")))+
																									(jsonObject.getString("lensName").equals("")?"":" / "+Html.fromHtml(jsonObject.getString("lensName")))
		    		   																				);
		    		   		this.details_textview.setText(this.details_textview.getText()+"\n");
		    		   		this.details_textview.setText(this.details_textview.getText()+"\n");
		    		   		this.details_textview.setText(this.details_textview.getText().toString()+(jsonObject.getString("visibility").equals("0")?"":this.visibilities[Integer.valueOf(jsonObject.getString("visibility"))]+" - ")+
		    		   																				  Html.fromHtml(jsonObject.getString("deepskyObservationDescription")));
		    		   	}
		    			this.text1_textview.setText(MainActivity.resources.getString(R.string.deepskyfragment_deepskyobservations_details)+this.deepskyObservationIdDetails.toString()+(DeepskyObservations.deepskyObservationsMaxId==0?"":" / "+Integer.valueOf(DeepskyObservations.deepskyObservationsMaxId)));   	    	    
		    			if(jsonObject.getString("hasDrawing").equals("0"))
		    				this.drawing_imageview.setImageBitmap(null);
		    			else if(jsonObject.getString("hasDrawing").equals("-1"))
		    				DeepskyObservations.broadcastDeepskyObservationDrawing(String.valueOf(this.deepskyObservationIdDetails));
		    			else {
		    				File file = new File(MainActivity.storagePath+"/deepsky/images/"+String.valueOf(this.deepskyObservationIdDetails)+".jpg");
		    				if(file.exists()) {
	    						BitmapFactory.Options options = new BitmapFactory.Options();
			    		        options.inSampleSize = 2;
			    		        Bitmap bm = BitmapFactory.decodeFile(MainActivity.storagePath+"/deepsky/images/"+String.valueOf(this.deepskyObservationIdDetails)+".jpg", options);
			    		        this.drawing_imageview.setImageBitmap(bm); 
		    				}
		    				else {
		    					//Toast.makeText(MainActivity.mainActivity, "Getting dsl drawing", Toast.LENGTH_LONG).show();
			    				DeepskyObservations.broadcastDeepskyObservationDrawing(String.valueOf(this.deepskyObservationIdDetails));		    							    					
		    				}
		    			}		    				
		    	    }
		        } 
		    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity, "DeepskyObservationsDetailsFragment Exception 1 "+e.toString(), Toast.LENGTH_LONG).show(); }
	       	}
    	}
    }    

}
