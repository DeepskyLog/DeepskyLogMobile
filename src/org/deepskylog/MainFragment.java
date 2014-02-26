package org.deepskylog;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainFragment extends Fragment {
	
    private Bundle savedState = null;
	
	private View mainFragmentView;
	
	private Button deepsky_button;
	private Button comets_button;
	private Button observers_button;
	private Button ephemerides_button;
	
	private TextView text1_textview;
	private TextView text2_textview;
	private TextView text3_textview;
	
	private Button command_button;
	
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    MainActivity.actualFragment=this;
	    MainActivity.actualFragmentName="mainFragment";
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainFragmentView=inflater.inflate(R.layout.mainfragment, container, false);
    	deepsky_button=((Button) mainFragmentView.findViewById(R.id.mainfragment_ds_button_id));
    	comets_button=((Button) mainFragmentView.findViewById(R.id.mainfragment_com_button_id));
    	observers_button=((Button) mainFragmentView.findViewById(R.id.mainfragment_obs_button_id));
    	ephemerides_button=((Button) mainFragmentView.findViewById(R.id.mainfragment_eph_button_id));
    	text1_textview=((TextView) mainFragmentView.findViewById(R.id.mainfragment_text1_textview_id));
    	text2_textview=((TextView) mainFragmentView.findViewById(R.id.mainfragment_text2_textview_id));
    	text3_textview=((TextView) mainFragmentView.findViewById(R.id.mainfragment_text3_textview_id));
    	command_button=((Button) mainFragmentView.findViewById(R.id.mainfragment_command_button_id));
    	
    	deepsky_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { MainActivity.goToFragment("deepskyFragment", MainActivity.ADD_TO_BACKSTACK); } });
    	comets_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { MainActivity.goToFragment("cometsFragment", MainActivity.ADD_TO_BACKSTACK); } });
    	observers_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { MainActivity.goToFragment("observersFragment", MainActivity.ADD_TO_BACKSTACK); } });
    	ephemerides_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { MainActivity.goToFragment("ephemeridesFragment", MainActivity.ADD_TO_BACKSTACK); } });
    	command_button.setText("Develop: Command Button - Test firstRun");
    	command_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { commandButtonOnClick(); } });
 		if(savedInstanceState==null) {
	    }
		else {
			savedState=savedInstanceState.getBundle("savedState");
		}
 		if(savedState!=null) {
	    	text1_textview.setText(savedState.getString("text1_textview"));
	    	text2_textview.setText(savedState.getString("text2_textview"));
	    	text3_textview.setText(savedState.getString("text3_textview"));
 		}
 		savedState=null;		
		MainActivity.actionBar.setSubtitle(MainActivity.resources.getString(R.string.actionbar_connectivity_N));
		return mainFragmentView;
	}
	@Override
	public void onResume() {
		super.onResume();
	    MainActivity.actualFragment=this;
	    MainActivity.actualFragmentName="mainFragment";
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    savedInstanceState.putBundle("savedState", saveState());
	}
	
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        savedState=saveState();
    }

    private Bundle saveState() {
        Bundle state = new Bundle();
        state.putString("text1_textview", text1_textview.getText().toString());
        state.putString("text2_textview", text2_textview.getText().toString());
        state.putString("text3_textview", text3_textview.getText().toString());
        return state;
    }
    
    public void setText(String theText) {
		text3_textview.setText(text2_textview.getText());
		text2_textview.setText(text1_textview.getText());
		text1_textview.setText(theText);
    }
    
    private static void commandButtonOnClick() {
    	MainActivity.preferenceEditor.putBoolean("firstrun", true).commit();
    	MainActivity.checkFirstRun();
    }
}



