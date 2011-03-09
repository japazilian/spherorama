package edu.purdue.spherorama;
  
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.opengl.GLUtils;
import android.view.SurfaceView;
import android.widget.Toast;
/*
 * Define the vertices for a representative face.
 * Render the cube by translating and rotating the face.
 */
public class Octogon implements PreviewCallback{
   private FloatBuffer vertexBuffer, texBuffer; // Buffer for vertex-array
  
   
   private int numFaces = 8;
   /*private int[] imageFileIDs = {  // Image file IDs
      R.drawable.a1,
      R.drawable.a2,
      R.drawable.a3,
      R.drawable.a4,
      R.drawable.a5,
      R.drawable.a6,
      R.drawable.a7,
      R.drawable.a8,
      R.drawable.a8
   };*/
   private int[] textureIDs = new int[numFaces];
   private Bitmap[] bitmap = new Bitmap[numFaces];
   
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
   
   int[] noImageTexture = new int[1];
   
   
	  
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
          
          /*
          // Adjust for aspect ratio
          if (imgWidth > imgHeight) {
             faceHeight = faceHeight * imgHeight / imgWidth; 
          } else {
             faceWidth = faceWidth * imgWidth / imgHeight;
          }*/
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
          
          
          
          
	      
	      
	      //for (int face = 0; face < numFaces; face++) {	          
	          // Define the vertices for this face
	          float[] vertices = {
	             faceLeft,  faceBottom, -3.9f,  // 0. left-bottom-front
	             faceRight, faceBottom, -3.9f,  // 1. right-bottom-front
	             faceLeft,  faceTop,    -3.9f,  // 2. left-top-front
	             faceRight, faceTop,    -3.9f,  // 3. right-top-front
	          };
	          vertexBuffer.put(vertices);  // Populate
	       //}
	       vertexBuffer.position(0);    // Rewind
	     
	       
	      ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4 );//* numFaces);
		  tbb.order(ByteOrder.nativeOrder());
		  texBuffer = tbb.asFloatBuffer();
		  //for (int face = 0; face < numFaces; face++) {
		     texBuffer.put(texCoords);
		  //} 
		  texBuffer.position(0);
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   /*
      // Setup vertex-array buffer. Vertices in float. An float has 4 bytes
      ByteBuffer vbb = ByteBuffer.allocateDirect(12 * 4 * numFaces);
      vbb.order(ByteOrder.nativeOrder()); // Use native byte order
      vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
      
      
      for (int face = 0; face < numFaces; face++) {
          bitmap[face] = BitmapFactory.decodeStream(
                context.getResources().openRawResource(imageFileIDs[face]));
          int imgWidth = bitmap[face].getWidth();
          int imgHeight = bitmap[face].getHeight();
          float faceWidth = 4.0f;
          float faceHeight = 4.0f;
          // Adjust for aspect ratio
          if (imgWidth > imgHeight) {
             faceHeight = faceHeight * imgHeight / imgWidth; 
          } else {
             faceWidth = faceWidth * imgWidth / imgHeight;
          }
          float faceLeft = -faceWidth / 2;
          float faceRight = -faceLeft;
          float faceTop = faceHeight / 2;
          float faceBottom = -faceTop;
          
          // Define the vertices for this face
          float[] vertices = {
             faceLeft,  faceBottom, -4.9f,  // 0. left-bottom-front
             faceRight, faceBottom, -4.9f,  // 1. right-bottom-front
             faceLeft,  faceTop,    -4.9f,  // 2. left-top-front
             faceRight, faceTop,    -4.9f,  // 3. right-top-front
          };
          vertexBuffer.put(vertices);  // Populate
       }
       vertexBuffer.position(0);    // Rewind
     
       
      ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4 * numFaces);
	  tbb.order(ByteOrder.nativeOrder());
	  texBuffer = tbb.asFloatBuffer();
	  for (int face = 0; face < numFaces; face++) {
	     texBuffer.put(texCoords);
	  }
	  texBuffer.position(0);   // Rewind
	  */
	  //Initiate camera stuff
	  //mCamera = Camera.open();
	  
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
      
      

      /*
      // side 1
      //gl.glPushMatrix();
      gl.glTranslatef(0.0f, 0.0f, 0f);
      //gl.glColor4f(colors[3][0], colors[3][1], colors[3][2], colors[3][3]);
      
      
      
      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[0]);
      bindCameraTexture(gl);
      
      
      
      
      
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      //gl.glPopMatrix();
      
      //gl.glTranslatef(0f, 0.0f, 5.4f);
      
      // side 2
      //gl.glPushMatrix();
      //gl.glLoadIdentity();
      gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
      gl.glTranslatef(0f, 0f, 0.0f);
      //gl.glColor4f(colors[1][0], colors[1][1], colors[1][2], colors[1][3]);
      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[1]);
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
      //gl.glPopMatrix();
      
      // slide 3
      gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
      gl.glTranslatef(0f, 0f, 0.0f);
      //gl.glColor4f(colors[1][0], colors[1][1], colors[1][2], colors[1][3]);
      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[2]);
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
      
      gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
      gl.glTranslatef(0f, 0f, 0.0f);
      //gl.glColor4f(colors[1][0], colors[1][1], colors[1][2], colors[1][3]);
      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[3]);
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
      
      gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
      gl.glTranslatef(0f, 0f, 0.0f);
      //gl.glColor4f(colors[1][0], colors[1][1], colors[1][2], colors[1][3]);
      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[4]);
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
      
      gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
      gl.glTranslatef(0f, 0f, 0.0f);
      //gl.glColor4f(colors[1][0], colors[1][1], colors[1][2], colors[1][3]);
      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[5]);
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
      
      gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
      gl.glTranslatef(0f, 0f, 0.0f);
      //gl.glColor4f(colors[1][0], colors[1][1], colors[1][2], colors[1][3]);
      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[6]);
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 24, 4);
      
      gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
      gl.glTranslatef(0f, 0f, 0.0f);
      //gl.glColor4f(colors[1][0], colors[1][1], colors[1][2], colors[1][3]);
      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[7]);
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 28, 4);*/
      
      
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
			
			
			/*if (mbm == null)
				return;
			
			if (cameraTexture==null)
				cameraTexture=new int[1];
			else
				gl.glDeleteTextures(1, cameraTexture, 0);
			
			gl.glGenTextures(1, cameraTexture, 0);
			int tex = cameraTexture[0];
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mbm, 0);
			mbm.recycle();*/
			
			
			//gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		    
			//gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[face]);
	         
			// Build Texture from loaded bitmap for the currently-bind texture ID
	         //GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap[face], 0);
	         //bitmap[face].recycle();
			
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
	    
		
		
		
		
		/*int[] textures = new int[1];	
	    gl.glGenTextures(1, textures, 0);	
	    gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);	
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);	
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);	
	    ByteBuffer imageBuffer = ByteBuffer.allocateDirect(bm.getHeight() * bm.getWidth() * 4);	
	    imageBuffer.order(ByteOrder.nativeOrder());	byte buffer[] = new byte[4];	
	    for(int i = 0; i < bm.getHeight(); i++)	{
	    	for(int j = 0; j < bm.getWidth(); j++)
	    	{			
	    		int color = bm.getPixel(j, i);			
	    		buffer[0] = (byte)Color.red(color);			
	    		buffer[1] = (byte)Color.green(color);			
	    		buffer[2] = (byte)Color.blue(color);			
	    		buffer[3] = (byte)Color.alpha(color);			
	    		imageBuffer.put(buffer);		
    		}	
    	}	
	    imageBuffer.position(0);	
	    gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, bm.getWidth(), bm.getHeight(), 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, imageBuffer);	
	    side0Texture[0] = textures[0];*/
	    
	    
	    
	    
        bm.recycle();
        //temp.recycle();
    
	
        hasPicture[lookingAt] = true;
		
	}
   
   public void loadTexture(GL10 gl) {
	      
	   /*gl.glGenTextures(1, noImageTexture, 0);
	   Bitmap bm = BitmapFactory.decodeStream(
               ctx.getResources().openRawResource(R.drawable.a1));
	   gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	    
       gl.glBindTexture(GL10.GL_TEXTURE_2D, noImageTexture[0]);
       
		// Build Texture from loaded bitmap for the currently-bind texture ID
       GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bm, 0);
       bm.recycle();*/
	   
	   
	      //gl.glGenTextures(8, textureIDs, 0); // Generate texture-ID array for 6 IDs
	   
	   	
	  
	      //gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	      //gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
	      //gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_WRAP_S,GL10.GL_CLAMP_TO_EDGE); 
	      //gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_WRAP_T,GL10.GL_CLAMP_TO_EDGE); 
	      // Generate OpenGL texture images
	     /*
	      for (int face = 0; face < numFaces; face++) {
	    	 gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		    
	         gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[face]);
	         
			// Build Texture from loaded bitmap for the currently-bind texture ID
	         GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap[face], 0);
	         bitmap[face].recycle();
	      }*/
	      
	   }

public void onPreviewFrame(byte[] data, Camera camera) {
	/*YuvImage yuvimage=new YuvImage(data,ImageFormat.NV21,256,256,null);
	ByteArrayOutputStream baos=new ByteArrayOutputStream();
	yuvimage.compressToJpeg(new Rect(0, 0, 256, 256), 80, baos);
	glCameraFrame = baos.toByteArray();*/
	
	
	int bwCounter=48*256;
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
		/*
		if (again) {
			for(int j=0; j<16; j++) {
				System.arraycopy(data, yuvsCounter, glCameraFrame, bwCounter, 15);
				yuvsCounter += 15;
				bwCounter += 15;
				glCameraFrame[bwCounter+1] = data[yuvsCounter];
				bwCounter++;
			}
			again = false;
		}*/
			

		
		
		
		//yuvsCounter=yuvsCounter+240;
		//bwCounter=bwCounter+256;
	}
	
	/*int [] rgb = new int[256*256];
	decodeYUV420SP(rgb, data, 256, 256);
	mbm = Bitmap.createBitmap(rgb, 256, 256,Bitmap.Config.ARGB_8888);*/
	
	
	//toRGB565(data, 256, 256, glCameraFrame);
	
	
}


private void toRGB565(byte[] yuvs, int width, int height, byte[] rgbs) {
    //the end of the luminance data
    final int lumEnd = width * height;
    //points to the next luminance value pair
    int lumPtr = 0;
    //points to the next chromiance value pair
    int chrPtr = lumEnd;
    //points to the next byte output pair of RGB565 value
    int outPtr = 0;
    //the end of the current luminance scanline
    int lineEnd = width;

    while (true) {

        //skip back to the start of the chromiance values when necessary
        if (lumPtr >= lineEnd) {
            if (lumPtr >= lumEnd) break; //we've reached the end
            //division here is a bit expensive, but's only done once per scanline
            chrPtr = lumEnd + ((lumPtr  >> 1) / width) * width;
            lineEnd += width;
        }

        //read the luminance and chromiance values
        final int Y1 = yuvs[lumPtr++] & 0xff; 
        final int Y2 = yuvs[lumPtr++] & 0xff; 
        final int Cr = (yuvs[chrPtr++] & 0xff) - 128; 
        final int Cb = (yuvs[chrPtr++] & 0xff) - 128;
        int R, G, B;

        //generate first RGB components
        B = Y1 + ((454 * Cb) >> 8);
        if(B < 0) B = 0; else if(B > 255) B = 255; 
        G = Y1 - ((88 * Cb + 183 * Cr) >> 8); 
        if(G < 0) G = 0; else if(G > 255) G = 255; 
        R = Y1 + ((359 * Cr) >> 8); 
        if(R < 0) R = 0; else if(R > 255) R = 255; 
        //NOTE: this assume little-endian encoding
        rgbs[outPtr++]  = (byte) (((G & 0x3c) << 3) | (B >> 3));
        rgbs[outPtr++]  = (byte) ((R & 0xf8) | (G >> 5));

        //generate second RGB components
        B = Y2 + ((454 * Cb) >> 8);
        if(B < 0) B = 0; else if(B > 255) B = 255; 
        G = Y2 - ((88 * Cb + 183 * Cr) >> 8); 
        if(G < 0) G = 0; else if(G > 255) G = 255; 
        R = Y2 + ((359 * Cr) >> 8); 
        if(R < 0) R = 0; else if(R > 255) R = 255; 
        //NOTE: this assume little-endian encoding
        rgbs[outPtr++]  = (byte) (((G & 0x3c) << 3) | (B >> 3));
        rgbs[outPtr++]  = (byte) ((R & 0xf8) | (G >> 5));
    }
}

static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
	final int frameSize = width * height;

	for (int j = 0, yp = 0; j < height; j++) {
		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
		for (int i = 0; i < width; i++, yp++) {
			int y = (0xff & ((int) yuv420sp[yp])) - 16;
			if (y < 0) y = 0;
			if ((i & 1) == 0) {
				v = (0xff & yuv420sp[uvp++]) - 128;
				u = (0xff & yuv420sp[uvp++]) - 128;
			}

			int y1192 = 1192 * y;
			int r = (y1192 + 1634 * v);
			int g = (y1192 - 833 * v - 400 * u);
			int b = (y1192 + 2066 * u);

			if (r < 0) r = 0; else if (r > 262143) r = 262143;
			if (g < 0) g = 0; else if (g > 262143) g = 262143;
			if (b < 0) b = 0; else if (b > 262143) b = 262143;

			rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
		}
	}
}


}
