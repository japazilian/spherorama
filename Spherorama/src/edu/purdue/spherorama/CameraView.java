package edu.purdue.spherorama;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class CameraView extends Activity {
	
	private static final String TAG = "CameraDemo";
	private Preview preview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// needs to be called before setContentView
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
    
	    setContentView(R.layout.camera);
	
	    preview = new Preview(this);
	    FrameLayout previewFrame = (FrameLayout)findViewById(R.id.preview);
	    previewFrame.addView(preview); 
	    preview.setOnClickListener(new OnClickListener() {
	      public void onClick(View v) {
	        preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
	      }
	    });
	    
	    //adding the outside edges
	    ImageView north = (ImageView)findViewById(R.id.overlay_north);
	    ImageView east = (ImageView)findViewById(R.id.overlay_east);
	    ImageView south = (ImageView)findViewById(R.id.overlay_south);
	    ImageView west = (ImageView)findViewById(R.id.overlay_west); 
	    ImageView arrow = (ImageView)findViewById(R.id.overlay_arrow);
	    previewFrame.removeView(north);
	    previewFrame.removeView(east);
	    previewFrame.removeView(south);
	    previewFrame.removeView(west);
	    previewFrame.removeView(arrow);
	    previewFrame.addView(north);
	    previewFrame.addView(east);
	    previewFrame.addView(south);
	    previewFrame.addView(west);
	    previewFrame.addView(arrow);
	
	    Log.d(TAG, "onCreate'd");
	}

	// Called when shutter is opened
	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	// Handles data for raw picture
	PictureCallback rawCallback = new PictureCallback() { // <7>
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	// Handles data for jpeg picture
	PictureCallback jpegCallback = new PictureCallback() { // <8>
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			try {
				// Write to SD Card
				outStream = new FileOutputStream(String.format("/sdcard/%d.jpg",
						System.currentTimeMillis())); // <9>
				//TODO put back in 
				//outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) { // <10>
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			Log.d(TAG, "onPictureTaken - jpeg");
			preview.camera.startPreview();
			flashArrow(3);
		}
	};
	
	private void flashArrow (int direction) {
		
		Bitmap bm = BitmapFactory.decodeResource(getResources(), 
				R.drawable.arrow);
		int width = bm.getWidth();
		int height = bm.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(0.75f, 0.75f);
		
		// 1 makes arrow point right
		// 2 makes arrow point bottom
		// 3 makes arrow point left
		// arrow points up otherwise
		switch(direction) {
		case 1:
			matrix.postRotate(90);
			break;
		case 2:
			matrix.postRotate(180);
			break;
		case 3:
			matrix.postRotate(270);
			break;
		}
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, 
				width, height, matrix, true);
		BitmapDrawable bmdrawable = new BitmapDrawable(newbm);
	
		ImageView arrow = (ImageView)findViewById(R.id.overlay_arrow);
		arrow.setImageDrawable(bmdrawable);		
		
		Animation fade = AnimationUtils.loadAnimation(this,
                R.anim.fade);	
		//Animation fadeout = AnimationUtils.loadAnimation(this,
        //        R.anim.fadeout);
		arrow.startAnimation(fade);
		//arrow.startAnimation(fadeout);
	}
}