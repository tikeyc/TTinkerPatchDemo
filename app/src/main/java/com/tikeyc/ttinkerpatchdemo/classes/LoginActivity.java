package com.tikeyc.ttinkerpatchdemo.classes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tikeyc.ttinkerpatchdemo.R;

import org.xutils.view.annotation.Event;

public class LoginActivity extends AppCompatActivity {

    @Event(value = R.id.login_button)
    private void loginButtonClick(View view) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        
    }


}
