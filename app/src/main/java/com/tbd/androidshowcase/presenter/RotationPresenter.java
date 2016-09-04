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
        // If the phone was not turned to the right then subtract the degree from the count
        // Otherwise, if the phone was turned to the right add the degrees to the total count
        if(orientation < previousOrientation)
            rotationCount -= orientation;
        else
            rotationCount += orientation;

        if(rotationCount >= rotationGoal)
        {
            // Rotation goal has been met, increase the totalRotations by 1.
            rotationCount = 0;
            totalRotations += 1;

            // Update the UI
            view.UpdateRotationCountTextView(totalRotations);
        }

        previousOrientation = orientation;

        //Log.v(debugTag, "Orientation Count: " + rotationCount);
    }
}
