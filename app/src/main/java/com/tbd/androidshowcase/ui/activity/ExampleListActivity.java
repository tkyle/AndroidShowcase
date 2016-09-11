package com.tbd.androidshowcase.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.regions.Regions;
import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.presenter.ExampleListPresenter;
import com.tbd.androidshowcase.user.IdentityManager;
import com.tbd.androidshowcase.utility.AWSMobileClient;
import com.tbd.androidshowcase.utility.DemoNoSQLOperationListAdapter;
import com.tbd.androidshowcase.utility.DemoNoSQLOperationListItem;
import com.tbd.androidshowcase.utility.DemoNoSQLTableBase;
import com.tbd.androidshowcase.utility.DemoNoSQLTableFactory;
import com.tbd.androidshowcase.utility.DynamoDBUtils;
import com.tbd.androidshowcase.utility.NotesAdapter;
import com.tbd.androidshowcase.utility.NotesDO;
import com.tbd.androidshowcase.utility.ThreadUtils;
import com.tbd.androidshowcase.view.IExampleListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class ExampleListActivity extends AppCompatActivity implements IExampleListView {

    public static final String BUNDLE_ARGS_TABLE_TITLE = "tableTitle";

    private ExampleListPresenter presenter;
    ArrayAdapter<String> listAdapter;
    ListView exampleListView;
    ArrayList<String> exampleList;

    ArrayList<NotesDO> items;

    private IdentityManager identityManager;

    private DemoNoSQLTableBase demoTable;

    NotesAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        presenter = new ExampleListPresenter(ExampleListActivity.this);

        exampleList = presenter.GetExampleList();
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, exampleList);

        notesAdapter = new NotesAdapter(this, new ArrayList<NotesDO>());

        exampleListView = (ListView) findViewById( R.id.exampleListView );
        exampleListView.setAdapter(notesAdapter);

        //listAdapter = new ArrayAdapter<String>(this, R.layout.examplelist_row, presenter.GetExampleList());

        //presenter.onGetItemsClicked();
        // Set the ArrayAdapter as the ListView's adapter.

        //exampleListView.setAdapter( listAdapter );
        registerForContextMenu(exampleListView);

        //final Bundle args = getArguments();

        final String tableName = "Notes";//args.getString(BUNDLE_ARGS_TABLE_TITLE);
        demoTable = DemoNoSQLTableFactory.instance(getApplicationContext()).getNoSQLTableByTableName(tableName);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }

    public void onGetUserIdClicked(View button){ presenter.onGetUserIdClicked();}

    public void onAddSampleItemsClicked(View button){ presenter.onAddSampleItemsClicked();}
    public void onRemoveSampleItemsClicked(View button){ presenter.onRemoveSampleItemsClicked();}

    public void onNewItemClicked(View button){ fireCustomDialog(null); }
    //public void onRemoveItemClicked(View button){ presenter.onRemoveItemClicked();}
    //public void onEditItemClicked(View button){ presenter.onEditItemClicked();}
    public void onGetItemsClicked(View button){ presenter.onGetItemsClicked();}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.exampleListView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(items.get(info.position).getContent());

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.note_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.edit:
                //EditItem();
                fireCustomDialog(items.get(info.position));
                return true;
            case R.id.delete:
                RemoveItem(items.get(info.position).getNoteId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // Begin Custom Dialog Code

    private void fireCustomDialog(final NotesDO note) {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        TextView titleView = (TextView) dialog.findViewById(R.id.custom_title);
        final EditText editCustom = (EditText) dialog.findViewById(R.id.custom_edit_note);
        Button commitButton = (Button) dialog.findViewById(R.id.custom_button_commit);
        LinearLayout rootLayout = (LinearLayout) dialog.findViewById(R.id.custom_root_layout);
        final boolean isEditOperation = (note != null);
        //this is for an edit
        if (isEditOperation) {
            titleView.setText("Edit Note");
            editCustom.setText(note.getContent());
            rootLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        }
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteText = editCustom.getText().toString();
                if (isEditOperation) {
                    NotesDO noteEdited = new NotesDO();
                    noteEdited.setNoteId(note.getNoteId());
                    noteEdited.setContent(noteText);

                    //mDbAdapter.updateReminder(reminderEdited);
                    EditItem(noteEdited);
                    //this is for new reminder
                } else {
                    //mDbAdapter.createReminder(reminderText, checkBox.isChecked());
                    NotesDO newNote = new NotesDO();
                    newNote.setContent(editCustom.getText().toString());
                    newNote.setNoteId(java.util.UUID.randomUUID().toString());
                    newNote.setTitle("NEW TITLE");

                    AddNewItem(newNote);
                }

                // Refresh item list
                notesAdapter.clear();
                GetItems();
                //mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                dialog.dismiss();
            }
        });

        Button buttonCancel = (Button) dialog.findViewById(R.id.custom_button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // End Custom Dialog Code

    @Override
    public void AddNewItem(final NotesDO note)
    {
        // Obtain a reference to the identity manager.
        //AWSMobileClient.initializeMobileClientIfNecessary(this);

        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    demoTable.addNewItem(note);
                } catch (final AmazonClientException ex) {
                    // The insertSampleData call already logs the error, so we only need to
                    // show the error dialog to the user at this point.
                    createAndShowDialog(getString(R.string.nosql_dialog_title_failed_operation_text), ex.getMessage());
                    return;
                }
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createAndShowDialog(getString(R.string.nosql_dialog_message_added_sample_data_text), getString(R.string.nosql_dialog_title_added_sample_data_text)); }
                });
            }
        }).start();



    }

    @Override
    public void GetUserId()
    {
        identityManager = AWSMobileClient.defaultMobileClient().getIdentityManager();
        fetchUserIdentity();
    }

    @Override
    public void RemoveItem(final String noteId)
    {
        // Obtain a reference to the identity manager.
        //AWSMobileClient.initializeMobileClientIfNecessary(this);

        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    demoTable.removeItem(noteId);
                } catch (final AmazonClientException ex) {
                    // The insertSampleData call already logs the error, so we only need to
                    // show the error dialog to the user at this point.
                    createAndShowDialog(getString(R.string.nosql_dialog_title_failed_operation_text), ex.getMessage());
                    return;
                }
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createAndShowDialog(getString(R.string.nosql_dialog_message_added_sample_data_text), getString(R.string.nosql_dialog_title_added_sample_data_text)); }
                });
            }
        }).start();
    }

    @Override
    public void EditItem(final NotesDO note)
    {
        // Obtain a reference to the identity manager.
        //AWSMobileClient.initializeMobileClientIfNecessary(this);

        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    demoTable.editItem(note);
                } catch (final AmazonClientException ex) {
                    // The insertSampleData call already logs the error, so we only need to
                    // show the error dialog to the user at this point.
                    createAndShowDialog(getString(R.string.nosql_dialog_title_failed_operation_text), ex.getMessage());
                    return;
                }
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createAndShowDialog(getString(R.string.nosql_dialog_message_added_sample_data_text), getString(R.string.nosql_dialog_title_added_sample_data_text)); }
                });
            }
        }).start();
    }

    private boolean getItemsDone = false;

    boolean getItemsDone() {
        return getItemsDone;
    }

    @Override
    public void GetItems()
    {
        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        final Callable<ArrayList<NotesDO>> f = new Callable<ArrayList<NotesDO>>() {

            @Override
            public ArrayList<NotesDO> call() throws Exception {

            return (ArrayList<NotesDO>)demoTable.getItems();
        };

        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ArrayList<NotesDO>> result = executor.submit(f);

        try
        {
            items = result.get();

            notesAdapter.addAll(items);

        }catch(final Exception ex)
        {
            // The insertSampleData call already logs the error, so we only need to
            // show the error dialog to the user at this point.
            createAndShowDialog(getString(R.string.nosql_dialog_title_failed_operation_text), ex.getMessage());
        }
    }

    @Override
    public void AddSampleItems()
    {
        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    demoTable.insertSampleData();
                } catch (final AmazonClientException ex) {
                    // The insertSampleData call already logs the error, so we only need to
                    // show the error dialog to the user at this point.
                    createAndShowDialog(getString(R.string.nosql_dialog_title_failed_operation_text), ex.getMessage());
                    return;
                }
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createAndShowDialog(getString(R.string.nosql_dialog_message_added_sample_data_text), getString(R.string.nosql_dialog_title_added_sample_data_text)); }
                });
            }
        }).start();

    }

    @Override
    public void RemoveSampleItems()
    {
        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    demoTable.removeSampleData();
                } catch (final AmazonClientException ex) {
                    // The insertSampleData call already logs the error, so we only need to
                    // show the error dialog to the user at this point.
                    createAndShowDialog(getString(R.string.nosql_dialog_title_failed_operation_text), ex.getMessage());
                    return;
                }
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() { createAndShowDialog("All Sample Items have been removed from your table.", "Removed Sample Data");}
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
