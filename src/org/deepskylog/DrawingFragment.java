package org.deepskylog;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class DrawingFragment extends Fragment {

	private View drawingFragmentView;
	private Bundle stateBundle=null;

	private ImageView drawing_imageview;
	
	public static String drawingFileName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}		
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.drawingFragmentView=inflater.inflate(R.layout.drawingfragment, container, false);
		this.drawing_imageview=((ImageView)this.drawingFragmentView.findViewById(R.id.drawingfragment_drawing_imageview));
		this.drawing_imageview.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { drawingOnClick(); } });
		if(savedInstanceState!=null) {
 			this.stateBundle=savedInstanceState.getBundle("stateBundle");
		}
 		if(this.stateBundle!=null) {
 		}
 		else {
 		}
		File file = new File(drawingFileName);
		if(file.exists()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
	        Bitmap bm = BitmapFactory.decodeFile(drawingFileName, options);
	        this.drawing_imageview.setImageBitmap(bm); 
		}
		else {
	        this.drawing_imageview.setImageBitmap(null); 
	        Toast.makeText(MainActivity.mainActivity, "No drawing to display: "+DrawingFragment.drawingFileName, Toast.LENGTH_LONG).show();
		}
 		this.stateBundle=null;
 		return this.drawingFragmentView;
	}
	
	@Override 
	public void onResume() {
		super.onResume();
		File file = new File(drawingFileName);
		if(file.exists()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inSampleSize = 1;
	        Bitmap bm = BitmapFactory.decodeFile(drawingFileName, options);
	        this.drawing_imageview.setImageBitmap(bm); 
		}
		else {
	        this.drawing_imageview.setImageBitmap(null); 
	        Toast.makeText(MainActivity.mainActivity, "No drawing to display: "+DrawingFragment.drawingFileName, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBundle("stateBundle", this.getStateBundle());
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private Bundle getStateBundle() {
        Bundle state=new Bundle();
        return state;
    }
	
	private void drawingOnClick() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + DrawingFragment.drawingFileName), "image/*");
		startActivity(intent);
	}
}
