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

public class DeepskyFragment extends Fragment {
	
	public static String sortMode;

	private View deepskyFragmentView;
	private Bundle stateBundle=null;

	private Button observationsbydate_button;
	private Button observationsunsorted_button;
	private Button observationsquery_button;
	private Button objectsquery_button;
	private TextView text1_textview;
	private TextView text2_textview;
	private TextView text3_textview;
	private Button command_button;
	
	private BroadcastReceiver broadcastDeepskyObservationsMaxIdChangedReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationsMaxIdChanged(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationSeenMaxIdChangedReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationSeenMaxIdChanged(context, intent); } };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.deepskyFragmentView=inflater.inflate(R.layout.deepskyfragment, container, false);		
		this.observationsbydate_button=(Button)this.deepskyFragmentView.findViewById(R.id.deepskyfragment_observationsbydate_button_id);
		this.observationsbydate_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { sortMode="By Date"; observationsByDateOnClick(v); } });
		this.observationsunsorted_button=(Button)this.deepskyFragmentView.findViewById(R.id.deepskyfragment_observationsunsorted_button_id);
		this.observationsunsorted_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { sortMode="None"; observationsUnsortedOnClick(v); } });
		this.observationsquery_button=(Button)this.deepskyFragmentView.findViewById(R.id.deepskyfragment_observationsquery_button_id);
		this.observationsquery_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { observationQueryOnClick(v); } });
		this.objectsquery_button=(Button)this.deepskyFragmentView.findViewById(R.id.deepskyfragment_objectsquery_button_id);
		this.objectsquery_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { objectsQueryOnClick(v); } });
		this.text1_textview=(TextView)this.deepskyFragmentView.findViewById(R.id.deepskyfragment_text1_textview_id);
		this.text1_textview.setText("");
		this.text2_textview=(TextView)this.deepskyFragmentView.findViewById(R.id.deepskyfragment_text2_textview_id);
		this.text2_textview.setText("");
		this.text3_textview=(TextView)this.deepskyFragmentView.findViewById(R.id.deepskyfragment_text3_textview_id);
		this.text3_textview.setText("");
		this.command_button=(Button)this.deepskyFragmentView.findViewById(R.id.deepskyfragment_command_button_id);
		this.command_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { commandOnClick(v); } });
		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(this.stateBundle!=null) {
 			this.text1_textview.setText(this.stateBundle.getString("text1_textview"));
 			this.text2_textview.setText(this.stateBundle.getString("text2_textview"));
 			this.text3_textview.setText(this.stateBundle.getString("text3_textview"));
 		}
 		else {
 			if(DeepskyObservations.deepskyObservationsMaxId>0) {
 				this.setText(DeepskyObservations.deepskyObservationsMaxId+MainActivity.resources.getString(R.string.deepskyfragment_deepskyobservations_in_dsl));
 				if((DeepskyObservations.deepskyObservationsMaxId-DeepskyObservations.deepskyObservationSeenMaxId)>0) this.setText(DeepskyObservations.deepskyObservationsMaxId-DeepskyObservations.deepskyObservationSeenMaxId+MainActivity.resources.getString(R.string.deepskyfragment_to_see));
 			}
 		}
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationsMaxIdChangedReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationsmaxidchanged"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationSeenMaxIdChangedReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationsseenmaxidchanged"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationSeenMaxIdChangedReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationsseenmaxidchanged"));
		DeepskyObservations.broadcastDeepskyObservationsMaxIdUpdate();
		return this.deepskyFragmentView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", this.getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationsMaxIdChangedReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationSeenMaxIdChangedReceiver);
		super.onDestroyView();
	}

	private Bundle getStateBundle() {
        if(this.stateBundle!=null) return this.stateBundle;
        else {
			Bundle state=new Bundle();
	        state.putString("text1_textview", text1_textview.getText().toString());
	        state.putString("text2_textview", text2_textview.getText().toString());
	        state.putString("text3_textview", text3_textview.getText().toString());
	        return state;
        }
    }
	
	private void onReceiveDeepskyObservationsMaxIdChanged(Context context, Intent intent) {
		this.clearText();
		this.setText(DeepskyObservations.deepskyObservationsMaxId+MainActivity.resources.getString(R.string.deepskyfragment_deepskyobservations_in_dsl));
		if((DeepskyObservations.deepskyObservationsMaxId-DeepskyObservations.deepskyObservationSeenMaxId)>0) this.setText(DeepskyObservations.deepskyObservationsMaxId-DeepskyObservations.deepskyObservationSeenMaxId+MainActivity.resources.getString(R.string.deepskyfragment_to_see));
	}
	
	private void onReceiveDeepskyObservationSeenMaxIdChanged(Context context, Intent intent) {
		this.clearText();
		this.setText(DeepskyObservations.deepskyObservationsMaxId+MainActivity.resources.getString(R.string.deepskyfragment_deepskyobservations_in_dsl));
		if((DeepskyObservations.deepskyObservationsMaxId-DeepskyObservations.deepskyObservationSeenMaxId)>0) this.setText(DeepskyObservations.deepskyObservationsMaxId-DeepskyObservations.deepskyObservationSeenMaxId+MainActivity.resources.getString(R.string.deepskyfragment_to_see));
	}
	
	private void observationsByDateOnClick(View v) {
		MainActivity.goToFragment("deepskyObservationsFragment", MainActivity.ADD_TO_BACKSTACK);
	}
	
	private void observationsUnsortedOnClick(View v) {
		MainActivity.goToFragment("deepskyObservationsFragment", MainActivity.ADD_TO_BACKSTACK);
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
		this.text3_textview.setText(this.text2_textview.getText());
		this.text2_textview.setText(this.text1_textview.getText());
		this.text1_textview.setText(theText);
    }

    private void clearText() {
    	this.text3_textview.setText("");
    	this.text2_textview.setText("");
    	this.text1_textview.setText("");
    }

}
