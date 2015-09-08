package com.company.administrator.monitorsystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A login screen that offers login via email/password.
 */
public class SettingActivity extends Activity  {


    // UI references.
    private EditText portView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button sureButton = (Button) findViewById(R.id.sureBtn);
        sureButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioGroup radioGroup =(RadioGroup)findViewById(R.id.seelctArea);
                if(radioGroup.getCheckedRadioButtonId() == R.id.three){
                    Intent tt = new Intent(SettingActivity.this,ThirdAreaActivity.class);
                    startActivity(tt);
                }else if(radioGroup.getCheckedRadioButtonId() == R.id.one){
                    Intent tt = new Intent(SettingActivity.this,FirstAreaActivity.class);
                    startActivity(tt);
                }else{
                    Toast.makeText(SettingActivity.this,"请选择区", Toast.LENGTH_LONG).show();
                    Intent tt = new Intent(SettingActivity.this, MainActivity.class);
                    startActivity(tt);
                }
                SettingActivity.this.finish();
            }
        });
    }

}

