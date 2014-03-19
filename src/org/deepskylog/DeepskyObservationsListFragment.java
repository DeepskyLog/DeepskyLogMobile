package org.deepskylog;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DeepskyObservationsListFragment extends Fragment {
	
    private Bundle stateBundle=null;
	
	private View deepskyObservationsListView;
	
	private TextView text1_textview;
	private ListView observationsList;
	
	private SimpleCursorAdapter mAdapter;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		deepskyObservationsListView=inflater.inflate(R.layout.deepskyobservationslistfragment, container, false);
		text1_textview=(TextView)deepskyObservationsListView.findViewById(R.id.ephemeridesfragment_text1_textview_id);
		if(savedInstanceState==null) {
	    }
		else {
			stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(stateBundle!=null) {
	    	text1_textview.setText(stateBundle.getString("text1_textview"));
 		}
 		stateBundle=null;		
 		observationsList=((ListView)(deepskyObservationsListView.findViewById(R.id.deepskyobservationslistfragment_observationlist_listview)));
 		//Get data
 		observationsList.setOnItemClickListener(new OnItemClickListener(){ public void onItemClick(AdapterView<?> parent, View v, int position, long id) { ondDeepskyObservationItemClick(parent, v, position, id); }; }); 
        /*ProgressBar progressBar = new ProgressBar(MainActivity.mainActivity);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);*/
 		testSetData();

   	return deepskyObservationsListView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
    
	private Bundle getStateBundle() {
        Bundle state =new Bundle();
        state.putString("text1_textview", text1_textview.getText().toString());
        return state;
    }
	
	public void testSetData() {
        String[] fromColumns={"objectName","observerName"};
        int[] toViews={R.id.deepskyobservationslistitemobjectname_textview_id,R.id.deepskyobservationslistitemobservername_textview_id};
        Cursor mCursor=DslDatabase.execSql("SELECT deepskyObservationId AS _id, objectName, observerName FROM deepskyObservations WHERE ((deepskyObservationId>75770) AND (deepskyObservationId<75780))");
        mAdapter=new SimpleCursorAdapter(MainActivity.mainActivity, R.layout.deepskyobservationslistitem, mCursor, fromColumns, toViews, 0);
        observationsList.setAdapter(mAdapter);
        //mCursor.close();
	}
	
    public void ondDeepskyObservationItemClick(AdapterView<?> parent, View v, int position, long id) {
    	MainActivity.preferenceEditor.putInt("deepskyObservationIdToGet",(int) id);
    	MainActivity.preferenceEditor.commit();
    	MainActivity.goToFragment("deepskyObservationsDetailsFragment", MainActivity.ADD_TO_BACKSTACK);
    }
    


}