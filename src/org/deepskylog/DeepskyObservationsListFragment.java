package org.deepskylog;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Fragment;
import android.content.Intent;
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

public class DeepskyObservationsListFragment extends Fragment {
	
 	private View deepskyObservationsListView;
    private Bundle stateBundle=null;
	
	private ListView deepskyObservationsList;
	
	private SimpleCursorAdapter deepskyObservationListAdapter;
	
	private String deepskyObservationListDate;
	private String deepskyObservationListFromId;
	private String deepskyObservationListToId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.deepskyObservationsListView=inflater.inflate(R.layout.deepskyobservationslistfragment, container, false);
		if(savedInstanceState!=null) {
			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
		if(stateBundle!=null) {
 			this.deepskyObservationListDate=this.stateBundle.getString("deepskyObservationListDate",(new SimpleDateFormat("yyyyMMdd")).format(new Date()));
 			this.deepskyObservationListDate=this.stateBundle.getString("deepskyObservationListFromId","0");
 			this.deepskyObservationListDate=this.stateBundle.getString("deepskyObservationListToId","0");
 		}
		else {
			this.deepskyObservationListDate=MainActivity.preferences.getString("deepskyObservationListDate", "00000000");
			this.deepskyObservationListDate=MainActivity.preferences.getString("deepskyObservationListFromId", "0");
			this.deepskyObservationListDate=MainActivity.preferences.getString("deepskyObservationListToId", "0");
		}
		stateBundle=null;		
 		this.deepskyObservationsList=((ListView)(this.deepskyObservationsListView.findViewById(R.id.deepskyobservationslistfragment_observationlist_listview)));
 		this.deepskyObservationsList.setOnItemClickListener(new OnItemClickListener(){ public void onItemClick(AdapterView<?> parent, View v, int position, long id) { ondDeepskyObservationItemClick(parent, v, position, id); }; }); 
  		this.testSetData();
  		return deepskyObservationsListView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", this.getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
    
	private Bundle getStateBundle() {
        Bundle state=new Bundle();
        state.putString("deepskyObservationListDate", this.deepskyObservationListDate);
        state.putString("deepskyObservationListFromId", this.deepskyObservationListFromId);
        state.putString("deepskyObservationListToId", this.deepskyObservationListToId);
        return state;
    }
	
	public void testSetData() {
        String[] fromColumns={"objectName","observerName"};
        int[] toViews={R.id.deepskyobservationslistitemobjectname_textview_id,R.id.deepskyobservationslistitemobservername_textview_id};
        Cursor mCursor=DslDatabase.execSql("SELECT deepskyObservationId AS _id, objectName, observerName FROM deepskyObservations WHERE ((deepskyObservationId>0) AND (deepskyObservationId<100000))");
        this.deepskyObservationListAdapter=new SimpleCursorAdapter(MainActivity.mainActivity, R.layout.deepskyobservationslistitem, mCursor, fromColumns, toViews, 0);
        this.deepskyObservationsList.setAdapter(this.deepskyObservationListAdapter);
	}
	
	public void getDeepskyObservationsListFromToId() {
        String[] fromColumns={"objectName","observerName"};
        int[] toViews={R.id.deepskyobservationslistitemobjectname_textview_id,R.id.deepskyobservationslistitemobservername_textview_id};
        if((this.deepskyObservationListFromId.equals("0"))||(this.deepskyObservationListToId.equals("0"))) {
        	if(DeepskyObservations.deepskyObservationsMaxId==0) {
        		DeepskyObservations.broadcastDeepskyObservationsMaxIdUpdate();
        		
        		return;
        	}
        }
        Cursor mCursor=DslDatabase.execSql("SELECT deepskyObservationId AS _id, objectName, observerName FROM deepskyObservations WHERE ((deepskyObservationId>"+this.deepskyObservationListFromId+") AND (deepskyObservationId<"+this.deepskyObservationListToId+"))");
        this.deepskyObservationListAdapter=new SimpleCursorAdapter(MainActivity.mainActivity, R.layout.deepskyobservationslistitem, mCursor, fromColumns, toViews, 0);
        this.deepskyObservationsList.setAdapter(this.deepskyObservationListAdapter);
	}
	
    public void ondDeepskyObservationItemClick(AdapterView<?> parent, View v, int position, long id) {
    	MainActivity.preferenceEditor.putInt("deepskyObservationIdToGet",(int) id).commit();
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationselectedfordetails").putExtra("org.deepskylog.deepskyObservationId", (int) id));
    }

}