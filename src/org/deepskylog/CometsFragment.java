package org.deepskylog;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CometsFragment extends Fragment {
	
	View cometsFragmentView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		cometsFragmentView=inflater.inflate(R.layout.cometsfragment, container, false);
     	return cometsFragmentView;
	}

}
