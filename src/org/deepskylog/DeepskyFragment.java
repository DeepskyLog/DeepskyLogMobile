package org.deepskylog;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DeepskyFragment extends Fragment {
	
	View deepskyFragmentView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	deepskyFragmentView=inflater.inflate(R.layout.deepskyfragment, container, false);
     	return deepskyFragmentView;
	}

}
