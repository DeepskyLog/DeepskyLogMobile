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
import android.view.ViewGroup;
import android.widget.TextView;

public class CometObservationsFragment extends Fragment {
	
	public static String sortMode="By Date";

	private View cometObservationsFragmentView;
	private Bundle stateBundle=null;

	private TextView text1_textview;

	private CometObservationsDetailsFragment cometObservationsDetailsFragment;
	private CometObservationsListFragment cometObservationsListFragment;
	
	private Fragment actualFragment;
	
	private BroadcastReceiver broadcastCometObservationSelectedReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveCometObservationSelected(context, intent); } };
	private BroadcastReceiver broadcastCometObservationsSwitchToListReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveCometObservationsSwitchToList(context, intent); } };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.cometObservationsFragmentView=inflater.inflate(R.layout.cometobservationsfragment, container, false);
		CometObservationsFragment.sortMode=CometFragment.sortMode;
		this.text1_textview=((TextView)this.cometObservationsFragmentView.findViewById(R.id.cometobservationsfragment_text1_textview_id));
		this.text1_textview.setText("Comet observations");
		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(this.stateBundle!=null) {
 			this.text1_textview.setText(stateBundle.getString("text1_textview"));
 			CometObservationsFragment.sortMode=stateBundle.getString("sortMode");
 		}
 		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastCometObservationSelectedReceiver, new IntentFilter("org.deepskylog.broadcastcometobservationselected"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastCometObservationsSwitchToListReceiver, new IntentFilter("org.deepskylog.broadcastcometobservationswitchtolist"));
		return this.cometObservationsFragmentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.cometObservationsDetailsFragment=new CometObservationsDetailsFragment();
		this.cometObservationsListFragment=new CometObservationsListFragment();		
		this.actualFragment=
				((this.stateBundle!=null)
						?(stateBundle.getString("actualFragmentName").equals("cometObservationsListFragment")
								?this.cometObservationsListFragment
								:this.cometObservationsDetailsFragment)
						:(MainActivity.preferences.getString("CometObservationsFragmentActualFragment", "cometObservationsListFragment").equals("cometObservationsListFragment")
								?this.cometObservationsListFragment
								:this.cometObservationsDetailsFragment));
		MainActivity.fragmentManager.beginTransaction()
			.add(R.id.cometobservationsfragment_framelayout, this.cometObservationsDetailsFragment,"cometObservationsDetailsFragment")
			.hide(this.cometObservationsDetailsFragment)
			.add(R.id.cometobservationsfragment_framelayout, this.cometObservationsListFragment,"cometObservationsListFragment")
			.hide(this.cometObservationsListFragment)
			.commit();
		this.switchTo(this.actualFragment);
 	}
	
	@Override
	public void onPause() {
		MainActivity.fragmentManager.beginTransaction()
			.remove(cometObservationsDetailsFragment)
			.remove(cometObservationsListFragment)
			.commit();
		super.onPause();
	}
	
	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastCometObservationSelectedReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastCometObservationsSwitchToListReceiver);
		super.onDestroyView();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", this.getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	private Bundle getStateBundle() {
        Bundle state=new Bundle();
        state.putString("text1_textview", (this.text1_textview!=null?this.text1_textview.getText().toString():""));
        state.putString("sortMode", CometObservationsFragment.sortMode);
        state.putString("actualFragmentName", (this.actualFragment==this.cometObservationsDetailsFragment?"cometObservationsDetailsFragment":"cometObservationsListFragment"));
        return state;
    }
	
	public void onReceiveCometObservationSelected(Context context, Intent intent) {
		switchTo(this.cometObservationsDetailsFragment);
	}
			
	public void onReceiveCometObservationsSwitchToList(Context context, Intent intent) {
		switchTo(this.cometObservationsListFragment); 
	}
			
	private void switchTo(Fragment fragment) {
		MainActivity.fragmentManager.beginTransaction()
									.hide(this.actualFragment)
									.show(fragment)
									.commit();
		if(fragment==this.cometObservationsListFragment) { 
			MainActivity.preferenceEditor.putString("CometObservationsFragmentActualFragment", "cometObservationsListFragment").commit();
			if(CometObservationsFragment.sortMode.equals("By Date")) this.text1_textview.setText("Comet Observations - List - by date");
			else this.text1_textview.setText("Comet Observations - List - unsorted");
		}
		if(fragment==this.cometObservationsDetailsFragment) { 
			MainActivity.preferenceEditor.putString("CometObservationsFragmentActualFragment", "cometObservationsListFragment").commit();
			if(CometObservationsFragment.sortMode.equals("By Date")) this.text1_textview.setText("Comet Observations - Details - by date"); 
			else this.text1_textview.setText("Comet Observations - Details - unsorted");
		}
		this.actualFragment=fragment;
	}
}
