package com.tbd.androidshowcase.ui.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.tbd.androidshowcase.R;

public class MoveCircleActivity extends AppCompatActivity {

    boolean drawCircle = false;

    float xValue = 0;
    float yValue = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new MyView(this));
    }

    public class MyView extends View
    {
        Paint paint;

        public MyView(Context context)
        {
            super(context);

            paint = new Paint();

            this.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if(event.getAction() == MotionEvent.ACTION_DOWN){

                        drawCircle = true;
                        xValue = event.getX();
                        yValue = event.getY();
                        invalidate();
                        return true;
                    }
                    else if (event.getAction() == MotionEvent.ACTION_MOVE)
                    {
                        xValue = event.getX();
                        yValue = event.getY();
                        invalidate();
                    }

                    return false;
                }
            });

        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);

            if(drawCircle == true)
            {
                int radius;
                radius = 100;
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.WHITE);
                canvas.drawPaint(paint);

                // Use Color.parseColor to define HTML colors
                paint.setColor(Color.parseColor("#CD5C5C"));
                canvas.drawCircle(xValue, yValue, radius, paint);
            }
        }
    }
}
