package org.deepskylog;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CometObservationsQueryFragment extends Fragment {
 	private View cometObservationsQueryView;
    private Bundle stateBundle=null;
		
	private TextView text1_textview;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.cometObservationsQueryView=inflater.inflate(R.layout.cometobservationsqueryfragment, container, false);
		this.text1_textview=(TextView)this.cometObservationsQueryView.findViewById(R.id.ephemeridesfragment_text1_textview_id);
		if(savedInstanceState!=null) {
			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(this.stateBundle!=null) {
 			this.text1_textview.setText(this.stateBundle.getString("text1_textview"));
 		}
 		this.stateBundle=null;		
    	return this.cometObservationsQueryView;
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", this.getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
    private Bundle getStateBundle() {
        Bundle state=new Bundle();
        state.putString("text1_textview", this.text1_textview.getText().toString());
        return state;
    }

}
