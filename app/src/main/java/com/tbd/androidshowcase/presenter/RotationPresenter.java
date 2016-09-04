package com.tbd.androidshowcase.presenter;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.OrientationEventListener;
import android.widget.TextView;

import com.tbd.androidshowcase.view.IRotationView;

/**
 * Created by Trevor on 9/4/2016.
 */
public class RotationPresenter
{
    IRotationView view;
    OrientationEventListener orientationListener;
    String debugTag = "Debug";
    TextView rotationCountTextView;

    int previousOrientation = 0;
    int rotationGoal = 64620;
    int rotationCount = 0;
    int totalRotations = 0;

    public RotationPresenter(IRotationView view)
    {
        this.view = view;
    }

    public void EvaluateOrientationChange(int orientation)
    {
        if(orientation < previousOrientation)
            rotationCount -= orientation;
        else
            rotationCount += orientation;

        if(rotationCount >= rotationGoal)
        {
            rotationCount = 0;
            totalRotations += 1;

            view.UpdateRotationCountTextView(totalRotations);
        }

        previousOrientation = orientation;

        Log.v(debugTag, "Orientation Count: " + rotationCount);
    }
}
