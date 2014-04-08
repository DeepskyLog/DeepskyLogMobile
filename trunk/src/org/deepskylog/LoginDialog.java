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
	
    private Bundle stateBundle=null;

	private String loginDialogOnClickListenerClassname;
	private String loginDialogOnClickListenerMethodname;
	private boolean autoDismiss;

	private View loginDialogView;
	private AlertDialog loginDialog;
	
	private EditText userid_edittext;
	private EditText password_edittext;
	
	private Method loginDialogOnClickListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}		
	}
	
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
        this.loginDialogView=inflater.inflate(R.layout.logindialog, null);
        this.userid_edittext=(EditText)this.loginDialogView.findViewById(R.id.logindialog_userid_id);
        this.password_edittext=(EditText)this.loginDialogView.findViewById(R.id.logindialog_password_id);
        this.userid_edittext.setText(MainActivity.preferences.getString("loginId", ""));
        this.password_edittext.setText(MainActivity.preferences.getString("loginPassword", ""));        
		if(savedInstanceState!=null) {
			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(this.stateBundle!=null) {
 			this.userid_edittext.setText(this.stateBundle.getString("userid_edittext"));
 			this.password_edittext.setText(this.stateBundle.getString("password_edittext"));
 			this.loginDialogOnClickListenerClassname=this.stateBundle.getString("loginDialogOnClickListenerClassname");
 			this.loginDialogOnClickListenerMethodname=this.stateBundle.getString("loginDialogOnClickListenerMethodname");
 			this.autoDismiss=this.stateBundle.getBoolean("autoDismiss");
			if((!(this.loginDialogOnClickListenerClassname.equals("")))&&(!(this.loginDialogOnClickListenerMethodname.equals("")))) { 
	    		try { this.loginDialogOnClickListener=Class.forName(loginDialogOnClickListenerClassname).getMethod(loginDialogOnClickListenerMethodname, String.class); }
	    		catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"Exception 2 in loginDialog "+e.getMessage().toString(), Toast.LENGTH_SHORT).show(); };
	    	}
		}
 		this.stateBundle=null;		
	    builder.setView(this.loginDialogView);
        builder.setPositiveButton(R.string.logindialog_login_button_text, null);
        builder.setNegativeButton(R.string.logindialog_cancel_button_text, null);      
        this.loginDialog=builder.create();
        this.loginDialog.setOnShowListener(new DialogInterface.OnShowListener() { 
        	@Override public void onShow(DialogInterface dialog) { 
        		((Button) loginDialog.getButton(AlertDialog.BUTTON_POSITIVE)).setOnClickListener(new View.OnClickListener() { 
        			@Override public void onClick(View view) { onButtonClick("positive"); } } );          		
    			((Button) loginDialog.getButton(AlertDialog.BUTTON_NEGATIVE)).setOnClickListener(new View.OnClickListener() { 
    				@Override public void onClick(View view) { onButtonClick("negative"); } } ); 
        	}
        } ); 		
        return this.loginDialog;
    }
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    savedInstanceState.putBundle("stateBundle", this.getState());
	}
    private Bundle getState() {
    	Bundle state = new Bundle();
        state.putString("loginDialogOnClickListenerClassname", this.loginDialogOnClickListenerClassname);
        state.putString("loginDialogOnClickListenerMethodname", this.loginDialogOnClickListenerMethodname);
        state.putString("userid_edittext", this.userid_edittext.getText().toString());
        state.putString("password_edittext", this.password_edittext.getText().toString());
        state.putBoolean("autoDismiss", this.autoDismiss);
      return state;
    }
    private void onButtonClick(String theButton) {
    	if((!(this.loginDialogOnClickListenerClassname.equals("")))&&(!(this.loginDialogOnClickListenerMethodname.equals("")))) { 
    		try { 
    			if(theButton.equals("positive")) {
    				MainActivity.preferenceEditor
    					.putString("loginId", this.userid_edittext.getText().toString())
    					.putString("loginPassword", this.password_edittext.getText().toString())
    					.putString("loggedPerson", "")
    					.commit();
    			}
    			MainActivity.mainActivity.setActionBar();
    			this.loginDialogOnClickListener.invoke(null,theButton); 
    		} 
    		catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"Exception 3 in loginDialog "+e.getMessage().toString(), Toast.LENGTH_SHORT).show(); };
    	}
    	if(this.autoDismiss) this.loginDialog.dismiss();
    }
 }
