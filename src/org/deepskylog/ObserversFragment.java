package org.deepskylog;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ObserversFragment extends Fragment {
	
	View observersFragmentView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		observersFragmentView=inflater.inflate(R.layout.observersfragment, container, false);
     	return observersFragmentView;
	}

}
