package edu.purdue.spherorama;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SelectNeighbors extends Activity implements OnTouchListener, OnClickListener {
	
	//private Button btn_pos, btn_neighbors, btn_done;
	private ImageView img_map;
	//private String sphere_name;
	private int x, y, windowWidth, windowHeight;
	private String map = "";
	private Context ctx;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          		WindowManager.LayoutParams.FLAG_FULLSCREEN);
      		// needs to be called before setContentView
  		requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.select_attributes);
        ctx = this.getBaseContext();
        LinearLayout ll = (LinearLayout)findViewById(R.id.ll_attr);
        ll.setGravity(Gravity.BOTTOM);
        ll.setHorizontalGravity(Gravity.CENTER);
        Button btn_done = (Button)findViewById(R.id.btn_done);
        btn_done.setOnClickListener(this);
        img_map = (ImageView)findViewById(R.id.img_map);
        img_map.setOnTouchListener(this);
        Display display = getWindowManager().getDefaultDisplay(); 
        windowWidth = display.getWidth();
        windowHeight = display.getHeight();
        x = windowWidth/2;
        y = windowHeight/2;
        //sphere_name = this.getIntent().getStringExtra("name");
        pickMapDialog();
    }

	private void pickMapDialog() {
		File sdir = new File("/sdcard/Spherorama/maps");
		final String [] maps = sdir.list();
		if(maps.length == 0) {
			Toast.makeText(this, "No map images in /sdcard/Spherorama/maps", 
					Toast.LENGTH_LONG).show();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick Map");
		builder.setSingleChoiceItems(maps, -1, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				map = maps[which];
				loadMap(maps[which]);
			}
        });
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	Toast.makeText(ctx, "Center map on location.\nTap done button"+
            			" when done.", Toast.LENGTH_LONG).show();
            }
        });
		builder.create().show();
	}
	
	public void loadMap(String fileName) {
		BitmapDrawable map = new BitmapDrawable("/sdcard/Spherorama/maps/"+fileName);
		img_map.setImageDrawable(map);
		img_map.invalidate();
	}

	float mx=0, my=0;
	public boolean onTouch(View v, MotionEvent event) {
		 float curX, curY;

         switch (event.getAction()) {

             case MotionEvent.ACTION_DOWN:
                 mx = event.getX();
                 my = event.getY();
                 break;
             case MotionEvent.ACTION_MOVE:
                 curX = event.getX();
                 curY = event.getY();
                 img_map.scrollBy((int) (mx - curX), (int) (my - curY));
                 x = x+((int)(mx - curX));
                 y = y+((int)(my - curY));
                 mx = curX;
                 my = curY;
                 break;
             case MotionEvent.ACTION_UP:
                 curX = event.getX();
                 curY = event.getY();
                 img_map.scrollBy((int) (mx - curX), (int) (my - curY));
                 x = x+((int)(mx - curX));
                 y = y+((int)(my - curY));
                 break;
         }
         if(x<0)
        	 x=0;
         if(y<0)
        	 y=0;
         //Log.d("Sphere", map+" x: "+x+" y: "+y);

         return true;
	}
	
	public void onClick(View v) {
		   Intent i = new Intent();
		   i.putExtra("x", x);
		   i.putExtra("y", y);
		   i.putExtra("map", map);
		   setResult(RESULT_OK, i);
		   this.finish();
	}  

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK) {
			 return true;
	     }

		return super.onKeyDown(keyCode, event);
	}
}
