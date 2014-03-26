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

public class DeepskyObservationsFragment extends Fragment {

	private View deepskyObservationsFragmentView;
	private Bundle stateBundle=null;

	private TextView text1_textview;

	private DeepskyObservationsDetailsFragment deepskyObservationsDetailsFragment;
	private DeepskyObservationsListFragment deepskyObservationsListFragment;
	
	private Fragment actualFragment;
	
	private BroadcastReceiver broadcastDeepskyObservationSelectedForDetailsReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationSelectedForDetails(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationsSwitchToListReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationsSwitchToList(context, intent); } };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.deepskyObservationsFragmentView=inflater.inflate(R.layout.deepskyobservationsfragment, container, false);
		this.text1_textview=((TextView)this.deepskyObservationsFragmentView.findViewById(R.id.deepskyobservationsfragment_text1_textview_id));
		this.text1_textview.setText("Deepsky observations");
		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(this.stateBundle!=null) {
 			this.text1_textview.setText(stateBundle.getString("text1_textview"));
 		}
 		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationSelectedForDetailsReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationselectedfordetails"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationsSwitchToListReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationswitchtolist"));
		return this.deepskyObservationsFragmentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.deepskyObservationsDetailsFragment=new DeepskyObservationsDetailsFragment();
		this.deepskyObservationsListFragment=new DeepskyObservationsListFragment();		
		this.actualFragment=((this.stateBundle!=null)?(stateBundle.getString("actualFragmentName").equals("deepskyObservationsDetailsFragment")?this.deepskyObservationsDetailsFragment:this.deepskyObservationsListFragment):this.deepskyObservationsListFragment);
		MainActivity.fragmentManager.beginTransaction()
			.add(R.id.deepskyobservationsfragment_framelayout, this.deepskyObservationsDetailsFragment,"deepskyObservationsDetailsFragment")
			.hide(this.deepskyObservationsDetailsFragment)
			.add(R.id.deepskyobservationsfragment_framelayout, this.deepskyObservationsListFragment,"deepskyObservationsListFragment")
			.hide(this.deepskyObservationsListFragment)
			.commit();
		this.switchTo(this.actualFragment);
 	}
	
	@Override
	public void onPause() {
		MainActivity.fragmentManager.beginTransaction()
			.remove(deepskyObservationsDetailsFragment)
			.remove(deepskyObservationsListFragment)
			.commit();
		super.onPause();
	}
	
	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationSelectedForDetailsReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationsSwitchToListReceiver);
		super.onDestroyView();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", this.getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	private Bundle getStateBundle() {
        Bundle state=new Bundle();
        state.putString("text1_textview", this.text1_textview.getText().toString());
        state.putString("actualFragmentName", (this.actualFragment==this.deepskyObservationsDetailsFragment?"deepskyObservationsDetailsFragment":"deepskyObservationsListFragment"));
        return state;
    }
	
	public void onReceiveDeepskyObservationSelectedForDetails(Context context, Intent intent) {
		switchTo(this.deepskyObservationsDetailsFragment);
	}
			
	public void onReceiveDeepskyObservationsSwitchToList(Context context, Intent intent) {
		switchTo(this.deepskyObservationsListFragment); 
	}
			
	private void switchTo(Fragment fragment) {
		MainActivity.fragmentManager.beginTransaction()
									.hide(this.actualFragment)
									.show(fragment)
									.commit();
		if(this.actualFragment==this.deepskyObservationsDetailsFragment) this.deepskyObservationsListFragment.requerySetData();
		if(fragment==this.deepskyObservationsListFragment) { this.text1_textview.setText("Deepsky Observations - List"); }
		if(fragment==this.deepskyObservationsDetailsFragment) { this.text1_textview.setText("Deepsky Observations - Details"); }
		this.actualFragment=fragment;
	}
}
