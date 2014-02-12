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
	
	private TextView loggedperson_textview;
	private Button login_button;
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mainFragmentView=inflater.inflate(R.layout.mainfragment, container, false);    	
       	loggedperson_textview=(TextView) mainFragmentView.findViewById(R.id.mainfragment_loggedperson_textview_id);
       	login_button=(Button) mainFragmentView.findViewById(R.id.mainfragment_login_button_id);
        login_button.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { login(); } });
    	checkLoggedPerson();
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
	private void checkLoggedPerson() {
		if(MainActivity.loggedPerson.equals("")) {
			loggedperson_textview.setVisibility(View.GONE);
			login_button.setVisibility(View.VISIBLE);
		}
		else {
		
			loggedperson_textview.setVisibility(View.VISIBLE);
			login_button.setVisibility(View.GONE);
		}
	}
	private void login() {
		//if(mainActivity.preferences.getString("loginName", "").equals("")) {
		    mainActivity.loginDialog.show(mainActivity.getFragmentManager(), "loginDialog");
		    //mainActivity.actualFragmentName="loginDialog";
			//mainActivity.goToFragment(mainActivity.ADD_TO_BACKSTACK);
		//}
	}
}



