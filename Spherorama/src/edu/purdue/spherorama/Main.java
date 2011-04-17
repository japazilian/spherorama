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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;

public class Main extends Activity implements OnClickListener {
	
	private Context ctx;
	private EditText edit_new;
    static final int PROGRESS_DIALOG = 0;
    //private ProgressThread progressThread;
    //private ProgressDialog progressDialog;
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
			Intent i2 = new Intent(this, Properties.class);
			startActivity(i2);
			break;
		case R.id.btn_upload:
			{
				File sdir = new File("/sdcard/Spherorama");
				String [] spheres = sdir.list();
				if(spheres.length == 1) {
					noSpheresDialog();
					return;
				}
				boolean states[] = new boolean[spheres.length];
				String spheresWithoutMaps[] = new String[spheres.length-1];
				int index=0;
				for(int i=0; i<spheres.length; i++) {
					if(spheres[i].equalsIgnoreCase("maps"))
						continue;
					spheresWithoutMaps[index++] = spheres[i];
				}
				showSpheresDialog(spheresWithoutMaps, states, 0);
			}
			break;
		case R.id.btn_delete:
		{
			File sdir = new File("/sdcard/Spherorama");
			String [] spheres = sdir.list();
			if(spheres.length == 1) {
				noSpheresDialog();
				return;
			}
			boolean states[] = new boolean[spheres.length];
			String spheresWithoutMaps[] = new String[spheres.length-1];
			int index=0;
			for(int i=0; i<spheres.length; i++) {
				if(spheres[i].equalsIgnoreCase("maps"))
					continue;
				spheresWithoutMaps[index++] = spheres[i];
			}
			showSpheresDialog(spheresWithoutMaps, states, 1);
		}
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
                	//progressThread = new ProgressThread(handler, items, states);
                    //showDialog(PROGRESS_DIALOG);                	
                	uploadSpheres(items, states);
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

	public void uploadSpheres(final String[] items, final boolean[] states) {
		Thread uploadThread = new Thread(new Runnable() {
			public void run() {
				String ns = Context.NOTIFICATION_SERVICE;
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
				try {
					//Get server, port, and password form prefs
					SharedPreferences settings = getSharedPreferences("prefs", 0);
					String server = settings.getString("server", "sac01.cs.purdue.edu");
					String port = settings.getString("port", "8080");
					String password = settings.getString("password", "password");
					
					
					// Setting up notification
					CharSequence tickerText = "Uploading images";              // ticker-text
					long when = System.currentTimeMillis();         // notification time
					// the next two lines initialize the Notification, using the configurations above
					Notification notification = new Notification(android.R.drawable.stat_sys_upload, tickerText, when);
					RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.upload_notification_layout);
			        contentView.setProgressBar(R.id.progressbar, 100, 0, true);
			        contentView.setTextViewText(R.id.text_notification, "Attempting connection.");
			        notification.contentView = contentView;
			        notification.flags |= Notification.FLAG_ONGOING_EVENT;
			        notification.flags |= Notification.FLAG_NO_CLEAR;
					Intent notificationIntent = new Intent(ctx, Main.class);
					PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);
					notification.contentIntent = contentIntent;
					mNotificationManager.notify(0, notification);
					
					
					int total = 0; 
					for(int i=0; i<states.length; i++) {
						if(states[i])
							total++;
					}
					total = total * 3;
					int done = 0; 
					for(int i=0; i<items.length; i++) {
						if(states[i]) {
							Socket socket = new Socket();
							socket.connect(new InetSocketAddress(server, Integer.parseInt(port)), 5*1000);
							InputStream inStream = socket.getInputStream() ;
				            OutputStream outStream = socket.getOutputStream() ;
				            BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
				            PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);
				            
				            // Check with password
				            out.println(password);
				            String status = in.readLine();
				            
				            if(status.equals("failed")) {
								// Kill upload notification, create new notification to
								// say the password was incorrect
								mNotificationManager.cancel(0);
								int icon_failed = android.R.drawable.stat_notify_error;
								CharSequence tickerText_failed = "Password Failed";
								long when_failed = System.currentTimeMillis();
								Notification notification_failed = new Notification(icon_failed, tickerText_failed, when_failed);
								notification_failed.flags |= Notification.FLAG_AUTO_CANCEL;
								CharSequence contentTitle = "Pano: Password Failed";
								CharSequence contentText = "Incorrect password for server.";
								Intent notificationIntent_failed = new Intent(ctx, Main.class);
								PendingIntent contentIntent_failed = PendingIntent.getActivity(ctx, 0, notificationIntent_failed, 0);
								notification_failed.setLatestEventInfo(ctx, contentTitle, contentText, contentIntent_failed);
								mNotificationManager.notify(2, notification_failed);
					            return;
				            }
					        
				            int percentageDone = (int)(((double)++done/total)*100);
				            contentView.setProgressBar(R.id.progressbar, 100, percentageDone, false);
				            contentView.setTextViewText(R.id.txt_percentage, percentageDone+"%");
					        contentView.setTextViewText(R.id.text_notification, "zipping images: "+items[i]);
					        notification.contentView = contentView;
							mNotificationManager.notify(0, notification);
				            
					        zipSphereDir(items[i]);
					        File zippedSphere = new File("/mnt/sdcard/Spherorama/"+
					        		items[i]+".zip");
					        out.println(items[i]+".zip");
					        
					        percentageDone = (int)(((double)++done/total)*100);
				            contentView.setProgressBar(R.id.progressbar, 100, percentageDone, false);
				            contentView.setTextViewText(R.id.txt_percentage, percentageDone+"%");
					        contentView.setTextViewText(R.id.text_notification, "uploading: "+items[i]);
					        notification.contentView = contentView;
							mNotificationManager.notify(0, notification);					        
					        
					        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(zippedSphere));
				            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream( ));
				            byte[] byteArray = new byte[1024];
				            int num;
				            while ((num = bis.read(byteArray)) != -1){
				                bos.write(byteArray,0,num);
				            }
		
				            bos.close();
				            bis.close();
					        
					        zippedSphere.delete();
					        
					        percentageDone = (int)(((double)++done/total)*100);
				            contentView.setProgressBar(R.id.progressbar, 100, percentageDone, false);
				            contentView.setTextViewText(R.id.txt_percentage, percentageDone+"%");
					        contentView.setTextViewText(R.id.text_notification, "finished: "+items[i]);
					        notification.contentView = contentView;
							mNotificationManager.notify(0, notification);	
							socket.close();
						} // for
					} // if
					
					// Kill upload notification, create new notification to 
					// say we are done
					mNotificationManager.cancel(0);
					int icon_done = android.R.drawable.stat_sys_upload_done;
					CharSequence tickerText_done = "Upload Complete";
					long when_done = System.currentTimeMillis();
					Notification notification_done = new Notification(icon_done, tickerText_done, when_done);
					notification_done.flags |= Notification.FLAG_AUTO_CANCEL;
					CharSequence contentTitle = "Pano: Upload Complete";
					CharSequence contentText = "All images uploaded successfuly.";
					Intent notificationIntent_done = new Intent(ctx, Main.class);
					PendingIntent contentIntent_done = PendingIntent.getActivity(ctx, 0, notificationIntent_done, 0);
					notification_done.setLatestEventInfo(ctx, contentTitle, contentText, contentIntent_done);
					mNotificationManager.notify(1, notification_done);
				} catch (Exception e) {
					// Kill upload notification, create new notification to
					// say there was an error while uploading or connecting
					mNotificationManager.cancel(0);
					int icon_failed = android.R.drawable.stat_notify_error;
					CharSequence tickerText_failed = "Error in Server Connection";
					long when_failed = System.currentTimeMillis();
					Notification notification_failed = new Notification(icon_failed, tickerText_failed, when_failed);
					notification_failed.flags |= Notification.FLAG_AUTO_CANCEL;
					CharSequence contentTitle = "Pano: Error in Server Connection";
					CharSequence contentText = e.getClass().getName();
					Intent notificationIntent_failed = new Intent(ctx, Main.class);
					PendingIntent contentIntent_failed = PendingIntent.getActivity(ctx, 0, notificationIntent_failed, 0);
					notification_failed.setLatestEventInfo(ctx, contentTitle, contentText, contentIntent_failed);
					mNotificationManager.notify(2, notification_failed);
					
					try {
						//There might be .zip files to delete, need to delete them
						for(int i=0; i<states.length; i++) {
		    				if(states[i]) {        	            
		    					String delzip = "/mnt/sdcard/Spherorama/"+items[i]+".zip";
		    					File zip = new File(delzip);
		    					zip.delete();
		    				}
		    			}
					} catch (Exception e2) { };
				}
			} // run
		});
		uploadThread.start();
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