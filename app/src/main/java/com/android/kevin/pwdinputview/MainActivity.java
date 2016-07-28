package com.android.kevin.pwdinputview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		PwdInputView pwdInputView = (PwdInputView) findViewById(R.id.pwdinput);
		pwdInputView.setInputCallBack(new PwdInputView.InputCallBack() {
			@Override
			public void onInputFinished(String result) {
				Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
			}
		});
		EditText ed = (EditText) findViewById(R.id.ed);
		ed.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.i("EditText",""+keyCode);
				return false;
			}
		});
	}
}
