package org.deepskylog;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CometObjectsQueryFragment extends Fragment {
 	
	private View cometObjectsQueryView;
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
		this.cometObjectsQueryView=inflater.inflate(R.layout.cometobjectsqueryfragment, container, false);
		this.text1_textview=(TextView)this.cometObjectsQueryView.findViewById(R.id.ephemeridesfragment_text1_textview_id);
		this.text1_textview.setText("");
		if(savedInstanceState==null) {
	    }
		else {
			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(this.stateBundle!=null) {
 			this.text1_textview.setText(this.stateBundle.getString("text1_textview"));
 		}
 		this.stateBundle=null;		
    	return this.cometObjectsQueryView;
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", this.getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
    private Bundle getStateBundle() {
        Bundle state=new Bundle();
        state.putString("text1_textview", (this.text1_textview!=null?this.text1_textview.getText().toString():""));
        return state;
    }
}
