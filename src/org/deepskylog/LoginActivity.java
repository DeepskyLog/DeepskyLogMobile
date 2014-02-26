package org.deepskylog;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {
	private EditText login;
	@SuppressWarnings("unused")
	private EditText password;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// Fetches the login
		login = (EditText) findViewById(R.id.editText1);

		// Fetches the password
		password = (EditText) findViewById(R.id.editText2);
	}

	// This method is called at button click because we assigned the name to the
	// "On Click property" of the button
	public void loginHandler(View view) throws IOException {
		// Let's download the OAL file
		URL url = new URL("http://www.ster.kuleuven.be/~wim/wim.oal");
		URLConnection ucon = url.openConnection();

		InputStream is = ucon.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(is);

		/*
		 * Read bytes to the Buffer until there is nothing more to read(-1).
		 */
		ByteArrayBuffer baf = new ByteArrayBuffer(50);
		int current = 0;
		while ((current = bis.read()) != -1) {
			baf.append((byte) current);
		}

		// Let's try to save the oal file from the internet to the internal
		// storage
		FileOutputStream fos = openFileOutput("obs.oal", Context.MODE_PRIVATE);
		fos.write(baf.toByteArray());
		fos.close();

 		// We want to start a new activity, so we create a new intent
		Intent intent = new Intent(this, OverviewActivity.class);
 		startActivity(intent);

		
		EditText output = (EditText) findViewById(R.id.editText3);
		output.setText("TEST : " + login.getText());
	}
}
