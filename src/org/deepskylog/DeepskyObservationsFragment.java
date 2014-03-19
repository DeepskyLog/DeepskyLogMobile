package org.deepskylog;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.widget.Toast;

public class DeepskyObservationsFragment extends Fragment {

	private static final Integer DISPLAY_MODE_NORMAL=1;
	private static final Integer DISPLAY_MODE_LIST  =2;
	
	private Bundle stateBundle=null;

	private View deepskyObservationsFragmentView;
	private static TextView text1_textview;
	private static TextView dsobstosee_textview;

	private Button mode_button;
	
	private DeepskyObservationsDetailsFragment deepskyObservationsDetailsFragment;
	private DeepskyObservationsListFragment deepskyObservationsListFragment;
	
	private Integer displayMode;

	private Fragment actualFragment;
	
	private BroadcastReceiver broadcastMaxDeepskyObservationIdReceiver=new BroadcastReceiver() { @Override  public void onReceive(Context context, Intent intent) { onReceiveMaxDeepskyObservationId(context, intent); } };
	private BroadcastReceiver broadcastNoDeepskyObservationReceiver=new BroadcastReceiver() {  @Override  public void onReceive(Context context, Intent intent) { onReceiveNoDeepskyObservation(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservation(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationFetchingReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationFetching(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationSelectedForDetailsReceiver=new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationSelectedForDetails(context, intent); } };

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		deepskyObservationsFragmentView=inflater.inflate(R.layout.deepskyobservationsfragment, container, false);
		text1_textview=((TextView)deepskyObservationsFragmentView.findViewById(R.id.deepskyobservationsfragment_text1_textview_id));
		text1_textview.setText("Fetching observations...");
 		dsobstosee_textview=((TextView)deepskyObservationsFragmentView.findViewById(R.id.deepskyobservationsfragment_dsobstosee_textview_id));
		dsobstosee_textview.setText("");
		dsobstosee_textview.setClickable(true);
		dsobstosee_textview.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { dsobstoseeOnClick(); } });
		mode_button=(Button)deepskyObservationsFragmentView.findViewById(R.id.deepskyobservationsfragment_mode_button_id);
		mode_button.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { swith(); } });
 		if(savedInstanceState!=null) {
 			stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(stateBundle!=null) {
 			text1_textview.setText(stateBundle.getString("text1_textview"));
 			displayMode=(stateBundle.getInt("displayMode"));
 		}
 		stateBundle=null;
		initFragments();
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastMaxDeepskyObservationIdReceiver, new IntentFilter("org.deepskylog.broadcastmaxdeepskyobservationid"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastDeepskyObservationReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservation"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastNoDeepskyObservationReceiver, new IntentFilter("org.deepskylog.broadcastnodeepskyobservation"));
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastDeepskyObservationFetchingReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationfetching"));		
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastDeepskyObservationSelectedForDetailsReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationselectedfordetails"));
		return deepskyObservationsFragmentView;
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
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(broadcastDeepskyObservationFetchingReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(broadcastDeepskyObservationSelectedForDetailsReceiver);
		super.onDestroyView();
	}

	
	private Bundle getStateBundle() {
        Bundle state = new Bundle();
        state.putString("text1_textview", text1_textview.getText().toString());
        state.putInt("displayMode", displayMode);
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
	       	if((DeepskyFragment.deepskyObservationsMaxId-DeepskyFragment.deepskyObservationSeenMaxId)>0) dsobstosee_textview.setText(((Integer)(DeepskyFragment.deepskyObservationsMaxId-DeepskyFragment.deepskyObservationSeenMaxId)).toString()+MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_to_see));
		}
	}
	
	private void onReceiveNoDeepskyObservation(Context context, Intent intent) {
		try {
			String unavailableId=Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"), "deepskyObservationId");
   	    	text1_textview.setText("Details "+unavailableId+(DeepskyFragment.deepskyObservationsMaxId==0?"":" / "+DeepskyFragment.deepskyObservationsMaxId.toString())+ " ("+unavailableId+ MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_not_available)+")");   	    	    
		}
    	catch (Exception e) { Toast.makeText(MainActivity.mainActivity,e.toString(),Toast.LENGTH_LONG).show(); }
	}

	public void onReceiveDeepskyObservation(Context context, Intent intent) {
		String result;
		try { result=Utils.getTagContent(intent.getStringExtra("org.deepskylog.resultRAW"),"deepskyObservationId"); }
    	catch(Exception e) { result=e.toString(); }
		text1_textview.setText("Details "+result+(DeepskyFragment.deepskyObservationsMaxId==0?"":" / "+DeepskyFragment.deepskyObservationsMaxId.toString()));   	    	    
    	if((DeepskyFragment.deepskyObservationsMaxId-DeepskyFragment.deepskyObservationSeenMaxId)>0) dsobstosee_textview.setText(((Integer)(DeepskyFragment.deepskyObservationsMaxId-DeepskyFragment.deepskyObservationSeenMaxId)).toString()+MainActivity.resources.getString(R.string.deepskyobservationsdetailsfragment_to_see));
	}

	public void onReceiveDeepskyObservationFetching(Context context, Intent intent) {
		String theId=intent.getStringExtra("org.deepsky.deepskyobservationid");
 		text1_textview.setText("Fetching 1 "+theId.toString()+(DeepskyFragment.deepskyObservationsMaxId==0?"":" / "+DeepskyFragment.deepskyObservationsMaxId.toString()));
	}
	
	public void onReceiveDeepskyObservationSelectedForDetails(Context context, Intent intent) {
		Integer theId=intent.getIntExtra("org.deepskylog.deepskyobservationid",DeepskyFragment.deepskyObservationSeenMaxId);
		text1_textview.setText("Fetching 2 "+theId.toString()+(DeepskyFragment.deepskyObservationsMaxId==0?"":" / "+DeepskyFragment.deepskyObservationsMaxId.toString()));
    	MainActivity.preferenceEditor.putInt("deepskyObservationIdToGet",theId);
    	MainActivity.preferenceEditor.commit();
		switchTo(deepskyObservationsDetailsFragment);
	}

	private void initFragments() {
		deepskyObservationsDetailsFragment=new DeepskyObservationsDetailsFragment();
		deepskyObservationsListFragment=new DeepskyObservationsListFragment();		
		FragmentTransaction fragmentManagerTransaction;
		fragmentManagerTransaction=MainActivity.fragmentManager.beginTransaction();
		fragmentManagerTransaction.add(R.id.deepskyobservationsfragment_framelayout, deepskyObservationsDetailsFragment);
		fragmentManagerTransaction.hide(deepskyObservationsDetailsFragment);
		fragmentManagerTransaction.add(R.id.deepskyobservationsfragment_framelayout, deepskyObservationsListFragment);
		fragmentManagerTransaction.commit();
		actualFragment=deepskyObservationsListFragment;
		displayMode=DISPLAY_MODE_LIST;		
	}
	
	private void swith() {
		if(displayMode==DISPLAY_MODE_NORMAL) { switchTo(deepskyObservationsListFragment); displayMode=DISPLAY_MODE_LIST; }
		else { switchTo(deepskyObservationsDetailsFragment); displayMode=DISPLAY_MODE_NORMAL; }
	}
	
	private void dsobstoseeOnClick() {
    	MainActivity.preferenceEditor.putInt("deepskyObservationIdToGet",DeepskyFragment.deepskyObservationSeenMaxId+1);
    	MainActivity.preferenceEditor.commit();
		switchTo(deepskyObservationsDetailsFragment);
	}
	
	private void switchTo(Fragment fragment) {
		if (fragment.isVisible()) return;
		FragmentTransaction fragmentManagerTransaction;
		fragmentManagerTransaction=MainActivity.fragmentManager.beginTransaction();
		fragmentManagerTransaction.hide(actualFragment);
		fragmentManagerTransaction.show(fragment);
		fragmentManagerTransaction.commit();
		actualFragment=fragment;
	}
}
