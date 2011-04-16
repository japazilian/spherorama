package edu.purdue.spherorama;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
/*
 * Custom GL view by extending GLSurfaceView so as
 * to override event handlers such as onKeyUp(), onTouchEvent()
 */
public class MyGLSurfaceView extends GLSurfaceView implements SurfaceHolder.Callback,
Camera.PreviewCallback, Camera.PictureCallback {
   public MyGLRenderer renderer;    // Custom GL Renderer
   
   // For touch event
   private final float TOUCH_SCALE_FACTOR = 160.0f / 320.0f;
   private float previousX;
   private float previousY;
   
   public static enum Mode {Shoot, View};
   public static Mode mode = Mode.Shoot;
   
   static Context ctx;
   GestureDetector gestureDetector;
   View.OnTouchListener gestureListener;
   public Button shootButton;
   // Constructor - Allocate and set the renderer
   public MyGLSurfaceView(Context context) {
      super(context);
      ctx = context;
      renderer = new MyGLRenderer(context);
      this.setRenderer(renderer);
      // Request focus, otherwise key/button won't react
      this.requestFocus();  
      this.setFocusableInTouchMode(true);
      
      gestureDetector = new GestureDetector(new MyGestureDetector());
      gestureListener = new View.OnTouchListener() {
          public boolean onTouch(View v, MotionEvent event) {
              if (gestureDetector.onTouchEvent(event)) {
                  return true;
              }
              return false;
          }
      };
      this.setOnTouchListener(gestureListener);
   }


   // Handler for touch event
   @Override
   public boolean onTouchEvent(final MotionEvent evt) {
	  if(mode == Mode.View) {
	      float currentX = evt.getX();
	      float currentY = evt.getY();
	      float deltaX;
	      //float deltaY;
	      switch (evt.getAction()) {
	         case MotionEvent.ACTION_MOVE:
	            // Modify rotational angles according to movement
	            deltaX = currentX - previousX;
	            //deltaY = currentY - previousY;
	            renderer.angleX += deltaX * TOUCH_SCALE_FACTOR * -1;
	            //renderer.angleY += deltaY * TOUCH_SCALE_FACTOR * -1;
	      }
	      // Save current x, y
	      previousX = currentX;
	      previousY = currentY;
	      return true;  // Event handled
	  }
	  else {
		  return true;
	  }
   }

	public void onPreviewFrame(byte[] data, Camera camera) {
		renderer.onPreviewFrame(data, camera);
	}
	//Handles data for jpeg picture
	public void onPictureTaken(byte[] data, Camera camera) {
		Toast.makeText(ctx, ""+data.length, Toast.LENGTH_SHORT).show();
		
		
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
		//Log.d(TAG, "onPictureTaken - jpeg");
		//Log.d(TAG, camera.getParameters().getWhiteBalance());
		//preview.camera.startPreview();
		//flashArrow(3);
		//pictureOverlay(data);
		
	}
	
	public void newImage(final Bitmap bm) {
		this.queueEvent(new Runnable() {
            // This method will be called on the rendering thread:
            public void run() {
        		renderer.cube.newImage(bm, renderer.gl);
            }});
	}
	
	public void changeMode(int i) {
		switch(i) {
		case 1:
			this.mode = Mode.Shoot;
			renderer.resetView();
			break;
		case 2:
			this.mode = Mode.View;
			renderer.cube.lookingAt = -1;
			break;
		}
	}

	

class MyGestureDetector extends SimpleOnGestureListener {
    
    float SWIPE_MAX_OFF_PATH = 40.0f;
    float SWIPE_MIN_DISTANCE = 3.0f;
    float SWIPE_THRESHOLD_VELOCITY = 1.0f;
    
    float startTime;
    
    
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    	if(mode == Mode.View)
    		return true;
        try {        	
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // right to left swipe
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Toast.makeText(ctx, "Left Swipe", Toast.LENGTH_SHORT).show();
                Animate a = new Animate(1);
                a.start();
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Toast.makeText(ctx, "Right Swipe", Toast.LENGTH_SHORT).show();
                Animate a = new Animate(2);
                a.start();
            }
        } catch (Exception e) {
            // nothing
        }
        return false;
    }
    
    class Animate extends Thread {
		private int direction; 
		
		public Animate(int direction) {
			this.direction = direction; 
		}
		
		public void run() {
			if(direction == 1) {
				float startTime = SystemClock.elapsedRealtime();
				float origX = renderer.angleX;
				float speed = (float)15/500000;
				while(SystemClock.elapsedRealtime() < (startTime+500)) {
					float deltat = (SystemClock.elapsedRealtime() - startTime);
					renderer.angleX = renderer.angleX+speed*deltat;
					//renderer.onDrawFrame(renderer.gl);
					Log.d("Vortex", "angle:"+renderer.angleX);
				}
				renderer.angleX = origX + 45;
      		  	renderer.animateLeft();
			}
			else {
				float startTime = SystemClock.elapsedRealtime();
				float origX = renderer.angleX;
				float speed = (float)15/500000;
				while(SystemClock.elapsedRealtime() < (startTime+500)) {
					float deltat = (SystemClock.elapsedRealtime() - startTime);
					renderer.angleX = renderer.angleX-speed*deltat;
					//renderer.onDrawFrame(renderer.gl);
					Log.d("Vortex", "angle:"+renderer.angleX);
				}
				renderer.angleX = origX - 45;
                renderer.animateRight();
			}
		}

	}
}



public void resetCurImage() {
	renderer.cube.hasPicture[renderer.cube.lookingAt]=false;	
}

}
