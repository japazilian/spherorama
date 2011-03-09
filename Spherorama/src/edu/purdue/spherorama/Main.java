package edu.purdue.spherorama;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main extends Activity implements OnClickListener {
	
	private Context ctx;
	private EditText edit_new;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ctx = this.getApplicationContext();
        Button createNew = (Button)findViewById(R.id.btn_new);
        createNew.setOnClickListener(this);
        Button upload = (Button)findViewById(R.id.btn_upload);
        upload.setOnClickListener(this);
        Button delete = (Button)findViewById(R.id.btn_delete);
        delete.setOnClickListener(this);
    }

	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_new:
			LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.new_sphere, null);
            final AlertDialog ad = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Create New Spherorama")
                .setView(textEntryView)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	edit_new = (EditText)textEntryView.findViewById(R.id.edit_new_sphere);
                    	String name = edit_new.getText().toString();
                    	Intent i = new Intent();
                    	i.setClassName("edu.purdue.spherorama", "edu.purdue.spherorama.ShootandView");
                    	i.putExtra("name", name);
                    	startActivity(i);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
            ad.show();
			break;
		case R.id.btn_upload:
			File sdir = new File("/sdcard/Spherorama");
			String [] spheres = sdir.list();
			boolean states[] = new boolean[spheres.length];
			showSpheresDialog(spheres, states);
			break;
		case R.id.btn_delete:
			File sdir2 = new File("/sdcard/Spherorama");
			String [] spheres2 = sdir2.list();
			boolean states2[] = new boolean[spheres2.length];
			showSpheresDialog(spheres2, states2);
			break;
		}
	}
	
	public void showSpheresDialog(final String[] items, final boolean[] states) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick Spheres")
			.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
    	            
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
		builder.setMultiChoiceItems(items, states, new DialogInterface.OnMultiChoiceClickListener(){
            public void onClick(DialogInterface dialogInterface, int item, boolean state) {
                Toast.makeText(getApplicationContext(), items[item] + " set to " + state, Toast.LENGTH_SHORT).show();
            }
        });
		builder.create().show();		
	}
}