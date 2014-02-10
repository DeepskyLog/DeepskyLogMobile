package org.deepskylog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.deepskylog.util.IEyepiece;
import org.deepskylog.util.IFilter;
import org.deepskylog.util.IImager;
import org.deepskylog.util.ILens;
import org.deepskylog.util.IObservation;
import org.deepskylog.util.IObserver;
import org.deepskylog.util.IScope;
import org.deepskylog.util.ISession;
import org.deepskylog.util.ISite;
import org.deepskylog.util.ITarget;
import org.deepskylog.util.OALException;
import org.deepskylog.util.util.SchemaException;
import org.deepskylog.util.util.SchemaLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OverviewActivity extends Activity {
	// TODO : Give information to InstrumentsActivity... extends application ?
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overview);

		try {
			FileInputStream fis = openFileInput("obs.oal");

			// File file = fis.
			File schema = new File("util/schema/");

			// Let's try to read the xml file
			SchemaLoader loader = new SchemaLoader();
			// For this, we need to import all the oal files...
			// We have to change to FileInputStream
			loader.load(fis, schema);
			List<IEyepiece> eyepieces = Arrays.asList(loader.getEyepieces());
			List<IFilter> filters = Arrays.asList(loader.getFilters());
			List<IImager> imagers = Arrays.asList(loader.getImagers());
			IObserver[] observers = loader.getObservers();
			List<IScope> telescopes = Arrays.asList(loader.getScopes());
			List<ISession> sessions = Arrays.asList(loader.getSessions());
			List<ISite> sites = Arrays.asList(loader.getSites());
			List<ITarget> objects = Arrays.asList(loader.getTargets());
			List<ILens> lenses = Arrays.asList(loader.getLenses());
			List<IObservation> observations = Arrays.asList(loader.getObservations());

			Button eyepiecesButton = (Button) findViewById(R.id.EyepiecesButton);
			eyepiecesButton.setText("Eyepieces : " + eyepieces.size());

			Button filtersButton = (Button) findViewById(R.id.FiltersButton);
			filtersButton.setText("Filters : " + filters.size());

			// TODO : Get the name of the observer
			TextView output = (TextView) findViewById(R.id.UserName);
			output.setText(observers[0].getDisplayName());

			Button scopesButton = (Button) findViewById(R.id.ScopesButton);
			scopesButton.setText("Instruments : " + telescopes.size());
			
			Button sessionsButton = (Button) findViewById(R.id.SessionsButton);
			sessionsButton.setText("Sessions : " + sessions.size());

			Button sitesButton = (Button) findViewById(R.id.SitesButton);
			sitesButton.setText("Sites : " + sites.size());

			Button objectsButton = (Button) findViewById(R.id.TargetsButton);
			objectsButton.setText("Objects : " + objects.size());

			Button lensesButton = (Button) findViewById(R.id.LensesButton);
			lensesButton.setText("Lenses : " + lenses.size());
			
			Button observationsButton = (Button) findViewById(R.id.observationsButton);
			// TODO : Use fixed text
			observationsButton.setText("Observations : " + observations.size());			
		} catch (SchemaException e) {
			// TODO : Do correct error handling
			e.printStackTrace();
		} catch (OALException e) {
			// TODO : Do correct error handling
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO : Do correct error handling
			e.printStackTrace();
		}
	}
	
 	// This method is called when we click start
 	public void clickInstruments(View view) {
 			// We want to start a new activity, so we create a new intent
 			Intent intent = new Intent(this, InstrumentsActivity.class);
 			startActivity(intent);
 	}

}
