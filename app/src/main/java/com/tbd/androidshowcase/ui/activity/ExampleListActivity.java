package com.tbd.androidshowcase.ui.activity;

import android.content.Context;
import android.content.Intent;
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

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.regions.Regions;
import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.presenter.ExampleListPresenter;
import com.tbd.androidshowcase.user.IdentityManager;
import com.tbd.androidshowcase.utility.AWSMobileClient;
import com.tbd.androidshowcase.utility.DemoNoSQLTableBase;
import com.tbd.androidshowcase.utility.DemoNoSQLTableFactory;
import com.tbd.androidshowcase.utility.DynamoDBUtils;
import com.tbd.androidshowcase.utility.ThreadUtils;
import com.tbd.androidshowcase.view.IExampleListView;

import java.util.ArrayList;

public class ExampleListActivity extends AppCompatActivity implements IExampleListView {

    public static final String BUNDLE_ARGS_TABLE_TITLE = "tableTitle";

    private ExampleListPresenter presenter;
    ArrayAdapter<String> listAdapter;
    ListView exampleListView;
    ArrayList<String> exampleList;

    private IdentityManager identityManager;

    private DemoNoSQLTableBase demoTable;

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

        //final Bundle args = getArguments();

        final String tableName = "Notes";//args.getString(BUNDLE_ARGS_TABLE_TITLE);
        demoTable = DemoNoSQLTableFactory.instance(getApplicationContext()).getNoSQLTableByTableName(tableName);    }

    public void onNewItemClicked(View button){ presenter.onNewItemClicked();}
    public void onAddSampleItemsClicked(View button){ presenter.onAddSampleItemsClicked();}
    public void onRemoveSampleItemsClicked(View button){ presenter.onRemoveSampleItemsClicked();}

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

    @Override
    public void AddNewItem()
    {
        // Obtain a reference to the identity manager.
        //AWSMobileClient.initializeMobileClientIfNecessary(this);

        identityManager = AWSMobileClient.defaultMobileClient().getIdentityManager();
        fetchUserIdentity();
    }

    @Override
    public void AddSampleItems()
    {
        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        //identityManager = AWSMobileClient.defaultMobileClient().getIdentityManager();
        //fetchUserIdentity();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    demoTable.insertSampleData();
                } catch (final AmazonClientException ex) {
                    // The insertSampleData call already logs the error, so we only need to
                    // show the error dialog to the user at this point.
                    //DynamoDBUtils.showErrorDialogForServiceException(getActivity(), getString(R.string.nosql_dialog_title_failed_operation_text), ex);
                    createAndShowDialog(getString(R.string.nosql_dialog_title_failed_operation_text), ex.getMessage());
                    return;
                }
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createAndShowDialog(getString(R.string.nosql_dialog_message_added_sample_data_text), getString(R.string.nosql_dialog_title_added_sample_data_text));
//                        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getActivity());
//                        dialogBuilder.setTitle(R.string.nosql_dialog_title_added_sample_data_text);
//                        dialogBuilder.setMessage(R.string.nosql_dialog_message_added_sample_data_text);
//                        dialogBuilder.setNegativeButton(R.string.nosql_dialog_ok_text, null);
//                        dialogBuilder.show();
                    }
                });
            }
        }).start();

    }

    @Override
    public void RemoveSampleItems()
    {
        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        //identityManager = AWSMobileClient.defaultMobileClient().getIdentityManager();
        //fetchUserIdentity();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    demoTable.removeSampleData();
                } catch (final AmazonClientException ex) {
                    // The insertSampleData call already logs the error, so we only need to
                    // show the error dialog to the user at this point.
                    //DynamoDBUtils.showErrorDialogForServiceException(getActivity(), getString(R.string.nosql_dialog_title_failed_operation_text), ex);
                    createAndShowDialog(getString(R.string.nosql_dialog_title_failed_operation_text), ex.getMessage());
                    return;
                }
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createAndShowDialog("All Sample Items have been removed from your table.", "Removed Sample Data");
//                        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getActivity());
//                        dialogBuilder.setTitle(R.string.nosql_dialog_title_added_sample_data_text);
//                        dialogBuilder.setMessage(R.string.nosql_dialog_message_added_sample_data_text);
//                        dialogBuilder.setNegativeButton(R.string.nosql_dialog_ok_text, null);
//                        dialogBuilder.show();
                    }
                });
            }
        }).start();

    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }

    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }

        createAndShowDialog(ex.getMessage(), title);
    }

    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    private void fetchUserIdentity()
    {
        //Log.d(LOG_TAG, "fetchUserIdentity");
        Log.d("ListActivity", "fetchUserIdentity");

        AWSMobileClient.defaultMobileClient().getIdentityManager().getUserID(new IdentityManager.IdentityHandler() {

        @Override
        public void handleIdentityID(String identityId) {

            Log.d("LogTag", "my ID is: " + identityId);
        }

        @Override
        public void handleError(Exception exception)
        {
            createAndShowDialogFromTask(exception, "Error");
        }
    });
    }

}
