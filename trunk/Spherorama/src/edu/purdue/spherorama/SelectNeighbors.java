package edu.purdue.spherorama;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SelectNeighbors extends Activity implements OnClickListener, OnTouchListener {
	
	private Button btn_pos, btn_neighbors, btn_done;
	private ImageView img_map;
	private String sphere_name;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_attributes);
        btn_pos = (Button)findViewById(R.id.btn_pos);
        btn_pos.setOnClickListener(this);
        btn_pos.setEnabled(false);
        btn_neighbors = (Button)findViewById(R.id.btn_neighbors);
        btn_neighbors.setOnClickListener(this);
        btn_done = (Button)findViewById(R.id.btn_done);
        btn_done.setOnClickListener(this);
        img_map = (ImageView)findViewById(R.id.img_map);
        img_map.setOnTouchListener(this);
        sphere_name = this.getIntent().getStringExtra("name");
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
				loadMap(maps[which]);
			}
        });
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
		builder.create().show();
	}
	
	public void loadMap(String fileName) {
		BitmapDrawable map = new BitmapDrawable("/sdcard/Spherorama/maps/"+fileName);
		img_map.setImageDrawable(map);
		img_map.invalidate();
		/*java.io.FileInputStream in;
		try {
			String file = "/mnt/sdcard/Spherorama/maps/"+fileName;
			//in = openFileInput(file);
			File f = new File(file);
			in = new FileInputStream(f);
			img_map.setImageBitmap(BitmapFactory.decodeStream(in));
			img_map.invalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	public void onClick(View arg0) {
		switch(arg0.getId()) {
		case R.id.btn_pos:
			btn_pos.setEnabled(false);
			btn_neighbors.setEnabled(true);
			break;
		case R.id.btn_neighbors:
			btn_pos.setEnabled(true);
			btn_neighbors.setEnabled(false);
			break;
		case R.id.btn_done:
			Intent i = new Intent();
        	i.setClassName("edu.purdue.spherorama.SelectNeighbors", 
        			"edu.purdue.spherorama.ShootandView");
        	i.putExtra("name", sphere_name);
        	startActivity(i);
			break;
		}
		
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
                 mx = curX;
                 my = curY;
                 break;
             case MotionEvent.ACTION_UP:
                 curX = event.getX();
                 curY = event.getY();
                 img_map.scrollBy((int) (mx - curX), (int) (my - curY));
                 break;
         }

         return true;
	}
}
