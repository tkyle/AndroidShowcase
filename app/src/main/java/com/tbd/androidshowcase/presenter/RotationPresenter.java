package com.tbd.androidshowcase.presenter;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.OrientationEventListener;

import com.tbd.androidshowcase.view.IRotationView;

/**
 * Created by Trevor on 9/4/2016.
 */
public class RotationPresenter
{
    IRotationView view;
    OrientationEventListener orientationListener;
    String debugTag = "Debug";

    public RotationPresenter(IRotationView view)
    {
        this.view = view;
    }

    public void SetupRotationEventListener(Context context)
    {
        orientationListener = new OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL)
        {
            @Override
            public void onOrientationChanged(int orientation)
            {
                Log.v(debugTag, "Orientation changed to " + orientation);
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
}
