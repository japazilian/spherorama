package edu.purdue.spherorama;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class Preview extends SurfaceView implements SurfaceHolder.Callback { 
  private static final String TAG = "Preview";

  SurfaceHolder mHolder;  
  public Camera camera;
  boolean previewRunning = false;
  

  Preview(Context context) {
    super(context);

    // Install a SurfaceHolder.Callback so we get notified when the
    // underlying surface is created and destroyed.
    mHolder = getHolder();
    mHolder.addCallback(this);
    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }

  // Called once the holder is ready
  public void surfaceCreated(SurfaceHolder holder) { 
    // The Surface has been created, acquire the camera and tell it where
    // to draw.
    camera = Camera.open();    
    Camera.Parameters para = camera.getParameters();
    para.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_INCANDESCENT);
    camera.setParameters(para);
    
    try {
      camera.setPreviewDisplay(holder);

      camera.setPreviewCallback(new PreviewCallback() {
        // Called for each frame previewed
        public void onPreviewFrame(byte[] data, Camera camera) {
          Log.d(TAG, "onPreviewFrame called at: " + System.currentTimeMillis());
          Preview.this.invalidate(); 
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Called when the holder is destroyed
  public void surfaceDestroyed(SurfaceHolder holder) {
    camera.stopPreview();
    previewRunning = false;
    camera.release();
  }

  // Called when holder has changed
  public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) { 
	  if (previewRunning) {
		  camera.stopPreview();
	  }
	  //Camera.Parameters p = camera.getParameters();
	  //p.setPreviewSize(w, h);
	  //camera.setParameters(p);
	  //camera.setDisplayOrientation(90);
	  try {
		  camera.setPreviewDisplay(holder);
	  } catch (IOException e) {
		  e.printStackTrace();
	  }
	  camera.startPreview();
	  previewRunning = true;
  }

}