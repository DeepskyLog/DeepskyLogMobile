package org.deepskylog;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DeepskyFragment extends Fragment {
	
	public static Integer deepskyObservationsMaxId;
	public static Integer deepskyObservationSeenMaxId;

	private View deepskyFragmentView;
	private Bundle stateBundle=null;

	private Button observationsdetails_button;
	private Button observationslist_button;
	private Button observationsquery_button;
	private Button objectsquery_button;
	
	private TextView text1_textview;
	private TextView text2_textview;
	private TextView text3_textview;
	
	private Button command_button;
	
	private BroadcastReceiver broadcastMaxDeepskyObservationIdReceiver=new BroadcastReceiver() { @Override  public void onReceive(Context context, Intent intent) { onReceiveMaxDeepskyObservationId(context, intent); } };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 		if(savedInstanceState!=null) {
 			stateBundle=savedInstanceState.getBundle("stateBundle");
		}		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		deepskyFragmentView=inflater.inflate(R.layout.deepskyfragment, container, false);		
 		observationsdetails_button=(Button)deepskyFragmentView.findViewById(R.id.deepskyfragment_observationsdetails_button_id);
 		observationsdetails_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { observationDetailsOnClick(v); } });
 		observationslist_button=(Button)deepskyFragmentView.findViewById(R.id.deepskyfragment_observationslist_button_id);
 		observationslist_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { observationListOnClick(v); } });
 		observationsquery_button=(Button)deepskyFragmentView.findViewById(R.id.deepskyfragment_observationsquery_button_id);
 		observationsquery_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { observationQueryOnClick(v); } });
 		objectsquery_button=(Button)deepskyFragmentView.findViewById(R.id.deepskyfragment_objectsquery_button_id);
 		objectsquery_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { objectsQueryOnClick(v); } });
 		text1_textview=(TextView)deepskyFragmentView.findViewById(R.id.deepskyfragment_text1_textview_id);
 		text1_textview.setText("");
    	text2_textview=(TextView)deepskyFragmentView.findViewById(R.id.deepskyfragment_text2_textview_id);
    	text2_textview.setText("");
    	text3_textview=(TextView)deepskyFragmentView.findViewById(R.id.deepskyfragment_text3_textview_id);
    	text3_textview.setText("");
    	command_button=(Button)deepskyFragmentView.findViewById(R.id.deepskyfragment_command_button_id);
		command_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { commandOnClick(v); } });
 		if(savedInstanceState!=null) {
 			stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(stateBundle!=null) {
 	    	text1_textview.setText(stateBundle.getString("text1_textview"));
 	    	text2_textview.setText(stateBundle.getString("text2_textview"));
 	    	text3_textview.setText(stateBundle.getString("text3_textview"));
 		}
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastMaxDeepskyObservationIdReceiver, new IntentFilter("org.deepskylog.broadcastmaxdeepskyobservationid"));
		DeepskyObservations.broadcastDeepskyObservationsMaxId();
		deepskyObservationsMaxId=MainActivity.preferences.getInt("deepskyObservationsMaxId", 0);
		deepskyObservationSeenMaxId=MainActivity.preferences.getInt("deepskyObservationSeenMaxId", 0);
		if(deepskyObservationSeenMaxId==0) deepskyObservationSeenMaxId=deepskyObservationsMaxId;
		if(deepskyObservationsMaxId>0) {
			setText(deepskyObservationsMaxId+" deepsky observation in Deepskylog.");
			if((deepskyObservationsMaxId-deepskyObservationSeenMaxId)>0) setText(deepskyObservationsMaxId-deepskyObservationSeenMaxId+MainActivity.resources.getString(R.string.deepskyfragment_to_see));
		}
		return deepskyFragmentView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(broadcastMaxDeepskyObservationIdReceiver);
		super.onDestroyView();
	}

	private Bundle getStateBundle() {
        if(stateBundle!=null) return stateBundle;
        else {
			Bundle state=new Bundle();
	        state.putString("text1_textview", text1_textview.getText().toString());
	        state.putString("text2_textview", text2_textview.getText().toString());
	        state.putString("text3_textview", text3_textview.getText().toString());
	        return state;
        }
    }
	
	private void onReceiveMaxDeepskyObservationId(Context context, Intent intent) {
		String result=intent.getStringExtra("org.deepskylog.resultRAW");
		Integer tempMax=0;
		try { tempMax=Integer.valueOf(Utils.getTagContent(result, "result")); }
		catch (Exception e) { Toast.makeText(MainActivity.mainActivity, e.toString(), Toast.LENGTH_LONG).show(); }
		if(tempMax>deepskyObservationsMaxId) { 
	    	deepskyObservationsMaxId=tempMax;
	    	MainActivity.preferenceEditor.putInt("deepskyObservationsSeenMaxId", deepskyObservationsMaxId);
	       	MainActivity.preferenceEditor.commit();
			setText(deepskyObservationsMaxId+" deepsky observation in Deepskylog.");
			if((deepskyObservationsMaxId-deepskyObservationSeenMaxId)>0) setText(deepskyObservationsMaxId-deepskyObservationSeenMaxId+MainActivity.resources.getString(R.string.deepskyfragment_to_see));
	    }
 	}
	
	private void observationDetailsOnClick(View v) {
		MainActivity.goToFragment("deepskyObservationsFragment", MainActivity.ADD_TO_BACKSTACK);
	}
	
	private void observationListOnClick(View v) {
		MainActivity.goToFragment("deepskyObservationsListFragment", MainActivity.ADD_TO_BACKSTACK);
	}
	
	private void observationQueryOnClick(View v) {
		MainActivity.goToFragment("deepskyObservationsQueryFragment", MainActivity.ADD_TO_BACKSTACK);
	}

	private void objectsQueryOnClick(View v) {
		MainActivity.goToFragment("deepskyObjectsQueryFragment", MainActivity.ADD_TO_BACKSTACK);
	}
	
	private void commandOnClick(View v) {
		
	}
	
   private void setText(String theText) {
		text3_textview.setText(text2_textview.getText());
		text2_textview.setText(text1_textview.getText());
		text1_textview.setText(theText);
    }

}
