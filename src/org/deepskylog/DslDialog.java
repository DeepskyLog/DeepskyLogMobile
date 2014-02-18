package org.deepskylog;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DslDialog extends DialogFragment {
	              
    public interface DslDialogOnClickListener {
        void onPositiveButtonClick();
        void onNegativeButtonClick();
    }
 
	private View dslDialogView;
	private AlertDialog dslDialog;
	
	public String text_textview_text;
	public String positive_button_text;
	public String negative_button_text;
	
    public DslDialogOnClickListener dslDialogOnClickListener;
    
    public static DslDialog newInstance(DslDialogOnClickListener theOnClickListener) {
        DslDialog d=new DslDialog();
    	d.dslDialogOnClickListener=theOnClickListener;
    	return d;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dslDialogView=inflater.inflate(R.layout.dsldialog, null);
        TextView textid_textview=(TextView)dslDialogView.findViewById(R.id.dsldialog_textid_id);
        textid_textview.setText(text_textview_text);
        builder.setView(dslDialogView);
        builder.setPositiveButton(positive_button_text, null);
        builder.setNegativeButton(negative_button_text, null);      
        dslDialog=builder.create();
        dslDialog.setOnShowListener(new DialogInterface.OnShowListener() { 
        	@Override public void onShow(DialogInterface dialog) { 
        		Button positive_button=(Button) dslDialog.getButton(AlertDialog.BUTTON_POSITIVE); 
        		positive_button.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { onPositiveClick(); } } );  
        		Button negative_button=(Button) dslDialog.getButton(AlertDialog.BUTTON_NEGATIVE); 
        		negative_button.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { onNegativeClick(); } } ); 
        	}
        } );
        return dslDialog;
    }
    
    private void onPositiveClick() {
    	dslDialog.dismiss();
    	dslDialogOnClickListener.onPositiveButtonClick();
    }
    
    private void onNegativeClick() {
    	dslDialog.dismiss();
    	dslDialogOnClickListener.onNegativeButtonClick();
    }
}
