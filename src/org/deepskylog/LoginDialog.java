package org.deepskylog;

import java.lang.reflect.Method;

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
	
    private Bundle savedState = null;

	private String loginDialogOnClickListenerClassname;
	private String loginDialogOnClickListenerMethodname;
	private boolean autoDismiss;

	private View loginDialogView;
	private AlertDialog loginDialog;
	
	private EditText userid_edittext;
	private EditText password_edittext;
	
	private Method loginDialogOnClickListener;

    public static LoginDialog newInstance(String theOnClickListenerClassname,
										  String theOnClickListenerMethodname,
										  boolean theAutoDismiss) {
        LoginDialog l=new LoginDialog();
        l.loginDialogOnClickListenerClassname=theOnClickListenerClassname;
        l.loginDialogOnClickListenerMethodname=theOnClickListenerMethodname;
        l.autoDismiss=theAutoDismiss;
    	if((!theOnClickListenerClassname.equals(""))&&(!theOnClickListenerMethodname.equals(""))) { 
    		try { l.loginDialogOnClickListener=Class.forName(theOnClickListenerClassname).getMethod(theOnClickListenerMethodname, String.class); }
    		catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"Exception 1 in loginDialog  "+e.getMessage().toString(), Toast.LENGTH_SHORT).show(); };
    	}
    	return l;
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
		if(savedInstanceState==null) {
	    }
		else {
			savedState=savedInstanceState.getBundle("savedState");
		}
 		if(savedState!=null) {
	    	userid_edittext.setText(savedState.getString("userid_edittext"));
	    	password_edittext.setText(savedState.getString("password_edittext"));
			loginDialogOnClickListenerClassname=savedState.getString("loginDialogOnClickListenerClassname");
			loginDialogOnClickListenerMethodname=savedState.getString("loginDialogOnClickListenerMethodname");
			autoDismiss=savedState.getBoolean("autoDismiss");
			if((!loginDialogOnClickListenerClassname.equals(""))&&(!loginDialogOnClickListenerMethodname.equals(""))) { 
	    		try { loginDialogOnClickListener=Class.forName(loginDialogOnClickListenerClassname).getMethod(loginDialogOnClickListenerMethodname, String.class); }
	    		catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"Exception 2 in loginDialog "+e.getMessage().toString(), Toast.LENGTH_SHORT).show(); };
	    	}
		}
 		savedState=null;		
	    builder.setView(loginDialogView);
        builder.setPositiveButton(R.string.logindialog_login_button_text, null);
        builder.setNegativeButton(R.string.logindialog_cancel_button_text, null);      
        loginDialog=builder.create();
        loginDialog.setOnShowListener(new DialogInterface.OnShowListener() { 
        	@Override public void onShow(DialogInterface dialog) { 
        		((Button) loginDialog.getButton(AlertDialog.BUTTON_POSITIVE)).setOnClickListener(new View.OnClickListener() { 
        			@Override public void onClick(View view) { onButtonClick("positive"); } } );          		
    			((Button) loginDialog.getButton(AlertDialog.BUTTON_NEGATIVE)).setOnClickListener(new View.OnClickListener() { 
    				@Override public void onClick(View view) { onButtonClick("negative"); } } ); 
        	}
        } ); 		
        return loginDialog;
    }
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    savedInstanceState.putBundle("savedState", saveState());
	}
    private Bundle saveState() {
    	Bundle state = new Bundle();
        state.putString("loginDialogOnClickListenerClassname", loginDialogOnClickListenerClassname);
        state.putString("loginDialogOnClickListenerMethodname", loginDialogOnClickListenerMethodname);
        state.putString("userid_edittext", userid_edittext.getText().toString());
        state.putString("password_edittext", password_edittext.getText().toString());
        state.putBoolean("autoDismiss", autoDismiss);
      return state;
    }
    private void onButtonClick(String theButton) {
    	if((!loginDialogOnClickListenerClassname.equals(""))&&(!loginDialogOnClickListenerMethodname.equals(""))) { 
    		try { loginDialogOnClickListener.invoke(null,theButton); } 
    		catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"Exception 3 in loginDialog "+e.getMessage().toString(), Toast.LENGTH_SHORT).show(); };
    	}
    	if(autoDismiss) loginDialog.dismiss();
    }
 }
