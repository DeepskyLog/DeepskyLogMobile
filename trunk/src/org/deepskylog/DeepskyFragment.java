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
import android.widget.Toast;

public class DeepskyFragment extends Fragment {
	
	public static Integer deepskyObservationMaxId;
	public static Integer deepskyObservationSeenMaxId;

	private View deepskyFragmentView;
	private Bundle savedState=null;

	private Button observationsdetails_button;
	private Button observationslist_button;
	private Button observationsquery_button;
	private Button objectsquery_button;
	
	private BroadcastReceiver broadcastMaxDeepskyObservationIdReceiver=new BroadcastReceiver() { @Override  public void onReceive(Context context, Intent intent) { onReceiveMaxDeepskyObservationId(context, intent); } };
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 		deepskyFragmentView=inflater.inflate(R.layout.deepskyfragment, container, false);
 		
 		observationsdetails_button=(Button)deepskyFragmentView.findViewById(R.id.deepskyfragment_observationsdetails_button_id);
 		observationsdetails_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { observationDetailsOnClick(v); } });
 		
 		if(savedInstanceState!=null) {
 			savedState=savedInstanceState.getBundle("savedState");
		}
 		if(savedState!=null) {

 		}
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(broadcastMaxDeepskyObservationIdReceiver, new IntentFilter("org.deepskylog.broadcastmaxdeepskyobservationid"));
		return deepskyFragmentView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("savedState", saveState());
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(broadcastMaxDeepskyObservationIdReceiver);
		super.onDestroyView();
	}

	private Bundle saveState() {
        Bundle state = new Bundle();
        return state;
    }
	
	private void onReceiveMaxDeepskyObservationId(Context context, Intent intent) {
		String result=intent.getStringExtra("org.deepskylog.resultRAW");
		Integer tempMax=0;
		try { tempMax=Integer.valueOf(Utils.getTagContent(result, "result")); }
		catch (Exception e) { Toast.makeText(MainActivity.mainActivity, e.toString(), Toast.LENGTH_LONG).show(); }
		if(tempMax>deepskyObservationMaxId) { 
	    	deepskyObservationMaxId=tempMax;
	    	MainActivity.preferenceEditor.putInt("deepskyObservationSeenMaxId", deepskyObservationMaxId);
	       	MainActivity.preferenceEditor.commit();
	    }
 	}
	
	private void observationDetailsOnClick(View v) {
		MainActivity.goToFragment("deepskyObservationsDetailsFragment", MainActivity.ADD_TO_BACKSTACK);
	}
}
