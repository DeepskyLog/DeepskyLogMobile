package org.deepskylog;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
	private MainActivity mainActivity;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    mainActivity=(MainActivity) activity;
	    mainActivity.actualFragment=this;
	    mainActivity.actualFragmentName="settingsFragment";
	}
	@Override
	public void onResume() {
		super.onResume();
	    mainActivity.actualFragment=this;
	    mainActivity.actualFragmentName="settingsFragment";
	}
	public void refreshPreferences() {
		getPreferenceScreen().removeAll();
		addPreferencesFromResource(R.xml.preferences);		
	}

}
