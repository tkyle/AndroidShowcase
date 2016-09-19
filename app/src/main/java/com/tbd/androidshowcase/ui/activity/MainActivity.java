package com.tbd.androidshowcase.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.main_action_bar_title);
    }

    // endregion

    // region MainPresenter Methods

    public void onProductListActivityClicked(View button){ presenter.onProductListActivityClicked();}
    public void onMoveCircleActivityClicked(View button) { presenter.onMoveCircleActivityClicked(); }

    // endregion

    // region IMainView Implementation

    @Override
    public void openProductListActivity()
    {
        Intent intent = new Intent(this, ProductListActivity.class);
        startActivity(intent);
    }

    @Override
    public void openMoveCircleActivity()
    {
        Intent intent = new Intent(this, MoveCircleActivity.class);
        startActivity(intent);
    }

    // endregion
}
