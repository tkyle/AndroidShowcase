package com.tbd.androidshowcase.ui.activity;

import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;

import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.presenter.RotationPresenter;
import com.tbd.androidshowcase.view.IRotationView;

public class RotationActivity extends AppCompatActivity implements IRotationView
{
    RotationPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation);

        presenter = new RotationPresenter(RotationActivity.this);
        presenter.SetupRotationEventListener(this);
    }
}
