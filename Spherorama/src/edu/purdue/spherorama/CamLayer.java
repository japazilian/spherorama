package edu.purdue.spherorama;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

/**
 * This class handles the camera. In particular, the method setPreviewCallback
 * is used to receive camera images. The camera images are not processed in
 * this class but delivered to the GLLayer. This class itself does
 * not display the camera images.
 * 
 * @author Niels
 *
 */
public class CamLayer extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback {
    Camera mCamera;
    boolean isPreviewRunning = false;
    Camera.PreviewCallback previewCallback;
    Camera.PictureCallback jpgCallback;
    
    Context ctx;

    CamLayer(Context context, Camera.PreviewCallback previewCallback) {
        super(context);
        ctx = context;
        this.previewCallback = previewCallback;
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        SurfaceHolder mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        
    }
    //mPreview.setLayoutParams(new LayoutParams(100,100))

	public void surfaceCreated(SurfaceHolder holder) {
    	//synchronized(this) {
	        mCamera = Camera.open();
	
	    	Camera.Parameters p = mCamera.getParameters();  
	    	/*for (int i : p.getSupportedPreviewFormats())
	    		Log.d("Vortex", "preview: "+i);
	    	
	    	for (Size i : p.getSupportedPreviewSizes())
	    		Log.d("Vortex", "size: "+i.height+"x"+i.width);
	    	
	    	for (Size i : p.getSupportedPictureSizes())
	    		Log.d("Vortex", "picture: "+i.height+"x"+i.width);*/
	    	
	    	//p.setPreviewFormat(ImageFormat.RGB_565);
	    	p.setPreviewSize(240, 160);
	    	p.setPictureSize(2592, 1728);
	    	p.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_INCANDESCENT);
	  
	    	mCamera.setParameters(p);
	    	
	    	try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				Log.e("Camera", "mCamera.setPreviewDisplay(holder);");
			}
			
	    	mCamera.startPreview();
    		mCamera.setPreviewCallback(this);
    	//}
	}

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
    	//synchronized(this) {
	    	try {
		    	if (mCamera!=null) {
		    		mCamera.stopPreview();  
		    		isPreviewRunning=false;
		    		mCamera.release();
		    	}
	    	} catch (Exception e) {
				Log.e("Camera", e.getMessage());
	    	}
    	//}
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

    }

	public void onPreviewFrame(byte[] arg0, Camera arg1) {
    	if (previewCallback!=null)
    		previewCallback.onPreviewFrame(arg0, arg1);        
	}
	
}
