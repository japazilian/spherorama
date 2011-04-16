package edu.purdue.spherorama;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Properties extends Activity implements OnClickListener {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.properties);
        
        Button done = (Button)findViewById(R.id.btn_props);
        done.setOnClickListener(this);
        
        
		SharedPreferences settings = getSharedPreferences("prefs", 0);
		String serverString = settings.getString("server", "sac01.cs.purdue.edu");
		String portString = settings.getString("port", "8080");
		String passwordString = settings.getString("password", "password");
		TextView txt_props = (TextView)findViewById(R.id.text_props);
		txt_props.setText("Currently Using: \n"+serverString+"\n"+portString+
				"\n"+passwordString);
		/*
		EditText server, port, password;
		server = (EditText)findViewById(R.id.edit_server);
		server.setText(serverString);
		port = (EditText)findViewById(R.id.edit_port);
		port.setText(portString);
		password = (EditText)findViewById(R.id.edit_password);
		password.setText(passwordString);*/
    }

	public void onClick(View arg0) {
		EditText server, port, password;
		server = (EditText)findViewById(R.id.edit_server);
		port = (EditText)findViewById(R.id.edit_port);
		password = (EditText)findViewById(R.id.edit_password);
		
		
		if(server.getText().toString().equals("") ||
				port.getText().toString().equals("") ||
				password.getText().toString().equals("")) {
			return;
		}
		
		
		SharedPreferences settings = getSharedPreferences("prefs", 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putString("server", server.getText().toString());
	    editor.putString("port", port.getText().toString());
	    editor.putString("password", password.getText().toString());
	    editor.commit();
	    this.finish();		
	}

}