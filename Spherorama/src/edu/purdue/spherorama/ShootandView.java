package edu.purdue.spherorama;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
/**
 * Our OpenGL program's main activity
 */
public class ShootandView extends Activity {
   
   private GLSurfaceView glView;   // Use GLSurfaceView
   private CamLayer mPreview;
   
   @Override
public boolean onCreateOptionsMenu(Menu menu) {
	   MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.menu, menu);
	    return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	 switch (item.getItemId()) {
	    case R.id.menu_shoot:
	    	((MyGLSurfaceView) glView).changeMode(1);
	    	shootButton.setVisibility(View.VISIBLE);
	        return true;
	    case R.id.menu_view:
	        ((MyGLSurfaceView) glView).changeMode(2);
	        shootButton.setVisibility(View.GONE);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
}

private Context ctx;
  private Button shootButton;
   // Call back when the activity is started, to initialize the view
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      		WindowManager.LayoutParams.FLAG_FULLSCREEN);
  		// needs to be called before setContentView
  		requestWindowFeature(Window.FEATURE_NO_TITLE); 
      glView = new MyGLSurfaceView(this);           // Allocate a GLSurfaceView
      //glView.setRenderer(new MyGLRenderer(this)); // Use a custom renderer
      this.setContentView(glView);                // This activity sets to GLSurfaceView
      mPreview = new CamLayer(this.getApplicationContext(), (PreviewCallback) glView);
      addContentView(mPreview, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
      
      
      
      //Adding location for button
      LinearLayout ll = new LinearLayout(this);
      ll.setGravity(Gravity.BOTTOM);
      ll.setHorizontalGravity(Gravity.CENTER);
      
      ctx = this;
      
      shootButton = new Button(this);
      shootButton.setWidth(100);
      shootButton.setText("Shoot");
      shootButton.setOnClickListener(new OnClickListener() {
	      public void onClick(View v) {
	    	Toast.makeText(ctx, "click!", Toast.LENGTH_SHORT).show();
	      }
	    });
      ll.addView(shootButton);
      addContentView(ll, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
      

      shootButton.setOnClickListener(new OnClickListener() {
	      public void onClick(View v) {
	    	mPreview.mCamera.autoFocus(autofocusCallback);
	      }
	    });
   }
   
// Called when camera autofocuses
	Camera.AutoFocusCallback autofocusCallback = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			if(success) {
		        mPreview.mCamera.takePicture(shutterCallback, 
		        		rawCallback, jpegCallback);
			}
		}
	};
	
	// Called when shutter is opened
	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			//Log.d(TAG, "onShutter'd");
		}
	};

	// Handles data for raw picture
	PictureCallback rawCallback = new PictureCallback() { 
		public void onPictureTaken(byte[] data, Camera camera) {
			//Log.d(TAG, "onPictureTaken - raw");
		}
	};
	
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			//TODO put back in 
			
			
			//Toast.makeText(ctx, ""+data.length, Toast.LENGTH_LONG).show();

			Bitmap bitmapOrg = BitmapFactory.decodeByteArray(data, 0, data.length);
			//1024x682 or 512x341
			Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapOrg, 512, 341, true);
			bitmapOrg.recycle();
			
			Bitmap potBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
			Canvas drawInMiddle = new Canvas(potBitmap);
			drawInMiddle.drawBitmap(resizedBitmap, 0.0f, (512-341)/2, null);
			resizedBitmap.recycle();
			//Bitmap resizedBitmap2 = Bitmap.createScaledBitmap(resizedBitmap, 512, 512, true);
			//resizedBitmap2.recycle();
			
			
			
			
			
			/*int width = bitmapOrg.getWidth();
	        int height = bitmapOrg.getHeight();
	        int newWidth = 512;
	        int newHeight = 512;
	       
	        // calculate the scale - in this case = 0.4f
	        float scaleWidth = ((float) newWidth) / width;
	        float scaleHeight = ((float) newHeight) / height;
	       
	        // createa matrix for the manipulation
	        Matrix matrix = new Matrix();
	        // resize the bit map
	        matrix.postScale(scaleWidth, scaleHeight);
	        
	        // recreate the new Bitmap
	        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,
	                          width, height, matrix, true);*/
	        
	        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	        potBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

	        //you can create a new file name "test.jpg" in sdcard folder.
	        File f = new File(Environment.getExternalStorageDirectory()
	                                + File.separator + "test.jpg");
	        try {
				f.createNewFile();
				//write the bytes in file
				FileOutputStream fo = new FileOutputStream(f);
				fo.write(bytes.toByteArray());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   
	       //byte[] orig = resizedBitmap.
			/*FileOutputStream outStream = null;
			try {
				// Write to SD Card
				outStream = new FileOutputStream(String.format("/sdcard/%d.jpg",
						System.currentTimeMillis()));
				
				outStream.write(data);
				outStream.close();
				//Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}*/
			((MyGLSurfaceView) glView).newImage(potBitmap);
			mPreview.mCamera.startPreview();
			
			/*Log.d(TAG, "onPictureTaken - jpeg");
			Log.d(TAG, camera.getParameters().getWhiteBalance());
			
			flashArrow(3);
			pictureOverlay(data);*/
		}
	};
   
   // Call back when the activity is going into the background
   @Override
   protected void onPause() {
      super.onPause();
      glView.onPause();
   }
   
   // Call back after onPause()
   @Override
   protected void onResume() {
      super.onResume();
      glView.onResume();
   }
}