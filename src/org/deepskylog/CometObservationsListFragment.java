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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CometObservationsListFragment extends Fragment {
	
 	private View cometObservationsListView;
    private Bundle stateBundle=null;
	
	private ListView cometObservationsList;
	private TextView text1_textview;
	
	private SimpleCursorAdapter cometObservationListAdapter;
	
	private String cometObservationsListDate="";
	private String cometObservationsListProposedDate="";
	private Integer cometObservationsListId=1;
	
	private String direction="down";

	private BroadcastReceiver broadcastCometObservationsListReceiver=new BroadcastReceiver() {  @Override  public void onReceive(Context context, Intent intent) { onReceiveCometObservationsList(context, intent); } };
	private BroadcastReceiver broadcastCometObservationsListDaysReceiver=new BroadcastReceiver() {  @Override  public void onReceive(Context context, Intent intent) { onReceiveCometObservationsListDays(context, intent); } };

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
		this.cometObservationsListView=inflater.inflate(R.layout.cometobservationslistfragment, container, false);
		this.numberOfIdsInList=10;
		this.text1_textview=(TextView)this.cometObservationsListView.findViewById(R.id.cometobservationslistfragment_text1_textview_id);
		this.text1_textview.setText("Fetching list");
		this.cometObservationsList=((ListView)(this.cometObservationsListView.findViewById(R.id.cometobservationslistfragment_observationlist_listview)));
 		this.cometObservationsList.setOnItemClickListener(new OnItemClickListener(){ public void onItemClick(AdapterView<?> parent, View v, int position, long id) { onCometObservationItemClick(parent, v, position, id); }; }); 
		this.cometObservationsList.setOnTouchListener(new OnSwipeTouchListener(MainActivity.mainActivity) {
 		    public void onSwipeTop() { }
 		    public void onSwipeRight() { goRight(); }
 		    public void onSwipeLeft() { goLeft(); }
 		    public void onSwipeBottom() {  }
		});
		if(savedInstanceState!=null) {
			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
		if(stateBundle!=null) {
			this.cometObservationsListDate=this.stateBundle.getString("cometObservationsListDate",(new SimpleDateFormat("yyyyMMdd")).format(new Date()));
			this.cometObservationsListProposedDate=this.stateBundle.getString("cometObservationsListProposedDate",(new SimpleDateFormat("yyyyMMdd")).format(new Date()));
			this.cometObservationsListId=this.stateBundle.getInt("cometObservationsListId");
 			this.text1_textview.setText(this.stateBundle.getString("text1_textview"));
			this.direction=this.stateBundle.getString("direction","down");
  		}
		else {
			this.cometObservationsListDate=MainActivity.preferences.getString("cometObservationsListDate", (new SimpleDateFormat("yyyyMMdd")).format(new Date()));
			this.cometObservationsListProposedDate=MainActivity.preferences.getString("cometObservationsListProposedDate", (new SimpleDateFormat("yyyyMMdd")).format(new Date()));
			this.cometObservationsListId=MainActivity.preferences.getInt("cometObservationsListId", CometObservations.cometObservationsMaxId);
			this.direction="down";
		}
		if(this.cometObservationsListId==0) this.cometObservationsListId=CometObservations.cometObservationsMaxId;
		stateBundle=null;
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastCometObservationsListReceiver, new IntentFilter("org.deepskylog.broadcastcometobservationslist"));
  		LocalBroadcastManager.getInstance(MainActivity.mainActivity).registerReceiver(this.broadcastCometObservationsListDaysReceiver, new IntentFilter("org.deepskylog.broadcastcometobservationslistdays"));
		return cometObservationsListView;
	}
	
	@Override 
	public void onResume() {
		super.onResume();
		if(CometObservationsFragment.sortMode.equals("By Date"))
 			this.gotoDateOrPrevious();
 		else
 			gotoId();
	}
	
	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastCometObservationsListReceiver);
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).unregisterReceiver(this.broadcastCometObservationsListDaysReceiver);
		MainActivity.preferenceEditor.putString("cometObservationsListDate", this.cometObservationsListDate);
		MainActivity.preferenceEditor.putString("cometObservationsListProposedDate", this.cometObservationsListProposedDate);
		MainActivity.preferenceEditor.putInt("cometObservationsListId", this.cometObservationsListId);
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
        state.putString("cometObservationsListDate", this.cometObservationsListDate);
        state.putString("cometObservationsListProposedDateDate", this.cometObservationsListProposedDate);
        state.putInt("cometObservationsListId", this.cometObservationsListId);
        state.putString("text1_textview",( this.text1_textview!=null? this.text1_textview.getText().toString():""));
        state.putString("direction", this.direction);
        return state;
    }
	
	private void goRight() {
 		if(CometObservationsFragment.sortMode.equals("By Date")) {
 			if(Integer.valueOf(this.cometObservationsListDate)>19670727)
 				this.cometObservationsListProposedDate=Utils.precedingDate(this.cometObservationsListDate);
 			else
 				Toast.makeText(MainActivity.mainActivity, "No observations before 27 July 1967", Toast.LENGTH_LONG).show();
 			this.gotoDateOrPrevious();
 		}
 		else
 			if(this.cometObservationsListId>numberOfIdsInList) {
 	 			this.cometObservationsListId-=this.numberOfIdsInList;
 	 			gotoId();
 			}
 			else
 				Toast.makeText(MainActivity.mainActivity, "You are at the first observation.", Toast.LENGTH_LONG).show();
	}
	
	@SuppressLint("SimpleDateFormat")
	private void goLeft() {
		if(CometObservationsFragment.sortMode.equals("By Date")) {
 			if(Integer.valueOf(this.cometObservationsListDate)<Integer.valueOf((new SimpleDateFormat("yyyyMMdd")).format(new Date())))
 				this.cometObservationsListProposedDate=Utils.followingDate(this.cometObservationsListDate);
 			else
 				Toast.makeText(MainActivity.mainActivity, "No observations after today", Toast.LENGTH_LONG).show();
 			this.gotoDateOrNext();
 		}
 		else
 			if(this.cometObservationsListId<CometObservations.cometObservationsMaxId) {
 	 			this.cometObservationsListId+=this.numberOfIdsInList;
 	 			gotoId();
 			}
 			else
 				Toast.makeText(MainActivity.mainActivity, "You are at the latest observation.", Toast.LENGTH_LONG).show();
	}
	
	private void gotoId() {
		Cursor mCursor=DslDatabase.execSql("SELECT cometObservationId AS _id, cometObjectName, observerName FROM cometObservationsList WHERE ((cometObservationId<="+String.valueOf(this.cometObservationsListId)+")AND(cometObservationId>="+String.valueOf(this.cometObservationsListId-this.numberOfIdsInList+1)+")) ORDER BY cometObservationId DESC");
		this.text1_textview.setText("Observations "+String.valueOf(this.cometObservationsListId-this.numberOfIdsInList+1)+" - "+String.valueOf(this.cometObservationsListId)+" / "+CometObservations.cometObservationsMaxId);
		if(mCursor.getCount()>0) {
			//Toast.makeText(MainActivity.mainActivity, "Internal lookup for "+String.valueOf(this.cometObservationsListId-9)+" to "+String.valueOf(this.cometObservationsListId)+" gave "+mCursor.getCount()+" results.", Toast.LENGTH_LONG).show();
			setData(mCursor);
			//if(mCursor.getCount()<10)
			//	CometObservations.broadcastCometObservationsListFromIdToId(String.valueOf(this.cometObservationsListId-9),String.valueOf(this.cometObservationsListId));
		}
		else {
			//Toast.makeText(MainActivity.mainActivity, "DSL lookup for "+String.valueOf(this.cometObservationsListId-9)+" to "+String.valueOf(this.cometObservationsListId), Toast.LENGTH_LONG).show();
			CometObservations.broadcastCometObservationsListFromIdToId(String.valueOf(this.cometObservationsListId-this.numberOfIdsInList+1),String.valueOf(this.cometObservationsListId));
		}
	}

	private void gotoDateOrPrevious() {
		this.direction="down";
		Cursor dCursor=DslDatabase.execSql("SELECT cometObservationsListDate FROM cometObservationsListDays WHERE (cometObservationsListDate<='"+this.cometObservationsListProposedDate+"') ORDER BY cometObservationsListDate DESC");
		if(dCursor.getCount()>0) {
			dCursor.moveToFirst();
			this.cometObservationsListDate=dCursor.getString(0);
			Cursor mCursor=DslDatabase.execSql("SELECT cometObservationId AS _id, cometObjectName, observerName FROM cometObservationsList WHERE (cometObservationDate='"+String.valueOf(this.cometObservationsListDate)+"') ORDER BY cometObservationId DESC");
			this.text1_textview.setText("Observations of "+this.cometObservationsListDate);
			setData(mCursor);
			CometObservations.broadcastCometObservationsListFromDateToDate(this.cometObservationsListDate,this.cometObservationsListDate);
		}
		else {
			//Toast.makeText(MainActivity.mainActivity, "List previous month lookup for "+this.cometObservationsListDate, Toast.LENGTH_LONG).show();
			CometObservations.broadcastCometObservationsListDaysFromDateToDate(Utils.precedingYear(this.cometObservationsListProposedDate), this.cometObservationsListProposedDate);
		}
	}
	
	private void gotoDate() {
		Cursor mCursor=DslDatabase.execSql("SELECT cometObservationId AS _id, cometObjectName, observerName FROM cometObservationsList WHERE (cometObservationDate='"+String.valueOf(this.cometObservationsListDate)+"') ORDER BY cometObservationId DESC");
		this.text1_textview.setText("Observation of "+this.cometObservationsListDate);
		setData(mCursor);
	}

	private void gotoDateOrNext() {
		this.direction="up";
		Cursor dCursor=DslDatabase.execSql("SELECT cometObservationsListDate FROM cometObservationsListDays WHERE (cometObservationsListDate>='"+this.cometObservationsListProposedDate+"') ORDER BY cometObservationsListDate ASC");
		if(dCursor.getCount()>0) {
			dCursor.moveToFirst();
			this.cometObservationsListDate=dCursor.getString(0);
			Cursor mCursor=DslDatabase.execSql("SELECT cometObservationId AS _id, cometObjectName, observerName FROM cometObservationsList WHERE (cometObservationDate='"+String.valueOf(this.cometObservationsListDate)+"') ORDER BY cometObservationId DESC");
			this.text1_textview.setText("Observations of "+this.cometObservationsListDate);
			setData(mCursor);
			CometObservations.broadcastCometObservationsListFromDateToDate(this.cometObservationsListDate,this.cometObservationsListDate);
		}
		else {
			//Toast.makeText(MainActivity.mainActivity, "List next month lookup for "+this.cometObservationsListDate, Toast.LENGTH_LONG).show();
		    CometObservations.broadcastCometObservationsListDaysFromDateToDate(this.cometObservationsListProposedDate,Utils.followingYear(this.cometObservationsListProposedDate));
		}
	}
		
	private void onReceiveCometObservationsList(Context context, Intent intent) {
		if(CometObservationsFragment.sortMode.equals("By Date"))
 			this.gotoDate();
 		else
 			gotoId();
	}
	
	private void onReceiveCometObservationsListDays(Context context, Intent intent) {
		//Toast.makeText(MainActivity.mainActivity, "List looked up for "+this.cometObservationsListProposedDate, Toast.LENGTH_LONG).show();
		Cursor dCursor;
		if(this.direction.equals("down")) dCursor=DslDatabase.execSql("SELECT cometObservationsListDate FROM cometObservationsListDays WHERE (cometObservationsListDate<='"+this.cometObservationsListProposedDate+"') ORDER BY cometObservationsListDate DESC");
		else dCursor=DslDatabase.execSql("SELECT cometObservationsListDate FROM cometObservationsListDays WHERE (cometObservationsListDate>='"+this.cometObservationsListProposedDate+"') ORDER BY cometObservationsListDate ASC");
		if(dCursor.getCount()>0) {
			//Toast.makeText(MainActivity.mainActivity, "List success looked up for "+this.cometObservationsListProposedDate, Toast.LENGTH_LONG).show();
			dCursor.moveToFirst();
			this.cometObservationsListDate=dCursor.getString(0);
			CometObservations.broadcastCometObservationsListFromDateToDate(this.cometObservationsListDate,this.cometObservationsListDate);
		}
		else
			if(this.direction.equals("down")) Toast.makeText(MainActivity.mainActivity, "No more observations before "+this.cometObservationsListDate, Toast.LENGTH_LONG).show();
			else Toast.makeText(MainActivity.mainActivity, "No more observations after "+this.cometObservationsListDate, Toast.LENGTH_LONG).show();
	}
	

	public void setData(Cursor mCursor) {
        if(mCursor.getCount()>0) {
            String[] fromColumns={"cometObjectName","observerName"};
            int[] toViews={R.id.cometobservationslistitemobjectname_textview_id,R.id.cometobservationslistitemobservername_textview_id};
    		this.cometObservationListAdapter=new SimpleCursorAdapter(MainActivity.mainActivity, R.layout.cometobservationslistitem, mCursor, fromColumns, toViews, 0);
            this.cometObservationsList.setAdapter(this.cometObservationListAdapter);
		}
		//else
		//	Toast.makeText(MainActivity.mainActivity, "No data for list", Toast.LENGTH_LONG).show();
	}

    public void onCometObservationItemClick(AdapterView<?> parent, View v, int position, long id) {
    	MainActivity.preferenceEditor.putInt("cometObservationIdToGet",(int) id).commit();
		LocalBroadcastManager.getInstance(MainActivity.mainActivity).sendBroadcast(new Intent("org.deepskylog.broadcastcometobservationselected").putExtra("org.deepskylog.cometObservationId", (int) id));
    }


}
