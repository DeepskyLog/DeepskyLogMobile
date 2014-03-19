package org.deepskylog;

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
	
    private Bundle stateBundle=null;
	
	private View deepskyObservationsListView;
	
	private ListView observationsList;
	
	private SimpleCursorAdapter mAdapter;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		deepskyObservationsListView=inflater.inflate(R.layout.deepskyobservationslistfragment, container, false);
		if(savedInstanceState==null) {
	    }
		else {
			stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(stateBundle!=null) {
 		}
 		stateBundle=null;		
 		observationsList=((ListView)(deepskyObservationsListView.findViewById(R.id.deepskyobservationslistfragment_observationlist_listview)));
 		observationsList.setOnItemClickListener(new OnItemClickListener(){ public void onItemClick(AdapterView<?> parent, View v, int position, long id) { ondDeepskyObservationItemClick(parent, v, position, id); }; }); 
  		testSetData();
  		return deepskyObservationsListView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
    
	private Bundle getStateBundle() {
        Bundle state=new Bundle();
        return state;
    }
	
	public void testSetData() {
        String[] fromColumns={"objectName","observerName"};
        int[] toViews={R.id.deepskyobservationslistitemobjectname_textview_id,R.id.deepskyobservationslistitemobservername_textview_id};
        Cursor mCursor=DslDatabase.execSql("SELECT deepskyObservationId AS _id, objectName, observerName FROM deepskyObservations WHERE ((deepskyObservationId>0) AND (deepskyObservationId<100000))");
        mAdapter=new SimpleCursorAdapter(MainActivity.mainActivity, R.layout.deepskyobservationslistitem, mCursor, fromColumns, toViews, 0);
        observationsList.setAdapter(mAdapter);
        //mCursor.close();
	}
	
    public void ondDeepskyObservationItemClick(AdapterView<?> parent, View v, int position, long id) {
    	MainActivity.preferenceEditor.putInt("deepskyObservationIdToGet",(int) id);
    	MainActivity.preferenceEditor.commit();
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationselectedfordetails").putExtra("org.deepskylog.deepskyobservationid", (int) id));
    }

}