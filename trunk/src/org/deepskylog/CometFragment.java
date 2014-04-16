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

public class CometFragment extends Fragment {
	public static String sortMode;

	private View cometFragmentView;
	private Bundle stateBundle=null;

	private Button observationsbydate_button;
	private Button observationsunsorted_button;
	private Button observationsquery_button;
	private Button objectsquery_button;
	private TextView text1_textview;
	private TextView text2_textview;
	private TextView text3_textview;
	private Button command_button;
	
	private BroadcastReceiver broadcastCometObservationsMaxIdChangedReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveCometObservationsMaxIdChanged(context, intent); } };
	private BroadcastReceiver broadcastCometObservationSeenMaxIdChangedReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveCometObservationSeenMaxIdChanged(context, intent); } };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.cometFragmentView=inflater.inflate(R.layout.cometfragment, container, false);		
		this.observationsbydate_button=(Button)this.cometFragmentView.findViewById(R.id.cometfragment_observationsbydate_button_id);
		this.observationsbydate_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { sortMode="By Date"; observationsByDateOnClick(v); } });
		this.observationsunsorted_button=(Button)this.cometFragmentView.findViewById(R.id.cometfragment_observationsunsorted_button_id);
		this.observationsunsorted_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { sortMode="None"; observationsUnsortedOnClick(v); } });
		this.observationsquery_button=(Button)this.cometFragmentView.findViewById(R.id.cometfragment_observationsquery_button_id);
		this.observationsquery_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { observationQueryOnClick(v); } });
		this.objectsquery_button=(Button)this.cometFragmentView.findViewById(R.id.cometfragment_objectsquery_button_id);
		this.objectsquery_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { objectsQueryOnClick(v); } });
		this.text1_textview=(TextView)this.cometFragmentView.findViewById(R.id.cometfragment_text1_textview_id);
		this.text1_textview.setText("");
		this.text2_textview=(TextView)this.cometFragmentView.findViewById(R.id.cometfragment_text2_textview_id);
		this.text2_textview.setText("");
		this.text3_textview=(TextView)this.cometFragmentView.findViewById(R.id.cometfragment_text3_textview_id);
		this.text3_textview.setText("");
		this.command_button=(Button)this.cometFragmentView.findViewById(R.id.cometfragment_command_button_id);
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
 			if(CometObservations.cometObservationsMaxId>0) {
 				this.setText(CometObservations.cometObservationsMaxId+MainActivity.resources.getString(R.string.cometfragment_cometobservations_in_dsl));
 				if((CometObservations.cometObservationsMaxId-CometObservations.cometObservationSeenMaxId)>0) this.setText(CometObservations.cometObservationsMaxId-CometObservations.cometObservationSeenMaxId+MainActivity.resources.getString(R.string.cometfragment_to_see));
 			}
 		}
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastCometObservationsMaxIdChangedReceiver, new IntentFilter("org.cometlog.broadcastcometobservationsmaxidchanged"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastCometObservationSeenMaxIdChangedReceiver, new IntentFilter("org.cometlog.broadcastcometobservationsseenmaxidchanged"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastCometObservationSeenMaxIdChangedReceiver, new IntentFilter("org.cometlog.broadcastcometobservationsseenmaxidchanged"));
		CometObservations.broadcastCometObservationsMaxIdUpdate();
		return this.cometFragmentView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", this.getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastCometObservationsMaxIdChangedReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastCometObservationSeenMaxIdChangedReceiver);
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
	
	private void onReceiveCometObservationsMaxIdChanged(Context context, Intent intent) {
		this.clearText();
		this.setText(CometObservations.cometObservationsMaxId+MainActivity.resources.getString(R.string.cometfragment_cometobservations_in_dsl));
		if((CometObservations.cometObservationsMaxId-CometObservations.cometObservationSeenMaxId)>0) this.setText(CometObservations.cometObservationsMaxId-CometObservations.cometObservationSeenMaxId+MainActivity.resources.getString(R.string.cometfragment_to_see));
	}
	
	private void onReceiveCometObservationSeenMaxIdChanged(Context context, Intent intent) {
		this.clearText();
		this.setText(CometObservations.cometObservationsMaxId+MainActivity.resources.getString(R.string.cometfragment_cometobservations_in_dsl));
		if((CometObservations.cometObservationsMaxId-CometObservations.cometObservationSeenMaxId)>0) this.setText(CometObservations.cometObservationsMaxId-CometObservations.cometObservationSeenMaxId+MainActivity.resources.getString(R.string.cometfragment_to_see));
	}
	
	private void observationsByDateOnClick(View v) {
		MainActivity.goToFragment("cometObservationsFragment", MainActivity.ADD_TO_BACKSTACK);
	}
	
	private void observationsUnsortedOnClick(View v) {
		MainActivity.goToFragment("cometObservationsFragment", MainActivity.ADD_TO_BACKSTACK);
	}
	
	private void observationQueryOnClick(View v) {
		MainActivity.goToFragment("cometObservationsQueryFragment", MainActivity.ADD_TO_BACKSTACK);
	}

	private void objectsQueryOnClick(View v) {
		MainActivity.goToFragment("cometObjectsQueryFragment", MainActivity.ADD_TO_BACKSTACK);
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
