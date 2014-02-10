package org.deepskylog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DeepskyLogmobileActivity extends Activity {
	public boolean _found = false;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // TODO : We check whether the oal file is loaded and display a text : 
        String[] files = fileList();

        for (int i = 0;i < files.length;i ++) {
        	if (files[i].equalsIgnoreCase("obs.oal")) {
        		_found = true;
        	}
        }
		TextView output = (TextView) findViewById(R.id.statusText);
        if (_found) {
    		output.setText("OAL file found");
        } else {
        	output.setText("OAL file not yet found");
        }
    }
     	
 	// This method is called when we click start
 	public void loginScreenHandler(View view) {
 		if (!_found) {
 			// We want to start a new activity, so we create a new intent
 			Intent intent = new Intent(this, LoginActivity.class);
 			startActivity(intent);
 		} else {
 			// We want to start a new activity, so we create a new intent
 			Intent intent = new Intent(this, OverviewActivity.class);
 			startActivity(intent); 			
 		}
 	}
}