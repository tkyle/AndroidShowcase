package com.tbd.androidshowcase.ui.activity;

import android.content.Intent;
import android.hardware.SensorManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.widget.TextView;

import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.presenter.RotationPresenter;
import com.tbd.androidshowcase.view.IRotationView;

public class RotationActivity extends AppCompatActivity implements IRotationView
{
    RotationPresenter presenter;
    OrientationEventListener orientationListener;
    TextView rotationCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation);

        rotationCountTextView = (TextView)findViewById(R.id.rotationCount);

        presenter = new RotationPresenter(RotationActivity.this);

        setupRotationEventListener();
    }

    private void setupRotationEventListener()
    {
        orientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL)
        {
            @Override
            public void onOrientationChanged(int orientation)
            {
                //Log.v(debugTag, "Orientation changed to " + orientation);
                presenter.EvaluateOrientationChange(orientation);
            }
        };

        if (orientationListener.canDetectOrientation() == true)
        {
            orientationListener.enable();
        }
        else
        {
            orientationListener.disable();
        }
    }

    @Override
    public void UpdateRotationCountTextView(int rotationCount)
    {
        // If the phone has been rotated 360 degrees then update the rotation counter text view
        if(rotationCountTextView != null)
            rotationCountTextView.setText(Integer.toString(rotationCount));
    }
}
