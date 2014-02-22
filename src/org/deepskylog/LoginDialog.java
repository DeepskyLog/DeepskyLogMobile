package org.deepskylog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginDialog extends DialogFragment {
	
    private Bundle savedState = null;

	public interface LoginDialogOnClickListener {
        void onPositiveButtonClick();
        void onNegativeButtonClick();
    }
    
    private LoginDialogOnClickListener loginDialogOnClickListener;

	private View loginDialogView;
	private AlertDialog loginDialog;
	
	private EditText userid_edittext;
	private EditText password_edittext;
	
    public static LoginDialog newInstance(LoginDialogOnClickListener theOnClickListener) {
        LoginDialog d=new LoginDialog();
    	d.loginDialogOnClickListener=theOnClickListener;
    	return d;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.mainActivity);
        LayoutInflater inflater=getActivity().getLayoutInflater();
        loginDialogView=inflater.inflate(R.layout.logindialog, null);
        userid_edittext=(EditText)loginDialogView.findViewById(R.id.logindialog_userid_id);
        password_edittext=(EditText)loginDialogView.findViewById(R.id.logindialog_password_id);
        if(!MainActivity.preferences.getString("loginName", "").equals("")) userid_edittext.setText(MainActivity.preferences.getString("loginId", ""));
        if(!MainActivity.preferences.getString("loginPassword", "").equals("")) password_edittext.setText(MainActivity.preferences.getString("loginPassword", ""));        
		builder.setView(loginDialogView);
        builder.setPositiveButton(R.string.logindialog_login_button_text, null);
        builder.setNegativeButton(R.string.logindialog_cancel_button_text, null);      
        loginDialog=builder.create();
        loginDialog.setOnShowListener(new DialogInterface.OnShowListener() { @Override public void onShow(DialogInterface dialog) { Button positive_button=(Button) loginDialog.getButton(AlertDialog.BUTTON_POSITIVE); positive_button.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { login(); } } ); } } );
        loginDialog.setOnShowListener(new DialogInterface.OnShowListener() { @Override public void onShow(DialogInterface dialog) { Button negative_button=(Button) loginDialog.getButton(AlertDialog.BUTTON_NEGATIVE); negative_button.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { visitor(); } } ); } } );
		if(savedInstanceState==null) {
	    }
		else {
			savedState=savedInstanceState.getBundle("savedState");
		}
 		if(savedState!=null) {
	    	userid_edittext.setText(savedState.getString("userid_edittext"));
	    	password_edittext.setText(savedState.getString("password_edittext"));
 		}
 		savedState=null;		
        return loginDialog;
    }
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    savedInstanceState.putBundle("savedState", saveState());
	}
    private Bundle saveState() {
        Bundle state = new Bundle();
        state.putString("userid_edittext", userid_edittext.getText().toString());
        state.putString("password_edittext", password_edittext.getText().toString());
        return state;
    }

    private void login() {
    	MainActivity.preferenceEditor.putString("loginId",userid_edittext.getText().toString());
    	MainActivity.preferenceEditor.putString("loginPassword",password_edittext.getText().toString());
    	MainActivity.preferenceEditor.commit();
  	    loginDialogOnClickListener.onPositiveButtonClick();
        loginDialog.dismiss();
    }
    private void visitor() {
    	MainActivity.preferenceEditor.putString("loginId","");
    	MainActivity.preferenceEditor.putString("loginPassword","");
    	MainActivity.preferenceEditor.commit();
  	    loginDialogOnClickListener.onNegativeButtonClick();
  	    loginDialog.dismiss();
    }
}
