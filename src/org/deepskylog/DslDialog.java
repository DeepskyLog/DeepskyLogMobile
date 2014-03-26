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
import android.widget.TextView;
import android.widget.Toast;

public class DslDialog extends DialogFragment {
	          
   private Bundle stateBundle=null;
		
	private String dslDialogOnClickListenerClassname;
	private String dslDialogOnClickListenerMethodname;
	private boolean autoDismiss;
	
	private String text_textview_text;
	private String positive_button_text;
	private String neutral_button_text;
	private String negative_button_text;
	
	private View dslDialogView;
	private AlertDialog dslDialog;
	
	private Method dslDialogOnClickListener;
    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}		
	}
	
    public static DslDialog newInstance(String theOnClickListenerClassname,
    									String theOnClickListenerMethodname,
    									String theTextText, String thePositiveButtonText, 
    									String theNeutralButtonText, String theNegativeButtonText,
    									boolean theAutoDismiss) {
        DslDialog d=new DslDialog();
        d.dslDialogOnClickListenerClassname=theOnClickListenerClassname;
        d.dslDialogOnClickListenerMethodname=theOnClickListenerMethodname;
        d.text_textview_text=theTextText;
        d.positive_button_text=thePositiveButtonText;
        d.neutral_button_text=theNeutralButtonText;
        d.negative_button_text=theNegativeButtonText;
        d.autoDismiss=theAutoDismiss;
    	if((!theOnClickListenerClassname.equals(""))&&(!theOnClickListenerMethodname.equals(""))) { 
    		try { d.dslDialogOnClickListener=Class.forName(theOnClickListenerClassname).getMethod(theOnClickListenerMethodname, String.class); }
    		catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"Exception 1 in dslDialog  "+e.getMessage().toString(), Toast.LENGTH_SHORT).show(); };
    	}
    	return d;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
 	    if(savedInstanceState!=null) {
 	    	this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(this.stateBundle!=null) {
 			this.text_textview_text=this.stateBundle.getString("textid_textview");
 			this.positive_button_text=this.stateBundle.getString("positive_button");
 			this.neutral_button_text=this.stateBundle.getString("neutral_button");
 			this.negative_button_text=this.stateBundle.getString("negative_button");
 			this.negative_button_text=this.stateBundle.getString("negative_button");
 			this.dslDialogOnClickListenerClassname=this.stateBundle.getString("dslDialogOnClickListenerClassname");
 			this.dslDialogOnClickListenerMethodname=this.stateBundle.getString("dslDialogOnClickListenerMethodname");
			this.autoDismiss=this.stateBundle.getBoolean("autoDismiss");
	    	if((!(this.dslDialogOnClickListenerClassname.equals("")))&&(!(this.dslDialogOnClickListenerMethodname.equals("")))) { 
	    		try { this.dslDialogOnClickListener=Class.forName(dslDialogOnClickListenerClassname).getMethod(this.dslDialogOnClickListenerMethodname, String.class); } 
	    		catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"Exception 2 in dslDialog "+e.getMessage().toString(), Toast.LENGTH_SHORT).show(); };
	    	}
 		}
 		this.stateBundle=null;		

    	AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        this.dslDialogView=inflater.inflate(R.layout.dsldialog, null);
        builder.setView(this.dslDialogView);
        ((TextView)this.dslDialogView.findViewById(R.id.dsldialog_textid_id)).setText(this.text_textview_text);
        if(this.positive_button_text.equals("")) this.positive_button_text="Ok";
        builder.setPositiveButton(this.positive_button_text, null);
        if(!(this.neutral_button_text.equals(""))) builder.setNegativeButton(this.neutral_button_text, null);      
        if(!negative_button_text.equals("")) builder.setNegativeButton(negative_button_text, null);      
        this.dslDialog=builder.create();
        this.dslDialog.setOnShowListener(new DialogInterface.OnShowListener() { 
        	@Override public void onShow(DialogInterface dialog) { 
        		((Button) dslDialog.getButton(AlertDialog.BUTTON_POSITIVE)).setOnClickListener(new View.OnClickListener() { 
        			@Override public void onClick(View view) { onButtonClick("positive"); } } );  
        		if(!neutral_button_text.equals("")) {
        			((Button) dslDialog.getButton(AlertDialog.BUTTON_NEUTRAL)).setOnClickListener(new View.OnClickListener() { 
        				@Override public void onClick(View view) { onButtonClick("neutral"); } } ); 
        		}
        		if(!negative_button_text.equals("")) {
        			((Button) dslDialog.getButton(AlertDialog.BUTTON_NEGATIVE)).setOnClickListener(new View.OnClickListener() { 
        				@Override public void onClick(View view) { onButtonClick("negative"); } } ); 
        		}
        	}
        } ); 		
        return this.dslDialog;
    }
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    savedInstanceState.putBundle("stateBundle", this.getState());
	}
    private Bundle getState() {
        Bundle state=new Bundle();
        state.putString("dslDialogOnClickListenerClassname", this.dslDialogOnClickListenerClassname);
        state.putString("dslDialogOnClickListenerMethodname", this.dslDialogOnClickListenerMethodname);
        state.putString("textid_textview", this.text_textview_text);
        state.putString("positive_button", this.positive_button_text);
        state.putString("neutral_button", this.neutral_button_text);
        state.putString("negative_button", this.negative_button_text);
        state.putBoolean("autoDismiss", this.autoDismiss);
        return state;
    }

    private void onButtonClick(String theButton) {
    	if((!(this.dslDialogOnClickListenerClassname.equals("")))&&(!(this.dslDialogOnClickListenerMethodname.equals("")))) { 
    		try { this.dslDialogOnClickListener.invoke(null,theButton); } 
    		catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"Exception 3 in dslDialog "+e.getMessage().toString(), Toast.LENGTH_SHORT).show(); };
    	}
    	if(this.autoDismiss) this.dslDialog.dismiss();
    }
 }
