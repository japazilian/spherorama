package edu.purdue.spherorama;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class ShootandView extends Activity {
   
   private GLSurfaceView glView;   // Use GLSurfaceView
   private CamLayer mPreview;
   private Context ctx;
   private Button shootButton;
   private String name;
   private ProgressDialog dialog;
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
	   MenuInflater inflater = getMenuInflater();
	   inflater.inflate(R.layout.menu, menu);
	   return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
	   switch (item.getItemId()) {
	   case R.id.menu_shoot:
		   ((MyGLSurfaceView) glView).changeMode(1);
		   shootButton.setVisibility(View.VISIBLE);
		   return true;
	   case R.id.menu_view:
		   ((MyGLSurfaceView) glView).changeMode(2);
		   shootButton.setVisibility(View.GONE);
		   return true;
	   case R.id.menu_done:
		   ((MyGLSurfaceView) glView).changeMode(1);
		   this.finish();
	   default:
		   return super.onOptionsItemSelected(item);
	   }
	}

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      		WindowManager.LayoutParams.FLAG_FULLSCREEN);
  		// needs to be called before setContentView
  		requestWindowFeature(Window.FEATURE_NO_TITLE); 
  		
      glView = new MyGLSurfaceView(this);           // Allocate a GLSurfaceView
      this.setContentView(glView);                // This activity sets to GLSurfaceView
      mPreview = new CamLayer(this.getApplicationContext(), (PreviewCallback) glView);
      addContentView(mPreview, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
      
      ctx = this;
      
      //Adding location for button
      LinearLayout ll = new LinearLayout(this);
      ll.setGravity(Gravity.BOTTOM);
      ll.setHorizontalGravity(Gravity.CENTER);
      
      shootButton = new Button(this);
      // Creating Button
      shootButton.setWidth(100);
      shootButton.setText("Shoot");
      shootButton.setOnClickListener(new OnClickListener() {
    	  public void onClick(View v) {
    		  if(shootButton.getText().toString().equals("Shoot")) {
	    		  shootButton.setEnabled(false);
	    		  mPreview.mCamera.autoFocus(autofocusCallback);
    		  }
    		  else {
    			  ((MyGLSurfaceView)glView).resetCurImage();
    			  shootButton.setText("Shoot");
    		  }
    	  }
      });
      
      ll.addView(shootButton);
      addContentView(ll, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
      
     
      // Get name of current sphere
      name = this.getIntent().getStringExtra("name");
      // Create dir
      File sphereDir = new File("/sdcard/Spherorama/"+name+"/");
      sphereDir.mkdirs();
      
      Intent i = new Intent(this, SelectNeighbors.class);
      startActivityForResult(i, 0);
      
   }
   
   @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		int x = data.getIntExtra("x", 0);
		int y = data.getIntExtra("y", 0);
		String map = data.getStringExtra("map");
		
		File attr = new File("/sdcard/Spherorama/"+name+"/attr");
		try {
			attr.createNewFile();
			FileOutputStream out = new FileOutputStream(attr);
			out.write(("x: "+x+"\ny: "+y+"\nmap: "+map).getBytes());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

   Handler changeButtonHandler = new Handler(){
	   @Override
	   public void handleMessage(Message msg) {
		   switch(msg.what){
       	   case 1:
       		   shootButton.setText("Shoot");
               break;
       	   case 2:
       		   shootButton.setText("Re-Shoot");
       		   break;
	      }
	   }
   };
   
   // Called when camera autofocuses
   Camera.AutoFocusCallback autofocusCallback = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			//if(success) {
  			  	dialog = ProgressDialog.show(ctx, "Working...", 
                      "Capturing image. Please wait...", true);
		        mPreview.mCamera.takePicture(shutterCallback, 
		        		rawCallback, jpegCallback);
			//}
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

			final Bitmap bitmapOrg = BitmapFactory.decodeByteArray(data, 0, data.length);
			//1024x682 or 512x341
			Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapOrg, 512, 341, true);
			// bitmapOrg.recycle();
			
			Bitmap potBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
			Canvas drawInMiddle = new Canvas(potBitmap);
			drawInMiddle.drawBitmap(resizedBitmap, 0.0f, (512-341)/2, null);
			resizedBitmap.recycle();
			Thread saveOnSD = new Thread(new Runnable() {
				public void run() {
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			        // potBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
			        bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

			        int lookingAt = ((MyGLSurfaceView)glView).renderer.cube.lookingAt;
			        //you can create a new file name "test.jpg" in sdcard folder.
			        File f = new File("/sdcard/Spherorama/"+name+"/"+lookingAt+".jpg");
			        try {
						f.createNewFile();
						//write the bytes in file
						FileOutputStream fo = new FileOutputStream(f);
						fo.write(bytes.toByteArray());
						bitmapOrg.recycle();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}	
			});
			saveOnSD.start();
	       
	   
			((MyGLSurfaceView) glView).newImage(potBitmap);
			mPreview.mCamera.startPreview();
			shootButton.setText("Re-Shoot");
			shootButton.setEnabled(true);
			dialog.cancel();
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