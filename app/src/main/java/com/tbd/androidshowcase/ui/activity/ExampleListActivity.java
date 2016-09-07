package com.tbd.androidshowcase.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.presenter.ExampleListPresenter;
import com.tbd.androidshowcase.view.IExampleListView;

import java.util.ArrayList;

public class ExampleListActivity extends AppCompatActivity implements IExampleListView {

    private ExampleListPresenter presenter;
    ArrayAdapter<String> listAdapter;
    ListView exampleListView;
    ArrayList<String> exampleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        presenter = new ExampleListPresenter(ExampleListActivity.this);

        exampleList = presenter.GetExampleList();
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, exampleList);
        //listAdapter = new ArrayAdapter<String>(this, R.layout.examplelist_row, presenter.GetExampleList());

        // Set the ArrayAdapter as the ListView's adapter.
        exampleListView = (ListView) findViewById( R.id.exampleListView );
        exampleListView.setAdapter( listAdapter );
        registerForContextMenu(exampleListView);

    }

    public void onNewItemClicked(View button){ presenter.onNewItemClicked();}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.exampleListView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(exampleList.get(info.position));

            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

}
