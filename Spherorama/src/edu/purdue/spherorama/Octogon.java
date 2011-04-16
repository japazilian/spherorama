package edu.purdue.spherorama;
  
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.opengl.GLUtils;
/*
 * Define the vertices for a representative face.
 * Render the cube by translating and rotating the face.
 */
public class Octogon implements PreviewCallback{
   private FloatBuffer vertexBuffer, texBuffer; // Buffer for vertex-array
   
   private float[] texCoords = {
	         0.0f, 1.0f,  // A. left-bottom
	         1.0f, 1.0f,  // B. right-bottom
	         0.0f, 0.0f,  // C. left-top
	         1.0f, 0.0f   // D. right-top
	      };
   
   byte[] glCameraFrame=new byte[256*256]; //size of a texture must be a power of 2
   int[] cameraTexture;
	
   //Testing
   
   public int lookingAt = 0;
   
   int[] side0Texture;
   int[] side1Texture;
   int[] side2Texture;
   int[] side3Texture;
   int[] side4Texture;
   int[] side5Texture;
   int[] side6Texture;
   int[] side7Texture;
   
   boolean [] hasPicture = new boolean[8];
   
   private Context ctx;
   
   // Constructor - Set up the buffers
   public Octogon(Context context) {
	   ctx = context;
	   // Setup vertex-array buffer. Vertices in float. An float has 4 bytes
      ByteBuffer vbb = ByteBuffer.allocateDirect(30 * 4 );//* numFaces);
      vbb.order(ByteOrder.nativeOrder()); // Use native byte order
      vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
      
      
      //int imgWidth = 512;//1024;
      //int imgHeight = 512;//1024;
      float faceWidth = 4.0f;
      float faceHeight = 4.0f;

      float faceLeft = -faceWidth / 2;
      float faceRight = -faceLeft;
      float faceTop = faceHeight / 2;
      float faceBottom = -faceTop;
      /*
      float awayDistance = 4.9f; // x
      
      double circleRadius = awayDistance/(Math.cos((45.0/2)*Math.PI/180)); // r
      
      double l = Math.sin((90-11.25)*Math.PI/180) * (circleRadius - awayDistance/Math.cos(11.25*Math.PI/180));
      double k = l/Math.tan((90-11.25)*Math.PI/180);
      double m = awayDistance*Math.tan(11.25*Math.PI/180)+k;
      
      
      float[] vertices = {
             faceLeft, 		faceBottom, 	-awayDistance, // point 1
             faceLeft, 		faceTop, 		-awayDistance, // point 2
             (float)(-m), 	faceBottom, 	(float)(-(l+awayDistance)), // point 3
             (float)(-m), 	faceTop, 		(float)(-(l+awayDistance)), // point 4
             0, 			faceBottom, 	(float)(-(circleRadius)), // point 5
             0, 			faceTop, 		(float)(-(circleRadius)), // point 6
             (float)(m), 	faceBottom, 	(float)(-(l+awayDistance)), // point 7
             (float)(m), 	faceTop, 		(float)(-(l+awayDistance)), // point 8
             faceRight, 	faceBottom, 	-awayDistance, // point 9
             faceRight, 	faceTop, 		-awayDistance, // point 10
          };
      */
	          
      // Define the vertices for this face
	  float[] vertices = {
	     faceLeft,  faceBottom, -3.9f,  // 0. left-bottom-front
	     faceRight, faceBottom, -3.9f,  // 1. right-bottom-front
	     faceLeft,  faceTop,    -3.9f,  // 2. left-top-front
	     faceRight, faceTop,    -3.9f,  // 3. right-top-front
	  };
  	  vertexBuffer.put(vertices);  // Populate
      vertexBuffer.position(0);    // Rewind
     
       
      ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4 );//* numFaces);
	  tbb.order(ByteOrder.nativeOrder());
	  texBuffer = tbb.asFloatBuffer();
	  texBuffer.put(texCoords);
	  texBuffer.position(0);

	  //Testing
	
	  for(int i=0; i<8; i++)
		  hasPicture[i] = false;
   }
   
   // Draw the shape
   public void draw(GL10 gl) {
      gl.glFrontFace(GL10.GL_CCW);    // Front face in counter-clockwise orientation
      gl.glEnable(GL10.GL_CULL_FACE); // Enable cull face
      gl.glCullFace(GL10.GL_BACK);    // Cull the back face (don't display)
      gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
      gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);  // Enable texture-coords-array (NEW)
      gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer); 
      gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer); // Define texture-coords buffer (NEW)
      gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
      
      // side 1
      gl.glTranslatef(0.0f, 0.0f, 0f);
      if(lookingAt == 0) {
    	  if(!hasPicture[0])
    		  bindCameraTexture(gl);
    	  else 
    		  gl.glBindTexture(GL10.GL_TEXTURE_2D, side0Texture[0]);
    	  gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
      else if(hasPicture[0]) {
    	  gl.glBindTexture(GL10.GL_TEXTURE_2D, side0Texture[0]);
    	  gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
      
      // side 2
      gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
      if(lookingAt == 1) {
    	  if(!hasPicture[1])
    		  bindCameraTexture(gl);
    	  else {
    		  gl.glBindTexture(GL10.GL_TEXTURE_2D, side1Texture[0]);
    	  }
    	  gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
      else if(hasPicture[1]) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, side1Texture[0]);
	  	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
  	  
      
      // slide 3
      gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
      if(lookingAt == 2) {
    	  if(!hasPicture[2])
    		  bindCameraTexture(gl);
    	  else {
    		  gl.glBindTexture(GL10.GL_TEXTURE_2D, side2Texture[0]);
    	  }
    	  gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
      else if(hasPicture[2]) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, side2Texture[0]);
	  	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
	  
      gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
      if(lookingAt == 3) {
    	  if(!hasPicture[3])
    		  bindCameraTexture(gl);
    	  else {
    		  gl.glBindTexture(GL10.GL_TEXTURE_2D, side3Texture[0]);
    	  }
    	  gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
      else if(hasPicture[3]) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, side3Texture[0]);
	  	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
      
      gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
      if(lookingAt == 4) {
    	  if(!hasPicture[4])
    		  bindCameraTexture(gl);
    	  else {
    		  gl.glBindTexture(GL10.GL_TEXTURE_2D, side4Texture[0]);
    	  }
    	  gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
      else if(hasPicture[4]) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, side4Texture[0]);
	  	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
      
      gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
      if(lookingAt == 5) {
    	  if(!hasPicture[5])
    		  bindCameraTexture(gl);
    	  else {
    		  gl.glBindTexture(GL10.GL_TEXTURE_2D, side5Texture[0]);
    	  }
    	  gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
      else if(hasPicture[5]) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, side5Texture[0]);
	  	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
      
      gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
      if(lookingAt == 6) {
    	  if(!hasPicture[6])
    		  bindCameraTexture(gl);
    	  else {
    		  gl.glBindTexture(GL10.GL_TEXTURE_2D, side6Texture[0]);
    	  }
    	  gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
      else if(hasPicture[6]) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, side6Texture[0]);
	  	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
      
      gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
      if(lookingAt == 7) {
    	  if(!hasPicture[7])
    		  bindCameraTexture(gl);
    	  else {
    		  gl.glBindTexture(GL10.GL_TEXTURE_2D, side7Texture[0]);
    	  }
    	  gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
      else if(hasPicture[7]) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, side7Texture[0]);
	  	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      }
      
      gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
      gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);  // Disable texture-coords-array (NEW)
      
      gl.glDisable(GL10.GL_CULL_FACE);
   }
   
   /**
	 * Generates a texture from the black and white array filled by the onPreviewFrame
	 * method.
	 */
	void bindCameraTexture(GL10 gl) {
		synchronized(this) {
			if (cameraTexture==null)
				cameraTexture=new int[1];
			else
				gl.glDeleteTextures(1, cameraTexture, 0);
			
			gl.glGenTextures(1, cameraTexture, 0);
			int tex = cameraTexture[0];
			gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
			gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_LUMINANCE, 256, 256, 0, GL10.GL_LUMINANCE, GL10.GL_UNSIGNED_BYTE, ByteBuffer.wrap(glCameraFrame));
			//gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);

		     gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_WRAP_S,GL10.GL_CLAMP_TO_EDGE); 
		     gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_WRAP_T,GL10.GL_CLAMP_TO_EDGE);
						
		}
	}
	
	public void newImage(Bitmap bm, GL10 gl) {
		//Toast.makeText(ctx, "new image", Toast.LENGTH_SHORT);
		/*Bitmap temp = BitmapFactory.decodeStream(
                ctx.getResources().openRawResource(imageFileIDs[0]));*/
		int change = 0;
		
		switch(lookingAt) {
		case 0:
			if (side0Texture==null)
				side0Texture=new int[1];
			else
				gl.glDeleteTextures(1, side0Texture, 0);
			gl.glGenTextures(1, side0Texture, 0);
			change = side0Texture[0];
			break;
		case 1:
			if (side1Texture==null)
				side1Texture=new int[1];
			else
				gl.glDeleteTextures(1, side1Texture, 0);
			gl.glGenTextures(1, side1Texture, 0);
			change = side1Texture[0];
			break;
		case 2:
			if (side2Texture==null)
				side2Texture=new int[1];
			else
				gl.glDeleteTextures(1, side2Texture, 0);
			gl.glGenTextures(1, side2Texture, 0);
			change = side2Texture[0];
			break;
		case 3:
			if (side3Texture==null)
				side3Texture=new int[1];
			else
				gl.glDeleteTextures(1, side3Texture, 0);
			gl.glGenTextures(1, side3Texture, 0);
			change = side3Texture[0];
			break;
		case 4:
			if (side4Texture==null)
				side4Texture=new int[1];
			else
				gl.glDeleteTextures(1, side4Texture, 0);
			gl.glGenTextures(1, side4Texture, 0);
			change = side4Texture[0];
			break;
		case 5:
			if (side5Texture==null)
				side5Texture=new int[1];
			else
				gl.glDeleteTextures(1, side5Texture, 0);
			gl.glGenTextures(1, side5Texture, 0);
			change = side5Texture[0];
			break;
		case 6:
			if (side6Texture==null)
				side6Texture=new int[1];
			else
				gl.glDeleteTextures(1, side6Texture, 0);
			gl.glGenTextures(1, side6Texture, 0);
			change = side6Texture[0];
			break;
		case 7:
			if (side7Texture==null)
				side7Texture=new int[1];
			else
				gl.glDeleteTextures(1, side7Texture, 0);
			gl.glGenTextures(1, side7Texture, 0);
			change = side7Texture[0];
			break;
			
		}
		
		
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	    gl.glBindTexture(GL10.GL_TEXTURE_2D, change);
        // Build Texture from loaded bitmap for the currently-bind texture ID
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bm, 0);
        bm.recycle();
        hasPicture[lookingAt] = true;
		
	}

	public void onPreviewFrame(byte[] data, Camera camera) {	
		// We are going from 240x160 to 256x170 to fit in 256x256
		
		int bwCounter=43*256;
		int yuvsCounter=0;
		boolean again = false;
		

		byte[] singleLine = new byte[256];
		for(int y=0; y<80; y++) {
			System.arraycopy(data, yuvsCounter, singleLine, 0, 120);
			yuvsCounter+=120;
			for(int j=0; j<16; j++) {
				singleLine[120+j]=data[yuvsCounter];
			}
			System.arraycopy(data, yuvsCounter, singleLine, 136, 120);
			yuvsCounter+=120;
			System.arraycopy(singleLine, 0, glCameraFrame, bwCounter, 256);
			bwCounter+=256;
		}
		for(int y=0; y<10; y++) {
			System.arraycopy(singleLine, 0, glCameraFrame, bwCounter, 256);
			bwCounter+=256;
		}
		for(int y=0; y<80; y++) {
			System.arraycopy(data, yuvsCounter, singleLine, 0, 120);
			yuvsCounter+=120;
			for(int j=0; j<16; j++) {
				singleLine[120+j]=data[yuvsCounter];
			}
			System.arraycopy(data, yuvsCounter, singleLine, 136, 120);
			yuvsCounter+=120;
			System.arraycopy(singleLine, 0, glCameraFrame, bwCounter, 256);
			bwCounter+=256;
		}
		
		/*
		for (int y=0;y<160;y++) {
			//System.arraycopy(data, yuvsCounter, glCameraFrame, bwCounter, 240);
			
			if(y%16==0)
				again = true;
			
			for(int j=0; j<16; j++) {
				System.arraycopy(data, yuvsCounter, glCameraFrame, bwCounter, 15);
				yuvsCounter += 15;
				bwCounter += 15;
				glCameraFrame[bwCounter+1] = data[yuvsCounter];
				bwCounter++;
			}
		}
		
		
		
		
		
		/*int bwCounter=48*256;
		int yuvsCounter=0;
		boolean again = false;
		for (int y=0;y<160;y++) {
			//System.arraycopy(data, yuvsCounter, glCameraFrame, bwCounter, 240);
			
			if(y%16==0)
				again = true;
			
			for(int j=0; j<16; j++) {
				System.arraycopy(data, yuvsCounter, glCameraFrame, bwCounter, 15);
				yuvsCounter += 15;
				bwCounter += 15;
				glCameraFrame[bwCounter+1] = data[yuvsCounter];
				bwCounter++;
			}
		}*/
	}
}
