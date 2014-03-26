package org.deepskylog;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CometsFragment extends Fragment {
	
	private View cometsFragmentView;
    private Bundle stateBundle=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}		
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.cometsFragmentView=inflater.inflate(R.layout.cometsfragment, container, false);
 		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(this.stateBundle!=null) {
	    	//text1_textview.setText(stateBundle.getString("text1_textview"));
 		}
 		this.stateBundle=null;		
     	return cometsFragmentView;
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", this.getState());
	    super.onSaveInstanceState(savedInstanceState);
	}
    private Bundle getState() {
        Bundle state=new Bundle();
        //state.putString("text1_textview", text1_textview.getText().toString());
        return state;
    }

}
