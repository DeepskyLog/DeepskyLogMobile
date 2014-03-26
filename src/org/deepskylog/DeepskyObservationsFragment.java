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
import android.widget.Button;
import android.widget.TextView;

public class DeepskyObservationsFragment extends Fragment {

	private View deepskyObservationsFragmentView;
	private Bundle stateBundle=null;

	private TextView text1_textview;
	private TextView dsobstosee_textview;
	private Button mode_button;
	
	private DeepskyObservationsDetailsFragment deepskyObservationsDetailsFragment;
	private DeepskyObservationsListFragment deepskyObservationsListFragment;
	
	private Fragment actualFragment;
	
	private BroadcastReceiver broadcastDeepskyObservationsMaxIdChangedReceiver=new BroadcastReceiver() { @Override  public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationsMaxIdChanged(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationSelectedForDetailsReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationSelectedForDetails(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationFetchingReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationFetching(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationDetailsDisplayedReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { if(actualFragment==deepskyObservationsDetailsFragment) { onReceiveDeepskyObservationDetailsDisplayed(context, intent); } } };
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
		this.text1_textview.setText("");
		this.dsobstosee_textview=((TextView)this.deepskyObservationsFragmentView.findViewById(R.id.deepskyobservationsfragment_dsobstosee_textview_id));
		this.dsobstosee_textview.setText("");
		this.dsobstosee_textview.setClickable(true);
		this.dsobstosee_textview.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { dsobstoseeOnClick(); } });
		this.mode_button=(Button)deepskyObservationsFragmentView.findViewById(R.id.deepskyobservationsfragment_mode_button_id);
		this.mode_button.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { swith(); } });
 		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(this.stateBundle!=null) {
 			this.text1_textview.setText(stateBundle.getString("text1_textview"));
 			this.dsobstosee_textview.setText(stateBundle.getString("dsobstosee_textview"));
 		}
 		this.stateBundle=null;
 		this.initFragments();
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationsMaxIdChangedReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationsmaxidchanged"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationSelectedForDetailsReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationselectedfordetails"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationFetchingReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationfetching"));		
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationDetailsDisplayedReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationdetailsdisplayed"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationsSwitchToListReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationswitchtolist"));
		return this.deepskyObservationsFragmentView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", this.getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationsMaxIdChangedReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationSelectedForDetailsReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationFetchingReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationDetailsDisplayedReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationsSwitchToListReceiver);
		super.onDestroyView();
	}

	
	private Bundle getStateBundle() {
        Bundle state=new Bundle();
        state.putString("text1_textview", this.text1_textview.getText().toString());
        state.putString("dsobstosee_textview", this.dsobstosee_textview.getText().toString());
        return state;
    }

	private void onReceiveDeepskyObservationsMaxIdChanged(Context context, Intent intent) {
		if((DeepskyObservations.deepskyObservationsMaxId-DeepskyObservations.deepskyObservationSeenMaxId)>0) 
			this.dsobstosee_textview.setText(((Integer)(DeepskyObservations.deepskyObservationsMaxId-DeepskyObservations.deepskyObservationSeenMaxId)).toString()+MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_to_see));
	}
	
	public void onReceiveDeepskyObservationSelectedForDetails(Context context, Intent intent) {
		switchTo(this.deepskyObservationsDetailsFragment);
	}
	
	public void onReceiveDeepskyObservationFetching(Context context, Intent intent) {
		Integer theId=intent.getIntExtra("org.deepskylog.deepskyobservationid",DeepskyObservations.deepskyObservationSeenMaxId);
		this.text1_textview.setText(MainActivity.resources.getString(R.string.deepskyfragment_deepskyobservations_fetching)+theId.toString()+(DeepskyObservations.deepskyObservationsMaxId==0?"":" / "+Integer.valueOf(DeepskyObservations.deepskyObservationsMaxId)));
	}
	
	private void onReceiveDeepskyObservationDetailsDisplayed(Context context, Intent intent) {
		String result=intent.getStringExtra("org.deepskylog.deepskyObservationId");
		if(result.equals("-1"))
			this.text1_textview.setText("Observation deleted");   	    	    
		else if(result.equals("0")) 
			this.text1_textview.setText("Observation unavailable");   	    	    
		else 
			this.text1_textview.setText(MainActivity.resources.getString(R.string.deepskyfragment_deepskyobservations_details)+result+(DeepskyObservations.deepskyObservationsMaxId==0?"":" / "+Integer.valueOf(DeepskyObservations.deepskyObservationsMaxId)));   	    	    
    	if((DeepskyObservations.deepskyObservationsMaxId-DeepskyObservations.deepskyObservationSeenMaxId)>0) dsobstosee_textview.setText(((Integer)(DeepskyObservations.deepskyObservationsMaxId-DeepskyObservations.deepskyObservationSeenMaxId)).toString()+MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_to_see));
	}
	
	public void onReceiveDeepskyObservationsSwitchToList(Context context, Intent intent) {
		switchTo(this.deepskyObservationsListFragment); 
	}
		
	private void initFragments() {
		this.deepskyObservationsDetailsFragment=new DeepskyObservationsDetailsFragment();
		this.deepskyObservationsListFragment=new DeepskyObservationsListFragment();		
		actualFragment=deepskyObservationsListFragment;
		MainActivity.fragmentManager.beginTransaction()
									.add(R.id.deepskyobservationsfragment_framelayout, deepskyObservationsDetailsFragment)
									.hide(this.deepskyObservationsDetailsFragment)
									.add(R.id.deepskyobservationsfragment_framelayout, deepskyObservationsListFragment)
									.commit();
	}
	
	private void swith() {
		if(this.deepskyObservationsDetailsFragment.isVisible()) { switchTo(this.deepskyObservationsListFragment); }
		else { switchTo(this.deepskyObservationsDetailsFragment); }
	}
	
	private void dsobstoseeOnClick() {
		//TODO : look for better way to activate observation
    	MainActivity.preferenceEditor.putInt("deepskyObservationIdToGet",DeepskyObservations.deepskyObservationSeenMaxId+1).commit();
    	switchTo(this.deepskyObservationsDetailsFragment);
	}
	
	private void switchTo(Fragment fragment) {
		if (fragment.isVisible()) return;
		MainActivity.fragmentManager.beginTransaction()
									.hide(actualFragment)
									.show(fragment)
									.commit();
		this.actualFragment=fragment;
	}
}
