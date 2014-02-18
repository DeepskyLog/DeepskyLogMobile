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
	
	private MainActivity mainActivity;
	private View mainFragmentView;
	
	Button deepsky_button;
	Button comets_button;
	Button observers_button;
	Button ephemerides_button;
	
	TextView text1_textview;
	TextView text2_textview;
	TextView text3_textview;
	
	Button command_button;
	
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    mainActivity=(MainActivity) activity;
	    mainActivity.actualFragment=this;
	    mainActivity.actualFragmentName="mainFragment";
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
    	
    	deepsky_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { mainActivity.goToFragment("deepskyFragment", MainActivity.ADD_TO_BACKSTACK); } });
    	comets_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { mainActivity.goToFragment("cometsFragment", MainActivity.ADD_TO_BACKSTACK); } });
    	observers_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { mainActivity.goToFragment("observersFragment", MainActivity.ADD_TO_BACKSTACK); } });
    	ephemerides_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { mainActivity.goToFragment("ephemeridesFragment", MainActivity.ADD_TO_BACKSTACK); } });

    	text1_textview.setText("Develop: test Text 1");
    	text2_textview.setText("Develop: test Text 1");
    	text3_textview.setText("Develop: test Text 3");
    	command_button.setText("Develop: Command Button");
     	return mainFragmentView;
	}
	@Override
	public void onResume() {
		super.onResume();
	    mainActivity.actualFragment=this;
	    mainActivity.actualFragmentName="mainFragment";
	}
}



