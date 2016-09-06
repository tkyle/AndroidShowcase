package com.tbd.androidshowcase.ui.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.model.ExampleItem;
import com.tbd.androidshowcase.model.todoitem;
import com.tbd.androidshowcase.model.todoitem;
import com.tbd.androidshowcase.presenter.ExampleListPresenter;
import com.tbd.androidshowcase.ui.adapter.ToDoItemAdapter;
import com.tbd.androidshowcase.view.IExampleListView;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ExampleListActivity extends AppCompatActivity implements IExampleListView {

    private ExampleListPresenter presenter;
    ArrayAdapter<String> listAdapter;
    ListView exampleListView;
    ArrayList<String> exampleList;

    // Todo Item stuff
    private MobileServiceClient mClient;
    private MobileServiceTable<todoitem> mToDoTable;
    private ToDoItemAdapter mAdapter;

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

        try
        {
            mClient = new MobileServiceClient("https://androidshowcase.azurewebsites.net", this);
        }
        catch(MalformedURLException ex)
        {
            Log.e("URL was malformed", ex.getMessage());
        }

    }

    public void onNewItemClicked(View button){ presenter.onNewItemClicked();}

    @Override
    public void AddNewItem()
    {
        todoitem item = new todoitem();
        item.setText("Awesome item");
        MobileServiceTable tbl = mClient.getTable("todoitem", todoitem.class);

        try
        {
            tbl.insert(item);
        }
        catch(Exception ex)
        {
            int x = 4;
        }



//        mClient.getTable("todoitem", todoitem.class).insert(item, new TableOperationCallback<todoitem>() {
//            public void onCompleted(todoitem entity, Exception exception, ServiceFilterResponse response) {
//                if (exception == null) {
//
//                    Toast.makeText(getApplicationContext(), "Insert Successful", Toast.LENGTH_SHORT).show();
//
//                } else {
//
//                    Toast.makeText(getApplicationContext(), "Insert Failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

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

    // TODO: Need to get rid of this and not have a public method being called from an instance of the activity.
    public void checkItem(final todoitem item) {
        if (mClient == null) {
            return;
        }

        // Set the item as completed and update it in the table
        item.setComplete(true);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    checkItemInTable(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (item.isComplete()) {
                                mAdapter.remove(item);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);

    }

    /**
     * Mark an item as completed in the Mobile Service Table
     *
     * @param item
     *            The item to mark
     */
    public void checkItemInTable(todoitem item) throws ExecutionException, InterruptedException
    {
        mToDoTable.update(item).get();
    }

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception.getMessage(), "Error");
            }
        });
    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }
}
