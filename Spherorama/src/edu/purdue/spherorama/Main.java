package edu.purdue.spherorama;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main extends Activity implements OnClickListener {
	
	private Context ctx;
	private EditText edit_new;
    static final int PROGRESS_DIALOG = 0;
    private ProgressThread progressThread;
    private ProgressDialog progressDialog;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ctx = this.getApplicationContext();
        Button createNew = (Button)findViewById(R.id.btn_new);
        createNew.setOnClickListener(this);
        Button settings = (Button)findViewById(R.id.btn_settings);
        settings.setOnClickListener(this);
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
		case R.id.btn_settings:
			
			break;
		case R.id.btn_upload:
			File sdir = new File("/sdcard/Spherorama");
			String [] spheres = sdir.list();
			if(spheres.length == 0) {
				noSpheresDialog();
				return;
			}
			boolean states[] = new boolean[spheres.length];
			showSpheresDialog(spheres, states, 0);
			break;
		case R.id.btn_delete:
			File sdir2 = new File("/sdcard/Spherorama");
			String [] spheres2 = sdir2.list();
			if(spheres2.length == 0) {
				noSpheresDialog();
				return;
			}
			boolean states2[] = new boolean[spheres2.length];
			showSpheresDialog(spheres2, states2, 1);
			break;
		}
	}
	
	public void noSpheresDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("No Spheres Available");
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}
	
	public void showSpheresDialog(final String[] items, final boolean[] states, final int type) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick Spherorama");
		if(type == 0) {
			builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	progressThread = new ProgressThread(handler, items, states);
                    showDialog(PROGRESS_DIALOG);                	
                	//uploadSpheres(items, states);
                }
            });
			builder.setMultiChoiceItems(items, states, new DialogInterface.OnMultiChoiceClickListener(){
	            public void onClick(DialogInterface dialogInterface, int item, boolean state) {
	                //Toast.makeText(getApplicationContext(), items[item] + " set to " + state, Toast.LENGTH_SHORT).show();
	            }
	        });
		}
		else if(type == 1){
			builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
    	            actuallyDelete(items, states);                	
                }
            });
			builder.setMultiChoiceItems(items, states, new DialogInterface.OnMultiChoiceClickListener(){
	            public void onClick(DialogInterface dialogInterface, int item, boolean state) {
	                //Toast.makeText(getApplicationContext(), items[item] + " set to " + state, Toast.LENGTH_SHORT).show();
	            }
	        });
		}
		else { // Open we can only do one
			builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					
				}
	        });
		}
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
		builder.create().show();		
	}
	
	public void actuallyDelete(final String[] items, final boolean[] states) {
		
		String deletingSpheres = "";
		for(int i=0; i<states.length; i++)
			if(states[i]) deletingSpheres = deletingSpheres+items[i]+"\n";
		
		deletingSpheres = "Operation can't be undone.\n\nChosen Spheres:\n"+deletingSpheres;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Are you sure?");
		builder.setMessage(deletingSpheres);
		builder.setNegativeButton("No", null);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	for(int i=0; i<states.length; i++) {
    				if(states[i]) {        	            
    					String deldir = "/mnt/sdcard/Spherorama/"+items[i]+"/";
    					File dir = new File(deldir);
    					String [] pics = dir.list();
    					for(int j=0; j<pics.length; j++) {
    						String delfile = deldir+pics[j];
    						File f = new File(delfile);
    						f.delete();
    					}
    					dir.delete();
    				}
    			}
            }
		});
		builder.create().show();
	}

	public void uploadSpheres(Handler handler, final String[] items, final boolean[] states) {
		try {
			int total = 0; 
			for(int i=0; i<states.length; i++) {
				if(states[i])
					total++;
			}
			total = total * 3;
			int done = 0; 
			for(int i=0; i<items.length; i++) {
				if(states[i]) {
					/*HttpClient client = new DefaultHttpClient(); 
			        String postURL = "http://98.222.207.132:8080/spherorama/server";
			        HttpPost post = new HttpPost(postURL);*/
					Socket socket = new Socket("sac12.cs.purdue.edu", 9000);
					InputStream inStream = socket.getInputStream() ;
		            OutputStream outStream = socket.getOutputStream() ;
		            BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
		            PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);
		            
		            // Check with password
		            out.println("karma");
		            String status = in.readLine();
		            if(status.equals("failed")) {
		            	Toast.makeText(ctx, "Password Failed", 
		            			Toast.LENGTH_SHORT).show();
		            	Message msg = handler.obtainMessage();
			            msg.arg1 = 100;
			            handler.sendMessage(msg);
		            }
			        
			        Message msg = handler.obtainMessage();
		            msg.arg1 = (int)(((double)++done/total)*100);
		            msg.obj = "zipping images for: "+items[i];
		            handler.sendMessage(msg);
		            
			        zipSphereDir(items[i]);
			        File zippedSphere = new File("/mnt/sdcard/Spherorama/"+
			        		items[i]+".zip");
			        out.println(items[i]+".zip");
			        
			        msg = handler.obtainMessage();
		            msg.arg1 = (int)(((double)++done/total)*100);
		            msg.obj = "uploading zipped file: "+items[i];
		            handler.sendMessage(msg);
			        
			        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(zippedSphere));
		            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream( ));
		            byte[] byteArray = new byte[8192];
		            int num;
		            while ((num = bis.read(byteArray)) != -1){
		                bos.write(byteArray,0,num);
		            }

		            bos.close();
		            bis.close();
			        
			        zippedSphere.delete();
					msg = handler.obtainMessage();
		            msg.arg1 = (int)(((double)++done/total)*100);
		            msg.obj = items[i]+": done";
		            handler.sendMessage(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected Dialog onCreateDialog(int id) {
        switch(id) {
        case PROGRESS_DIALOG:
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Uploading...");
            progressDialog.setMessage("Starting...");
            return progressDialog;
        default:
            return null;
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch(id) {
        case PROGRESS_DIALOG:
            progressDialog.setProgress(0);
            progressDialog.setCancelable(false);
            //progressThread = new ProgressThread(handler);
            progressThread.start();
        }
    }

    // Define the Handler that receives messages from the thread and update the progress
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int total = msg.arg1;
            String s = (String)msg.obj;
            progressDialog.setProgress(total);
            progressDialog.setMessage(s);
            if (total >= 100){
            	Toast.makeText(ctx, "Upload Completed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss(); //dismissDialog(PROGRESS_DIALOG);
                progressThread.setState(ProgressThread.STATE_DONE);
            }
        }
    };
	
    /** Nested class that performs progress calculations (counting) */
    class ProgressThread extends Thread {
        Handler mHandler;
        final static int STATE_DONE = 0;
        final static int STATE_RUNNING = 1;
        int mState;
        int total;
        String[] items;
        boolean[] states;
       
        ProgressThread(Handler h, String[] s, boolean[] b) {
            mHandler = h;
            items = s;
            states = b;
        }
       
        public void run() {
            mState = STATE_RUNNING;   
            uploadSpheres(handler, items, states);
            while(mState == STATE_RUNNING) {
            	try {
            		Thread.sleep(50);
            	} catch(Exception e) {};
            }
        }
        
        /* sets the current state for the thread,
         * used to stop the thread */
        public void setState(int state) {
            mState = state;
        } 
    }

	private void zipSphereDir(String string) {
		try 
		{ 
		    //create a ZipOutputStream to zip the data to 
		    ZipOutputStream zos = new 
		           ZipOutputStream(new FileOutputStream("/mnt/sdcard/Spherorama/"+string+".zip")); 
		    zipDir("/mnt/sdcard/Spherorama/"+string, zos); 
		    //close the stream 
		    zos.close(); 
		} 
		catch(Exception e) { } 
	}
	public void zipDir(String dir2zip, ZipOutputStream zos) 
	{ 
	    try 
	    { 
	        File zipDir = new File(dir2zip); 
	        //get a listing of the directory content 
	        String[] dirList = zipDir.list(); 
	        byte[] readBuffer = new byte[2156]; 
	        int bytesIn = 0; 
	        //loop through dirList, and zip the files 
	        for(int i=0; i<dirList.length; i++) 
	        { 
	            File f = new File(zipDir, dirList[i]); 
	            //create a FileInputStream on top of f 
	            FileInputStream fis = new FileInputStream(f); 
	            // create a new zip entry 
	            ZipEntry anEntry = new ZipEntry(f.getPath()); 
	            //place the zip entry in the ZipOutputStream object 
	            zos.putNextEntry(anEntry); 
	            //now write the content of the file to the ZipOutputStream 
	            while((bytesIn = fis.read(readBuffer)) != -1) 
	            { 
	                zos.write(readBuffer, 0, bytesIn); 
	            } 
	           //close the Stream 
	           fis.close(); 
	        } 
		} 
		catch(Exception e) { } 
	}
}