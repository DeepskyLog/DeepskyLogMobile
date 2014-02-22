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
	          
    private Bundle savedState = null;
	
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
 	    if(savedInstanceState==null) {

 	    }
		else {
			savedState=savedInstanceState.getBundle("savedState");
		}
 		if(savedState!=null) {
 			text_textview_text=savedState.getString("textid_textview");
	    	positive_button_text=savedState.getString("positive_button");
	    	neutral_button_text=savedState.getString("neutral_button");
	    	negative_button_text=savedState.getString("negative_button");
	    	negative_button_text=savedState.getString("negative_button");
			dslDialogOnClickListenerClassname=savedState.getString("dslDialogOnClickListenerClassname");
			dslDialogOnClickListenerMethodname=savedState.getString("dslDialogOnClickListenerMethodname");
			autoDismiss=savedState.getBoolean("autoDismiss");
	    	if((!dslDialogOnClickListenerClassname.equals(""))&&(!dslDialogOnClickListenerMethodname.equals(""))) { 
	    		try { dslDialogOnClickListener=Class.forName(dslDialogOnClickListenerClassname).getMethod(dslDialogOnClickListenerMethodname, String.class); } 
	    		catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"Exception 2 in dslDialog "+e.getMessage().toString(), Toast.LENGTH_SHORT).show(); };
	    	}
 		}
 		savedState=null;		

    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dslDialogView=inflater.inflate(R.layout.dsldialog, null);
        builder.setView(dslDialogView);
        ((TextView)dslDialogView.findViewById(R.id.dsldialog_textid_id)).setText(text_textview_text);
        if(positive_button_text.equals("")) positive_button_text="Ok";
        builder.setPositiveButton(positive_button_text, null);
        if(!neutral_button_text.equals("")) builder.setNegativeButton(neutral_button_text, null);      
        if(!negative_button_text.equals("")) builder.setNegativeButton(negative_button_text, null);      
        dslDialog=builder.create();
        dslDialog.setOnShowListener(new DialogInterface.OnShowListener() { 
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
        return dslDialog;
    }
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    savedInstanceState.putBundle("savedState", saveState());
	}
    private Bundle saveState() {
        Bundle state = new Bundle();
        state.putString("dslDialogOnClickListenerClassname", dslDialogOnClickListenerClassname);
        state.putString("dslDialogOnClickListenerMethodname", dslDialogOnClickListenerMethodname);
        state.putString("textid_textview", text_textview_text);
        state.putString("positive_button", positive_button_text);
        state.putString("neutral_button", neutral_button_text);
        state.putString("negative_button", negative_button_text);
        state.putBoolean("autoDismiss", autoDismiss);
        return state;
    }

    private void onButtonClick(String theButton) {
    	if((!dslDialogOnClickListenerClassname.equals(""))&&(!dslDialogOnClickListenerMethodname.equals(""))) { 
    		try { dslDialogOnClickListener.invoke(null,theButton); } 
    		catch (Exception e) { Toast.makeText(MainActivity.mainActivity,"Exception 3 in dslDialog "+e.getMessage().toString(), Toast.LENGTH_SHORT).show(); };
    	}
    	if(autoDismiss) dslDialog.dismiss();
    }
 }
