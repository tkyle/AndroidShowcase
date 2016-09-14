package com.tbd.androidshowcase.ui.activity;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.presenter.ProductListPresenter;
import com.tbd.androidshowcase.ui.fragment.ProductFragment;
import com.tbd.androidshowcase.user.IdentityManager;
import com.tbd.androidshowcase.utility.AWSMobileClient;
import com.tbd.androidshowcase.tables.ITableObject;
import com.tbd.androidshowcase.tables.NoSQLTableBase;
import com.tbd.androidshowcase.model.Product;
import com.tbd.androidshowcase.ui.adapters.ProductsAdapter;
import com.tbd.androidshowcase.tables.TableFactory;
import com.tbd.androidshowcase.utility.ThreadUtils;
import com.tbd.androidshowcase.view.IProductListView;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProductListActivity extends AppCompatActivity implements IProductListView {

    public static final String BUNDLE_ARGS_TABLE_TITLE = "tableTitle";

    private ProductListPresenter presenter;
    ListView exampleListView;

    ArrayList<? extends ITableObject> items;

    private IdentityManager identityManager;

    private NoSQLTableBase productTable;

    ProductsAdapter productsAdapter;

    FloatingActionButton newButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        presenter = new ProductListPresenter(ProductListActivity.this);

        productsAdapter = new ProductsAdapter(this, new ArrayList<Product>());

        exampleListView = (ListView) findViewById( R.id.exampleListView );
        exampleListView.setAdapter(productsAdapter);

        // FloatingActionButton is using a wrapper (it seems) and it doesn't like the onClick being
        // declared in XML. May be able to fix this by updating the design library.
        // This is working for now.
        newButton = (FloatingActionButton)findViewById(R.id.newButton);
//        newButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                fireCustomDialog(null);
//            }});

        newButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog();
            }});

        registerForContextMenu(exampleListView);

        final String tableName = "Products";//args.getString(BUNDLE_ARGS_TABLE_TITLE);
        productTable = TableFactory.instance(getApplicationContext()).getNoSQLTableByTableName(tableName);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        GetItems();

    }

    public void showDialog()
    {
        FragmentManager fm = getSupportFragmentManager();
        ProductFragment productDialog = ProductFragment.newInstance("Type your name");
        productDialog.show(fm, "fragment_edit_name");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.exampleListView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(((Product)items.get(info.position)).getName());

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
                RemoveItem(((Product)items.get(info.position)).getProductId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // Begin Custom Dialog Code

    private void fireCustomDialog(final ITableObject tableObject) {

        final Product product = tableObject != null ? (Product)tableObject : null;

        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        //TextView titleView = (TextView) dialog.findViewById(R.id.custom_title);

        final EditText editProductName = (EditText) dialog.findViewById(R.id.txtProductName);
        final EditText editProductDescription = (EditText) dialog.findViewById(R.id.txtProductDescription);
        final EditText editProductCost = (EditText) dialog.findViewById(R.id.txtProductCost);


        Button commitButton = (Button) dialog.findViewById(R.id.btnSubmit);
        //LinearLayout rootLayout = (LinearLayout) dialog.findViewById(R.id.custom_root_layout);
        final boolean isEditOperation = (product != null);
        //this is for an edit
        if (isEditOperation) {
//            titleView.setText("Edit Product");

            editProductName.setText(product.getName());
            editProductDescription.setText(product.getDescription());
            editProductCost.setText(product.getCost().toString());

            //rootLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        }
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String productNameText = editProductName.getText().toString();
                String productDescriptionText = editProductDescription.getText().toString();
                String productCostText = editProductCost.getText().toString();

                if (isEditOperation) {

                    Product productEdited = new Product();
                    productEdited.setProductId(product.getProductId());
                    productEdited.setName(productNameText);
                    productEdited.setDescription(productDescriptionText);
                    productEdited.setCost(Double.parseDouble(productCostText));

                    EditItem(productEdited);

                } else {

                    Product newProduct = new Product();

                    newProduct.setProductId(java.util.UUID.randomUUID().toString());
                    newProduct.setName(productNameText);
                    newProduct.setDescription(productDescriptionText);
                    newProduct.setCost(Double.parseDouble(productCostText));

                    AddNewItem(newProduct);
                }

                // Refresh item list
                productsAdapter.clear();
                GetItems();
                dialog.dismiss();
            }
        });

      /*  Button buttonCancel = (Button) dialog.findViewById(R.id.custom_button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/
        dialog.show();
    }

    // End Custom Dialog Code

    @Override
    public void AddNewItem(final Product product)
    {
        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    productTable.addNewItem(product);
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
    public void RemoveItem(final String productId)
    {
        // Obtain a reference to the identity manager.
        //AWSMobileClient.initializeMobileClientIfNecessary(this);

        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    productTable.removeItem(productId);
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
    public void EditItem(final Product product)
    {
        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    productTable.editItem(product);
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

        final Callable<ArrayList<ITableObject>> f = new Callable<ArrayList<ITableObject>>() {

            @Override
            public ArrayList<ITableObject> call() throws Exception {

            return (ArrayList<ITableObject>)productTable.getItems();
        };

        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ArrayList<ITableObject>> result = executor.submit(f);

        try
        {
            items = result.get();

            productsAdapter.clear();

            productsAdapter.addAll((ArrayList<Product>)items);

        }catch(final Exception ex)
        {
            // The insertSampleData call already logs the error, so we only need to
            // show the error dialog to the user at this point.
            createAndShowDialog(getString(R.string.nosql_dialog_title_failed_operation_text), ex.getMessage());
        }
    }


    public void AddSampleItems()
    {
        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //demoTable.insertSampleData();
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


    public void RemoveSampleItems()
    {
        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //demoTable.removeSampleData();
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
