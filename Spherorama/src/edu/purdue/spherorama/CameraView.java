package edu.purdue.spherorama;

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
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class CameraView extends Activity {
	
	private static final String TAG = "CameraDemo";
	private Preview preview;
	private ImageView north, east, south, west, arrow, prev_img;
	private Sphere sphere;

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
	    	preview.camera.autoFocus(autofocusCallback);
	      }
	    });
	    
	    //adding the outside edges, arrow, and previous image's overlay
	    /*north = (ImageView)findViewById(R.id.overlay_north);
	    east = (ImageView)findViewById(R.id.overlay_east);
	    south = (ImageView)findViewById(R.id.overlay_south);
	    west = (ImageView)findViewById(R.id.overlay_west); */
	    arrow = (ImageView)findViewById(R.id.overlay_arrow);
	    prev_img = (ImageView)findViewById(R.id.overlay_prev_img);
	    prev_img.setAlpha(0x99);
	    // these need to be added last to be overlaid on top of the preview
	    // easier to build in xml, but can't add the same view twice
	    previewFrame.removeView(prev_img);
	    /*previewFrame.removeView(north);
	    previewFrame.removeView(east);
	    previewFrame.removeView(south);
	    previewFrame.removeView(west);*/
	    previewFrame.removeView(arrow);
	    previewFrame.addView(prev_img);
	    /*previewFrame.addView(north);
	    previewFrame.addView(east);
	    previewFrame.addView(south);
	    previewFrame.addView(west);*/
	    previewFrame.addView(arrow);
	    
	    sphere = new Sphere();
	    Log.d(TAG, "onCreate'd");
	}

	@Override
	protected void onPause() {
		prev_img.setImageDrawable(null);
		super.onPause();
	}

	// Called when camera autofocuses
	Camera.AutoFocusCallback autofocusCallback = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			if(success) {
		        preview.camera.takePicture(shutterCallback, 
		        		rawCallback, jpegCallback);
			}
		}
	};
	
	// Called when shutter is opened
	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	// Handles data for raw picture
	PictureCallback rawCallback = new PictureCallback() { 
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	// Handles data for jpeg picture
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			//TODO put back in 
			/*FileOutputStream outStream = null;
			try {
				// Write to SD Card
				outStream = new FileOutputStream(String.format("/sdcard/%d.jpg",
						System.currentTimeMillis()));
				
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}*/
			Log.d(TAG, "onPictureTaken - jpeg");
			Log.d(TAG, camera.getParameters().getWhiteBalance());
			preview.camera.startPreview();
			flashArrow(3);
			pictureOverlay(data);
		}
	};
	
	private void pictureOverlay(byte[] data) {
		Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
		prev_img.setImageBitmap(bm);
		Animation move = AnimationUtils.loadAnimation(this,
                R.anim.move_right);
		prev_img.startAnimation(move);
		move.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				prev_img.offsetLeftAndRight(450);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});	
		//east.setAlpha(0xAA);
		//east.setImageBitmap(bm);
		
	}
	
	/**
	 * Flashes arrow.png on the screen to show user which way to turn next
	 * @param direction 0 for up, 1 for right, 2 for bottom, 3 for left
	 */
	private void flashArrow (int direction) {	
		Bitmap bm = BitmapFactory.decodeResource(getResources(), 
				R.drawable.arrow);
		int width = bm.getWidth();
		int height = bm.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(0.75f, 0.75f);
		
		// arrow is originally up
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
	
		//ImageView arrow = (ImageView)findViewById(R.id.overlay_arrow);
		arrow.setImageDrawable(bmdrawable);
		
		Animation fade = AnimationUtils.loadAnimation(this,
                R.anim.fade);
		arrow.startAnimation(fade);
		final ImageView arrowf = arrow;
		fade.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				arrowf.setImageDrawable(null);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});		
	}
}