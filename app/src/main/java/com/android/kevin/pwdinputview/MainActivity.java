package com.android.kevin.pwdinputview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

	}
}
