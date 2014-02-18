package org.deepskylog;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EphemeridesFragment extends Fragment {
	
	View ephemeridesFragmentView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ephemeridesFragmentView=inflater.inflate(R.layout.ephemeridesfragment, container, false);
     	return ephemeridesFragmentView;
	}

}
