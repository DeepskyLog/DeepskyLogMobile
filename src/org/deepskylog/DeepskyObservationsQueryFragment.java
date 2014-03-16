package org.deepskylog;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DeepskyObservationsQueryFragment extends Fragment {
	
    private Bundle stateBundle=null;
	
	private View deepskyObservationsQueryView;
	
	private TextView text1_textview;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		deepskyObservationsQueryView=inflater.inflate(R.layout.deepskyobservationsqueryfragment, container, false);
		text1_textview=(TextView)deepskyObservationsQueryView.findViewById(R.id.ephemeridesfragment_text1_textview_id);
		if(savedInstanceState==null) {
	    }
		else {
			stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(stateBundle!=null) {
	    	text1_textview.setText(stateBundle.getString("text1_textview"));
 		}
 		stateBundle=null;		
    	return deepskyObservationsQueryView;
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
}