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
import android.widget.TextView;
import android.widget.Toast;

public class DeepskyObservationsListFragment extends Fragment {
	
 	private View deepskyObservationsListView;
    private Bundle stateBundle=null;
	
	private ListView deepskyObservationsList;
	private TextView text1_textview;
	
	private SimpleCursorAdapter deepskyObservationListAdapter;
	
	private String deepskyObservationsListDate;
	private String deepskyObservationsListProposedDate;
	private Integer deepskyObservationsListId;
	
	private String direction;

	private BroadcastReceiver broadcastDeepskyObservationsListReceiver=new BroadcastReceiver() {  @Override  public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationsList(context, intent); } };
	private BroadcastReceiver broadcastDeepskyObservationsListDaysReceiver=new BroadcastReceiver() {  @Override  public void onReceive(Context context, Intent intent) { onReceiveDeepskyObservationsListDays(context, intent); } };

	private int numberOfIdsInList; 
	
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
		this.numberOfIdsInList=10;
		this.text1_textview=(TextView)this.deepskyObservationsListView.findViewById(R.id.deepskyobservationslistfragment_text1_textview_id);
		this.text1_textview.setText("Fetching list");
		this.deepskyObservationsList=((ListView)(this.deepskyObservationsListView.findViewById(R.id.deepskyobservationslistfragment_observationlist_listview)));
 		this.deepskyObservationsList.setOnItemClickListener(new OnItemClickListener(){ public void onItemClick(AdapterView<?> parent, View v, int position, long id) { ondDeepskyObservationItemClick(parent, v, position, id); }; }); 
		this.deepskyObservationsList.setOnTouchListener(new OnSwipeTouchListener(MainActivity.mainActivity) {
 		    public void onSwipeTop() { }
 		    public void onSwipeRight() { goRight(); }
 		    public void onSwipeLeft() { goLeft(); }
 		    public void onSwipeBottom() {  }
		});
		if(savedInstanceState!=null) {
			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
		if(stateBundle!=null) {
			this.deepskyObservationsListDate=this.stateBundle.getString("deepskyObservationsListDate",(new SimpleDateFormat("yyyyMMdd")).format(new Date()));
			this.deepskyObservationsListProposedDate=this.stateBundle.getString("deepskyObservationsListProposedDate",(new SimpleDateFormat("yyyyMMdd")).format(new Date()));
			this.deepskyObservationsListId=this.stateBundle.getInt("deepskyObservationsListId");
 			this.text1_textview.setText(this.stateBundle.getString("text1_textview"));
			this.direction=this.stateBundle.getString("direction","down");
  		}
		else {
			this.deepskyObservationsListDate=MainActivity.preferences.getString("deepskyObservationsListDate", (new SimpleDateFormat("yyyyMMdd")).format(new Date()));
			this.deepskyObservationsListProposedDate=MainActivity.preferences.getString("deepskyObservationsListProposedDate", (new SimpleDateFormat("yyyyMMdd")).format(new Date()));
			this.deepskyObservationsListId=MainActivity.preferences.getInt("deepskyObservationsListId", DeepskyObservations.deepskyObservationsMaxId);
			this.direction="down";
		}
		if(this.deepskyObservationsListId==0) this.deepskyObservationsListId=DeepskyObservations.deepskyObservationsMaxId;
		stateBundle=null;
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationsListReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationslist"));
  		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastDeepskyObservationsListDaysReceiver, new IntentFilter("org.deepskylog.broadcastdeepskyobservationslistdays"));
		return deepskyObservationsListView;
	}
	
	@Override 
	public void onResume() {
		super.onResume();
		if(DeepskyObservationsFragment.sortMode.equals("By Date"))
 			this.gotoDateOrPrevious();
 		else
 			gotoId();
	}
	
	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationsListReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastDeepskyObservationsListDaysReceiver);
		MainActivity.preferenceEditor.putString("deepskyObservationsListDate", this.deepskyObservationsListDate);
		MainActivity.preferenceEditor.putString("deepskyObservationsListProposedDate", this.deepskyObservationsListProposedDate);
		MainActivity.preferenceEditor.putInt("deepskyObservationsListId", this.deepskyObservationsListId);
		MainActivity.preferenceEditor.commit();
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
        state.putString("deepskyObservationsListProposedDateDate", this.deepskyObservationsListProposedDate);
        state.putInt("deepskyObservationsListId", this.deepskyObservationsListId);
        state.putString("text1_textview", this.text1_textview.getText().toString());
        state.putString("direction", this.direction);
        return state;
    }
	
	private void goRight() {
 		if(DeepskyObservationsFragment.sortMode.equals("By Date")) {
 			if(Integer.valueOf(this.deepskyObservationsListDate)>19670727)
 				this.deepskyObservationsListProposedDate=Utils.precedingDate(this.deepskyObservationsListDate);
 			else
 				Toast.makeText(MainActivity.mainActivity, "No observations before 27 July 1967", Toast.LENGTH_LONG).show();
 			this.gotoDateOrPrevious();
 		}
 		else
 			if(this.deepskyObservationsListId>numberOfIdsInList) {
 	 			this.deepskyObservationsListId-=this.numberOfIdsInList;
 	 			gotoId();
 			}
 			else
 				Toast.makeText(MainActivity.mainActivity, "You are at the first observation.", Toast.LENGTH_LONG).show();
	}
	
	@SuppressLint("SimpleDateFormat")
	private void goLeft() {
		if(DeepskyObservationsFragment.sortMode.equals("By Date")) {
 			if(Integer.valueOf(this.deepskyObservationsListDate)<Integer.valueOf((new SimpleDateFormat("yyyyMMdd")).format(new Date())))
 				this.deepskyObservationsListProposedDate=Utils.followingDate(this.deepskyObservationsListDate);
 			else
 				Toast.makeText(MainActivity.mainActivity, "No observations after today", Toast.LENGTH_LONG).show();
 			this.gotoDateOrNext();
 		}
 		else
 			if(this.deepskyObservationsListId<DeepskyObservations.deepskyObservationsMaxId) {
 	 			this.deepskyObservationsListId+=this.numberOfIdsInList;
 	 			gotoId();
 			}
 			else
 				Toast.makeText(MainActivity.mainActivity, "You are at the latest observation.", Toast.LENGTH_LONG).show();
	}
	
	private void gotoId() {
		Cursor mCursor=DslDatabase.execSql("SELECT deepskyObservationId AS _id, deepskyObjectName, observerName FROM deepskyObservationsList WHERE ((deepskyObservationId<="+String.valueOf(this.deepskyObservationsListId)+")AND(deepskyObservationId>="+String.valueOf(this.deepskyObservationsListId-this.numberOfIdsInList+1)+")) ORDER BY deepskyObservationId DESC");
		this.text1_textview.setText("Observations "+String.valueOf(this.deepskyObservationsListId-this.numberOfIdsInList+1)+" - "+String.valueOf(this.deepskyObservationsListId));
		if(mCursor.getCount()>0) {
			//Toast.makeText(MainActivity.mainActivity, "Internal lookup for "+String.valueOf(this.deepskyObservationsListId-9)+" to "+String.valueOf(this.deepskyObservationsListId)+" gave "+mCursor.getCount()+" results.", Toast.LENGTH_LONG).show();
			setData(mCursor);
			//if(mCursor.getCount()<10)
			//	DeepskyObservations.broadcastDeepskyObservationsListFromIdToId(String.valueOf(this.deepskyObservationsListId-9),String.valueOf(this.deepskyObservationsListId));
		}
		else {
			//Toast.makeText(MainActivity.mainActivity, "DSL lookup for "+String.valueOf(this.deepskyObservationsListId-9)+" to "+String.valueOf(this.deepskyObservationsListId), Toast.LENGTH_LONG).show();
			DeepskyObservations.broadcastDeepskyObservationsListFromIdToId(String.valueOf(this.deepskyObservationsListId-this.numberOfIdsInList+1),String.valueOf(this.deepskyObservationsListId));
		}
	}

	private void gotoDateOrPrevious() {
		this.direction="down";
		Cursor dCursor=DslDatabase.execSql("SELECT deepskyObservationsListDate FROM deepskyObservationsListDays WHERE (deepskyObservationsListDate<='"+this.deepskyObservationsListProposedDate+"') ORDER BY deepskyObservationsListDate DESC");
		if(dCursor.getCount()>0) {
			dCursor.moveToFirst();
			this.deepskyObservationsListDate=dCursor.getString(0);
			Cursor mCursor=DslDatabase.execSql("SELECT deepskyObservationId AS _id, deepskyObjectName, observerName FROM deepskyObservationsList WHERE (deepskyObservationDate='"+String.valueOf(this.deepskyObservationsListDate)+"') ORDER BY deepskyObservationId DESC");
			this.text1_textview.setText("Observations of "+this.deepskyObservationsListDate);
			setData(mCursor);
			DeepskyObservations.broadcastDeepskyObservationsListFromDateToDate(this.deepskyObservationsListDate,this.deepskyObservationsListDate);
		}
		else {
			//Toast.makeText(MainActivity.mainActivity, "List previous month lookup for "+this.deepskyObservationsListDate, Toast.LENGTH_LONG).show();
			DeepskyObservations.broadcastDeepskyObservationsListDaysFromDateToDate(Utils.precedingMonth(this.deepskyObservationsListProposedDate), this.deepskyObservationsListProposedDate);
		}
	}
	
	private void gotoDate() {
		Cursor mCursor=DslDatabase.execSql("SELECT deepskyObservationId AS _id, deepskyObjectName, observerName FROM deepskyObservationsList WHERE (deepskyObservationDate='"+String.valueOf(this.deepskyObservationsListDate)+"') ORDER BY deepskyObservationId DESC");
		this.text1_textview.setText("Observation of "+this.deepskyObservationsListDate);
		setData(mCursor);
	}

	private void gotoDateOrNext() {
		this.direction="up";
		Cursor dCursor=DslDatabase.execSql("SELECT deepskyObservationsListDate FROM deepskyObservationsListDays WHERE (deepskyObservationsListDate>='"+this.deepskyObservationsListProposedDate+"') ORDER BY deepskyObservationsListDate ASC");
		if(dCursor.getCount()>0) {
			dCursor.moveToFirst();
			this.deepskyObservationsListDate=dCursor.getString(0);
			Cursor mCursor=DslDatabase.execSql("SELECT deepskyObservationId AS _id, deepskyObjectName, observerName FROM deepskyObservationsList WHERE (deepskyObservationDate='"+String.valueOf(this.deepskyObservationsListDate)+"') ORDER BY deepskyObservationId DESC");
			this.text1_textview.setText("Observations of "+this.deepskyObservationsListDate);
			setData(mCursor);
			DeepskyObservations.broadcastDeepskyObservationsListFromDateToDate(this.deepskyObservationsListDate,this.deepskyObservationsListDate);
		}
		else {
			//Toast.makeText(MainActivity.mainActivity, "List next month lookup for "+this.deepskyObservationsListDate, Toast.LENGTH_LONG).show();
		    DeepskyObservations.broadcastDeepskyObservationsListDaysFromDateToDate(this.deepskyObservationsListProposedDate,Utils.followingMonth(this.deepskyObservationsListProposedDate));
		}
	}
		
	private void onReceiveDeepskyObservationsList(Context context, Intent intent) {
		if(DeepskyObservationsFragment.sortMode.equals("By Date"))
 			this.gotoDate();
 		else
 			gotoId();
	}
	
	private void onReceiveDeepskyObservationsListDays(Context context, Intent intent) {
		//Toast.makeText(MainActivity.mainActivity, "List looked up for "+this.deepskyObservationsListProposedDate, Toast.LENGTH_LONG).show();
		Cursor dCursor;
		if(this.direction.equals("down")) dCursor=DslDatabase.execSql("SELECT deepskyObservationsListDate FROM deepskyObservationsListDays WHERE (deepskyObservationsListDate<='"+this.deepskyObservationsListProposedDate+"') ORDER BY deepskyObservationsListDate DESC");
		else dCursor=DslDatabase.execSql("SELECT deepskyObservationsListDate FROM deepskyObservationsListDays WHERE (deepskyObservationsListDate>='"+this.deepskyObservationsListProposedDate+"') ORDER BY deepskyObservationsListDate ASC");
		if(dCursor.getCount()>0) {
			//Toast.makeText(MainActivity.mainActivity, "List success looked up for "+this.deepskyObservationsListProposedDate, Toast.LENGTH_LONG).show();
			dCursor.moveToFirst();
			this.deepskyObservationsListDate=dCursor.getString(0);
			DeepskyObservations.broadcastDeepskyObservationsListFromDateToDate(this.deepskyObservationsListDate,this.deepskyObservationsListDate);
		}
		else
			if(this.direction.equals("down")) Toast.makeText(MainActivity.mainActivity, "No more observations before "+this.deepskyObservationsListDate, Toast.LENGTH_LONG).show();
			else Toast.makeText(MainActivity.mainActivity, "No more observations after "+this.deepskyObservationsListDate, Toast.LENGTH_LONG).show();
	}
	

	public void setData(Cursor mCursor) {
        if(mCursor.getCount()>0) {
            String[] fromColumns={"deepskyObjectName","observerName"};
            int[] toViews={R.id.deepskyobservationslistitemobjectname_textview_id,R.id.deepskyobservationslistitemobservername_textview_id};
    		this.deepskyObservationListAdapter=new SimpleCursorAdapter(MainActivity.mainActivity, R.layout.deepskyobservationslistitem, mCursor, fromColumns, toViews, 0);
            this.deepskyObservationsList.setAdapter(this.deepskyObservationListAdapter);
		}
		//else
		//	Toast.makeText(MainActivity.mainActivity, "No data for list", Toast.LENGTH_LONG).show();
	}

    public void ondDeepskyObservationItemClick(AdapterView<?> parent, View v, int position, long id) {
    	MainActivity.preferenceEditor.putInt("deepskyObservationIdToGet",(int) id).commit();
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastdeepskyobservationselectedfordetails").putExtra("org.deepskylog.deepskyObservationId", (int) id));
    }

}