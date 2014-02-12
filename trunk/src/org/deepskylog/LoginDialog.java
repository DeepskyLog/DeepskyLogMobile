package org.deepskylog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginDialog extends DialogFragment {
	private MainActivity mainActivity;
	private View loginDialogView;
	private  AlertDialog loginDialog;
	private EditText username_edittext;
	private EditText password_edittext;
	
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    mainActivity=(MainActivity) activity;
	    mainActivity.actualFragment=this;
	    mainActivity.actualFragmentName="loginDialog";
	}
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        loginDialogView=inflater.inflate(R.layout.logindialog, null);
        username_edittext=(EditText)loginDialogView.findViewById(R.id.logindialog_username_id);
        password_edittext=(EditText)loginDialogView.findViewById(R.id.logindialog_password_id);
        if(!mainActivity.preferences.getString("loginName", "").equals("")) username_edittext.setText(mainActivity.preferences.getString("loginName", ""));
        if(!mainActivity.preferences.getString("loginPassword", "").equals("")) password_edittext.setText(mainActivity.preferences.getString("loginPassword", ""));        
		builder.setView(loginDialogView);
        builder.setPositiveButton(R.string.logindialog_login_button_text, null);
        builder.setNegativeButton(R.string.logindialog_cancel_button_text, new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) { LoginDialog.this.getDialog().cancel(); } });      
        loginDialog=builder.create();
        loginDialog.setOnShowListener(new DialogInterface.OnShowListener() { @Override public void onShow(DialogInterface dialog) { Button positive_button=(Button) loginDialog.getButton(AlertDialog.BUTTON_POSITIVE); positive_button.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { login(); } } ); } } );
        return loginDialog;
    }
    private void login() {
    	mainActivity.preferenceEditor.putString("loginName",username_edittext.getText().toString());
    	mainActivity.preferenceEditor.putString("loginPassword",password_edittext.getText().toString());
    	mainActivity.preferenceEditor.commit();
  	   if(mainActivity.preferences.getString("loginName", "").equals("")) {
 		   Toast.makeText(mainActivity, "DEVELOP: warn for empty name", Toast.LENGTH_LONG).show();
 	   }
 	   else if(mainActivity.preferences.getString("loginPassword", "").equals("")) {
 		   Toast.makeText(mainActivity, "DEVELOP: warn for empty password", Toast.LENGTH_LONG).show();
 	   }
 	   else {
 	       Toast.makeText(mainActivity, "DEVELOP: implement login check", Toast.LENGTH_LONG).show();
 	       loginDialog.dismiss();
       }
   	
    }
}
