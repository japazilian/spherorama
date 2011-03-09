package edu.purdue.spherorama;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button createNew = (Button)findViewById(R.id.btn_new);
        createNew.setOnClickListener(this);
    }

	public void onClick(View v) {
        startActivity(new Intent(this, ShootandView.class));
	}
}