package com.tbd.androidshowcase.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.presenter.ExampleListPresenter;
import com.tbd.androidshowcase.view.IExampleListView;

public class ExampleListActivity extends AppCompatActivity implements IExampleListView {

    private ExampleListPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        presenter = new ExampleListPresenter(ExampleListActivity.this);
    }
}
