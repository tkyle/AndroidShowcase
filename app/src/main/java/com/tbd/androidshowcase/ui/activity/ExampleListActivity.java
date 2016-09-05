package com.tbd.androidshowcase.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.presenter.ExampleListPresenter;
import com.tbd.androidshowcase.view.IExampleListView;

public class ExampleListActivity extends AppCompatActivity implements IExampleListView {

    private ExampleListPresenter presenter;
    ArrayAdapter<String> listAdapter;
    ListView exampleListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        presenter = new ExampleListPresenter(ExampleListActivity.this);

        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, presenter.GetExampleList());
        //listAdapter = new ArrayAdapter<String>(this, R.layout.examplelist_row, presenter.GetExampleList());

        // Set the ArrayAdapter as the ListView's adapter.
        exampleListView = (ListView) findViewById( R.id.exampleListView );
        exampleListView.setAdapter( listAdapter );
        exampleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // TODO: Need to use example objects not strings
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //AddFriendToList(position);
            }
        });
    }
}
