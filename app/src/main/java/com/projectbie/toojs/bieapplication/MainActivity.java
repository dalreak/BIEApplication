package com.projectbie.toojs.bieapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //InitClient initclient = new InitClient(this);
        //initclient.getjsonData();

        InitLiveChart initlivechart = new InitLiveChart(this);


    }
}
