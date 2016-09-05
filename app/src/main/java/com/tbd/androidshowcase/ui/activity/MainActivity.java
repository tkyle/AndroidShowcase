package com.tbd.androidshowcase.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.presenter.MainPresenter;
import com.tbd.androidshowcase.view.IMainView;

public class MainActivity extends AppCompatActivity implements IMainView
{
    // region Fields

    private MainPresenter presenter;

    // endregion

    // region Constructor

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter(MainActivity.this);
    }

    // endregion

    // region MainPresenter Methods

    public void onExampleListEventClicked(View button){ presenter.onExampleListActivityClicked();}
    public void onMoveCircleActivityClicked(View button) { presenter.onMoveCircleActivityClicked(); }
    public void onRotationActivityClicked(View button)
    {
        presenter.onRotationActivityClicked();
    }

    // endregion

    // region IMainView Implementation

    @Override
    public void openExampleListActivity()
    {
        Intent intent = new Intent(this, ExampleListActivity.class);
        startActivity(intent);
    }

    @Override
    public void openMoveCircleActivity()
    {
        Intent intent = new Intent(this, MoveCircleActivity.class);
        startActivity(intent);
    }

    @Override
    public void openRotationActivity()
    {
        Intent intent = new Intent(this, RotationActivity.class);
        startActivity(intent);
    }

    // endregion
}
