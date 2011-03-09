package edu.purdue.spherorama;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;
import android.widget.Button;

public class MyGLRenderer implements GLSurfaceView.Renderer, PreviewCallback {

	private Context context;   // Application context needed to read image (NEW)
	public Octogon cube;
	public  float angleX = 0;     // rotational angle in degree for cube
	public  float angleY = 0;
	private float startTime = 0;
	public GL10 gl;
   	// Constructor
	public MyGLRenderer(Context context) {
		this.context = context;   // Get the application context (NEW)
		cube = new Octogon(context);
      
	}
	  
	   // Call back when the surface is first created or re-created.
	   public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	      gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);  // Set color's clear-value to black
	      gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
	      gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
	      gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
	      gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
	      gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
	      gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance
	  
	      // Setup Texture, each time the surface is created (NEW)
	      

	      //cube.loadTexture(gl);            // Load images into textures (NEW)
	      gl.glEnable(GL10.GL_TEXTURE_2D);  // Enable texture (NEW)
	      
	      
	      //testing
	      this.gl = gl;
	   }
	   
	   // Call back after onSurfaceCreated() or whenever the window's size changes
	   public void onSurfaceChanged(GL10 gl, int width, int height) {
		  if (height == 0) height = 1;   // To prevent divide by zero
	      float aspect = (float)width / height;
	   
	      // Set the viewport (display area) to cover the entire window
	      gl.glViewport(0, 0, width, height);
	  
	      // Setup perspective projection, with aspect ratio matches viewport
	      gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
	      gl.glLoadIdentity();                 // Reset projection matrix
	      // Use perspective projection
	      GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.f);
	  
	      gl.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
	      gl.glLoadIdentity();                 // Reset

	   }
	   
	   // Call back to draw the current frame.
	   public void onDrawFrame(GL10 gl) {
		   // Clear color and depth buffers
	      gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	      
	      // ----- Render the Cube -----
	      gl.glLoadIdentity();                  // Reset the current model-view matrix
	      gl.glTranslatef(0.0f, 0.0f, 0.5f);   // Translate into the screen
	      gl.glRotatef(angleX, 0.0f, 1.0f, 0.0f); // Rotate
	      Log.d("GLRend", ""+angleX);
	      gl.glRotatef(angleY, 1.0f , 0.0f , 0.0f ); // Rotate
	      cube.draw(gl);
	      // Update the rotational angle after each refresh.
	      //angleCube += speedCube;
	   }

	public void onPreviewFrame(byte[] data, Camera camera) {
		cube.onPreviewFrame(data, camera);
	}

	public void animateLeft() {
		//cube.lookingAt = (cube.lookingAt-1)%8; isn't working for some reason
		cube.lookingAt--;
		if(cube.lookingAt == -1)
			cube.lookingAt = 7;
		if(cube.hasPicture[cube.lookingAt])
			((ShootandView)context).changeButtonHandler.sendEmptyMessage(2);
		else
			((ShootandView)context).changeButtonHandler.sendEmptyMessage(1);
		
	}

	public void animateRight() {
		cube.lookingAt = (cube.lookingAt+1)%8;
		if(cube.hasPicture[cube.lookingAt])
			((ShootandView)context).changeButtonHandler.sendEmptyMessage(2);
		else
			((ShootandView)context).changeButtonHandler.sendEmptyMessage(1);
	}

	public void resetView() {
		this.angleX = 0.0f;
		this.angleY = 0.0f;
		cube.lookingAt = 0;
		
	}
}
