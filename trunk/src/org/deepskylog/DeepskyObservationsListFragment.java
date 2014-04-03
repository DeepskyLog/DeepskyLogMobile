package org.deepskylog;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class DeepskyObservationsListFragment extends Fragment {
	
 	private View deepskyObservationsListView;
    private Bundle stateBundle=null;
	
	private ListView deepskyObservationsList;
	private SimpleCursorAdapter deepskyObservationListAdapter;
	
	private String deepskyObservationsListDate;
	
	private String direction;

	private BroadcastReceiver broadcastDeepskyObservationsListReceiver=new BroadcastReceiver() {  @Override  public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationsList(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationsListDaysReceiver=new BroadcastReceiver() {  @Override  public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationsListDays(context, intent); } };

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
		this.deepskyObservationsListView=inflater.inflate(R.layout.deepskyobservationslistfragment, container, false);
		if(savedInstanceState!=null) {
			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
		this.deepskyObservationsListDate=(new SimpleDateFormat("yyyyMMdd")).format(new Date());
		if(stateBundle!=null) {
 			this.deepskyObservationsListDate=this.stateBundle.getString("deepskyObservationsListDate",(new SimpleDateFormat("yyyyMMdd")).format(new Date()));
 			this.direction=this.stateBundle.getString("direction","down");
  		}
		else {
			this.deepskyObservationsListDate=MainActivity.preferences.getString("deepskyObservationsListDate", (new SimpleDateFormat("yyyyMMdd")).format(new Date()));
 			this.direction="down";
		}
		stateBundle=null;		
 		DeepskyObservations.broadcastDeepskyObservationsListFromDateToDate(Utils.precedingMonth(this.deepskyObservationsListDate),Utils.followingMonth(this.deepskyObservationsListDate));
		this.deepskyObservationsList=((ListView)(this.deepskyObservationsListView.findViewById(R.id.deepskyobservationslistfragment_observationlist_listview)));
 		this.deepskyObservationsList.setOnItemClickListener(new OnItemClickListener(){ public void onItemClick(AdapterView<?> parent, View v, int position, long id) { ondDeepskyObservationItemClick(parent, v, position, id); }; }); 
		this.deepskyObservationsList.setOnTouchListener(new OnSwipeTouchListener(MainActivity.mainActivity) {
 		    public void onSwipeTop() { }
 		    public void onSwipeRight() { direction="down"; setDataFromDate(Utils.precedingDate(deepskyObservationsListDate),"down"); }
 		    public void onSwipeLeft() { direction="up"; setDataFromDate(Utils.followingDate(deepskyObservationsListDate),"up"); }
 		    public void onSwipeBottom() {  }
		});
  		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationsListReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationslist"));
  		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationsListDaysReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationslistdays"));
		return deepskyObservationsListView;
	}
	
	@Override 
	public void onResume() {
		super.onResume();
		//this.testSetData();
	}
	
	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationsListReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationsListDaysReceiver);
		super.onDestroyView();
	}

	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", this.getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
    
	private Bundle getStateBundle() {
        Bundle state=new Bundle();
        state.putString("deepskyObservationsListDate", this.deepskyObservationsListDate);
        state.putString("direction", this.direction);
        return state;
    }
	
	public void requerySetData() {
		Cursor mCursor=DslDatabase.execSql("SELECT deepskyObservationId AS _id, deepskyObjectName, observerName FROM deepskyObservationsList WHERE (deepskyObservationDate='"+this.deepskyObservationsListDate+"')");
        if(mCursor.getCount()>0) this.deepskyObservationListAdapter.changeCursor(mCursor);
        else Toast.makeText(MainActivity.mainActivity, "no data for date "+this.deepskyObservationsListDate, Toast.LENGTH_LONG).show();
	}
	
	private void onReceiveDeepskyObservationsList(Context context, Intent intent) {
		requerySetData();
	}
	
	private void onReceiveDeepskyObservationsListDays(Context context, Intent intent) {

		// look if actual date is in list, else look for next in f(up/down)
		// then look for list itself

	}
	
	public void testSetData() {
		String[] fromColumns={"deepskyObjectName","observerName"};
        int[] toViews={R.id.deepskyobservationslistitemobjectname_textview_id,R.id.deepskyobservationslistitemobservername_textview_id};
        Cursor mCursor=DslDatabase.execSql("SELECT deepskyObservationId AS _id, deepskyObjectName, observerName FROM deepskyObservationsList WHERE (deepskyObservationDate='"+this.deepskyObservationsListDate+"')");
        this.deepskyObservationListAdapter=new SimpleCursorAdapter(MainActivity.mainActivity, R.layout.deepskyobservationslistitem, mCursor, fromColumns, toViews, 0);
        this.deepskyObservationsList.setAdapter(this.deepskyObservationListAdapter);
	}

	@SuppressLint("SimpleDateFormat")
	public void setDataFromDate(String proposedDate, String directionUpDown) {
		if(proposedDate.compareTo("20140310")<0) proposedDate="20140310";
		if(proposedDate.compareTo((new SimpleDateFormat("yyyyMMdd")).format(new Date()))>0) proposedDate=(new SimpleDateFormat("yyyyMMdd")).format(new Date());
		this.deepskyObservationsListDate=proposedDate;
		Toast.makeText(getActivity(), "Fetching for proposed date: "+proposedDate, Toast.LENGTH_SHORT).show();
		Cursor mCursor=DslDatabase.execSql("SELECT deepskyObservationId AS _id, deepskyObjectName, observerName FROM deepskyObservationsList WHERE (deepskyObservationDate='"+proposedDate+"')");
        this.deepskyObservationListAdapter.changeCursor(mCursor);
/*		if(mCursor.getCount()>0) {
			this.deepskyObservationListAdapter=new SimpleCursorAdapter(MainActivity.mainActivity, R.layout.deepskyobservationslistitem, mCursor, fromColumns, toViews, 0);
			this.deepskyObservationsListDate=proposedDate;
		}
		else {
			Toast.makeText(MainActivity.mainActivity, "WP non data", Toast.LENGTH_SHORT).show();
	    	MainActivity.preferenceEditor.putInt("deepskyObservationIdToGet",(int) 75000).commit();
			LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationselectedfordetails").putExtra("org.deepskylog.deepskyObservationId", (int) 75000));
			mCursor.close();
			if(directionUpDown.equals("up")) setDataFromDate(Utils.followingDate(proposedDate),directionUpDown);
			else setDataFromDate(Utils.precedingDate(proposedDate),directionUpDown);
		}	*/
        this.deepskyObservationsList.setAdapter(this.deepskyObservationListAdapter);
	}
	
/*	public void getDeepskyObservationsListFromToId() {
        String[] fromColumns={"deepskyObjectName","observerName"};
        int[] toViews={R.id.deepskyobservationslistitemobjectname_textview_id,R.id.deepskyobservationslistitemobservername_textview_id};
        if((this.deepskyObservationListFromId.equals("0"))||(this.deepskyObservationListToId.equals("0"))) {
        	if(DeepskyObservations.deepskyObservationsMaxId==0) {
        		DeepskyObservations.broadcastDeepskyObservationsMaxIdUpdate();
        		
        		return;
        	}
        }
        Cursor mCursor=DslDatabase.execSql("SELECT deepskyObservationId AS _id, deepskyObjectName, observerName FROM deepskyObservations WHERE ((deepskyObservationId>"+this.deepskyObservationListFromId+") AND (deepskyObservationId<"+this.deepskyObservationListToId+"))");
        this.deepskyObservationListAdapter=new SimpleCursorAdapter(MainActivity.mainActivity, R.layout.deepskyobservationslistitem, mCursor, fromColumns, toViews, 0);
        this.deepskyObservationsList.setAdapter(this.deepskyObservationListAdapter);
	}
*/	
    public void ondDeepskyObservationItemClick(AdapterView<?> parent, View v, int position, long id) {
    	MainActivity.preferenceEditor.putInt("deepskyObservationIdToGet",(int) id).commit();
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationselectedfordetails").putExtra("org.deepskylog.deepskyObservationId", (int) id));
    }

}