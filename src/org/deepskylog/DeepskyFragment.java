package org.deepskylog;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DeepskyFragment extends Fragment {

	private Bundle savedState = null;

	private View deepskyFragmentView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		deepskyFragmentView=inflater.inflate(R.layout.deepskyfragment, container, false);
 		if(savedInstanceState==null) {
	    }
		else {
			savedState=savedInstanceState.getBundle("savedState");
		}
 		if(savedState!=null) {
	    	//text1_textview.setText(savedState.getString("text1_textview"));
 		}
 		savedState=null;		
     	return deepskyFragmentView;
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    savedInstanceState.putBundle("savedState", saveState());
	}
    private Bundle saveState() {
        Bundle state = new Bundle();
        //state.putString("text1_textview", text1_textview.getText().toString());
        return state;
    }

}
