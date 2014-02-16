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
import android.widget.Toast;

public class MainFragment extends Fragment {
	
	private MainActivity mainActivity;
	private View mainFragmentView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mainFragmentView=inflater.inflate(R.layout.mainfragment, container, false);    	
     	return mainFragmentView;
	}
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    mainActivity=(MainActivity) activity;
	    mainActivity.actualFragment=this;
	    mainActivity.actualFragmentName="mainFragment";
	}
	@Override
	public void onResume() {
		super.onResume();
	    mainActivity.actualFragment=this;
	    mainActivity.actualFragmentName="mainFragment";
	}
}



