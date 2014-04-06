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
	
    private Bundle stateBundle=null;
	
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 		if(savedInstanceState!=null) {
 			stateBundle=savedInstanceState.getBundle("stateBundle");
		}		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mainFragmentView=inflater.inflate(R.layout.mainfragment, container, false);
		this.deepsky_button=((Button) this.mainFragmentView.findViewById(R.id.mainfragment_ds_button_id));
		this.comets_button=((Button) this.mainFragmentView.findViewById(R.id.mainfragment_com_button_id));
		this.observers_button=((Button) this.mainFragmentView.findViewById(R.id.mainfragment_obs_button_id));
		this.ephemerides_button=((Button) this.mainFragmentView.findViewById(R.id.mainfragment_eph_button_id));
		this.text1_textview=((TextView) this.mainFragmentView.findViewById(R.id.mainfragment_text1_textview_id));
		this.text1_textview.setText("");
		this.text2_textview=((TextView) this.mainFragmentView.findViewById(R.id.mainfragment_text2_textview_id));
		this.text2_textview.setText("");
		this.text3_textview=((TextView) this.mainFragmentView.findViewById(R.id.mainfragment_text3_textview_id));
		this.text3_textview.setText("");
		this.command_button=((Button) this.mainFragmentView.findViewById(R.id.mainfragment_command_button_id));
   
		this.deepsky_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { MainActivity.goToFragment("deepskyFragment", MainActivity.ADD_TO_BACKSTACK); } });
		this.comets_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { MainActivity.goToFragment("cometsFragment", MainActivity.ADD_TO_BACKSTACK); } });
		this.observers_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { MainActivity.goToFragment("observersFragment", MainActivity.ADD_TO_BACKSTACK); } });
		this.ephemerides_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { MainActivity.goToFragment("ephemeridesFragment", MainActivity.ADD_TO_BACKSTACK); } });
		this.command_button.setText("Develop: Command Button - for debug purposes only");
		this.command_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { commandButtonOnClick(); } });
 		if(savedInstanceState==null) {
	    }
		else {
			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(this.stateBundle!=null) {
 			this.text1_textview.setText(this.stateBundle.getString("text1_textview"));
 			this.text2_textview.setText(this.stateBundle.getString("text2_textview"));
	    	this.text3_textview.setText(this.stateBundle.getString("text3_textview"));
 		}
 		stateBundle=null;		
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
	    savedInstanceState.putBundle("stateBundle", this.getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
	
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private Bundle getStateBundle() {
        if(this.stateBundle!=null) return stateBundle;
        else {
			Bundle state=new Bundle();
	        state.putString("text1_textview", this.text1_textview.getText().toString());
	        state.putString("text2_textview", this.text2_textview.getText().toString());
	        state.putString("text3_textview", this.text3_textview.getText().toString());
	        return state;
        }
    }
    
    public void setText(String theText) {
    	this.text3_textview.setText(this.text2_textview.getText());
    	this.text2_textview.setText(this.text1_textview.getText());
    	this.text1_textview.setText(theText);
    }
    
    private static void commandButtonOnClick() {
    	MainActivity.preferenceEditor.putBoolean("firstrun", true).commit();
    	MainActivity.checkFirstRun();
    }
}



