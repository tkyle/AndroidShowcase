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
    String debugTag = "DEBUG";
    TextView rotationCountTextView;

    public int RotationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation);

        rotationCountTextView = (TextView)findViewById(R.id.rotationCount);

        presenter = new RotationPresenter(RotationActivity.this);
        SetupRotationEventListener();
    }

    private void SetupRotationEventListener()
    {
        orientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL)
        {
            @Override
            public void onOrientationChanged(int orientation)
            {
                Log.v(debugTag, "Orientation changed to " + orientation);
                presenter.EvaluateOrientationChange(orientation);
            }
        };

        if (orientationListener.canDetectOrientation() == true)
        {
            Log.v(debugTag, "Can detect orientation");
            orientationListener.enable();
        }
        else
        {
            Log.v(debugTag, "Cannot detect orientation");
            orientationListener.disable();
        }
    }

    @Override
    public void UpdateRotationCountTextView(int rotationCount)
    {
        if(rotationCountTextView != null)
            rotationCountTextView.setText(Integer.toString(rotationCount));
    }
}
