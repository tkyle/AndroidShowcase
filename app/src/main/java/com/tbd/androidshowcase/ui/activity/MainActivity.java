package com.tbd.androidshowcase.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.view.IMainView;

public class MainActivity extends AppCompatActivity implements IMainView {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
