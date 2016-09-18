package com.tbd.androidshowcase.ui.activity;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;

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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProductListActivity extends AppCompatActivity implements IProductListView, ProductFragment.ProductFragmentListener {

    // region Fields

//    private final String tableName = "Products";
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeContainer;
    private ProductListPresenter presenter;
    ListView exampleListView;
    ArrayList<? extends ITableObject> items;
    private NoSQLTableBase productTable;
    ProductsAdapter productsAdapter;
    FloatingActionButton newButton;

    // endregion

    // region Constructor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        presenter = new ProductListPresenter(ProductListActivity.this, this.getApplicationContext());
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        setupSwipeContainer();

        setupProductsList();

        setupActionButton();

        setupActionBar();

        //final String tableName = "Products";//args.getString(BUNDLE_ARGS_TABLE_TITLE);
        //productTable = TableFactory.instance(getApplicationContext()).getNoSQLTableByTableName(tableName);

        // Get Products list on Activity creation
        //GetItems();
        presenter.GetItems();
    }

    // endregion

    // region Setup Methods

    private void setupSwipeContainer()
    {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // Pull to refresh for products list.
                //GetItems();
                presenter.GetItems();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupProductsList()
    {
        items = new ArrayList<Product>();

        productsAdapter = new ProductsAdapter(this, (ArrayList<Product>)items);

        exampleListView = (ListView) findViewById( R.id.exampleListView );
        exampleListView.setAdapter(productsAdapter);

        registerForContextMenu(exampleListView);
    }

    private void setupActionButton()
    {
        // FloatingActionButton is using a wrapper (it seems) and it doesn't like the onClick being
        // declared in XML. May be able to fix this by updating the design library.
        // This is working for now.
        newButton = (FloatingActionButton)findViewById(R.id.newButton);
        newButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(true, new Product(java.util.UUID.randomUUID().toString()));
            }});
    }

    private void setupActionBar()
    {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Product List");
    }

    // endregion

    public void showDialog(Boolean isNew, Product product)
    {
        FragmentManager fm = getSupportFragmentManager();
        ProductFragment productDialog = ProductFragment.newInstance("Type your name", product, isNew);
        productDialog.show(fm, "fragment_product");
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
                showDialog(false, (Product)items.get(info.position));
                return true;
            case R.id.delete:
                RemoveItem((Product)items.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onFinishEditDialog(Product product, Boolean isNew) {
        if(isNew)
            presenter.onAddNewItemClicked(product);
        else
        {
            presenter.onEditItemClicked(product, false);
            //EditItem(product, false);
        }
        /*        if(isNew)
            AddNewItem(product);
        else
        {
            EditItem(product, false);
        }*/
    }

    @Override
    public void ShowItemAddedSnackbar(final Product newItem)
    {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item added", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view){

                        RemoveItem(newItem);
                    }
                });
        snackbar.show();
    }

    @Override
    public void ShowError(String message, String exceptionMessage)
    {
        createAndShowDialog(getString(R.string.nosql_dialog_title_failed_operation_text), exceptionMessage);
    }

    /*@Override
    public void AddNewItem(final Product product)
    {
       // Obtain a reference to the identity manager.
        // TODO: Implement callable instead so I can return the new product?
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        final Callable<Product> f = new Callable<Product>() {

            @Override
            public Product call() throws Exception {

                return (Product)productTable.addNewItem(product);
            };

        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Product> result = executor.submit(f);

        try
        {
            final Product newItem = result.get();

            //GetItems();
            presenter.GetItems();

            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item added", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view){

                            RemoveItem(newItem);
                        }
                    });
            snackbar.show();

        }catch(final Exception ex)
        {
            createAndShowDialog(getString(R.string.nosql_dialog_title_failed_operation_text), ex.getMessage());
        }
    }

    /*private void refreshProductsList()
    {
        exampleListView.invalidate();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                productsAdapter.refreshProducts((ArrayList<Product>)items);
            }
        });
    }*/
    @Override
    public void RefreshProductsList(final ArrayList<Product> products)
    {
        exampleListView.invalidate();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                productsAdapter.refreshProducts(products);
            }
        });
    }

    @Override
    public void SetSwipeContainerRefreshStatus(final Boolean status)
    {
        swipeContainer.setRefreshing(status);
    }

    @Override
    public void RemoveItem(final Product product)
    {
        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        final Callable<Void> f = new Callable<Void>() {

            @Override
            public Void call() throws Exception {

                productTable.removeItem(product.getProductId());

                return null;
            };
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> result = executor.submit(f);

        try
        {
            final Void voidResult = result.get();

            //GetItems();
            presenter.GetItems();

            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item Removed", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view){

                            //AddNewItem(product);
                            presenter.onAddNewItemClicked(product);
                        }
                    });
            snackbar.show();

        }catch(final Exception ex)
        {
            createAndShowDialog(getString(R.string.nosql_dialog_title_failed_operation_text), ex.getMessage());
        }
    }

    @Override
    public void EditItem(final Product product, final Boolean isRestore)
    {
        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        final Callable<Product> f = new Callable<Product>() {

            @Override
            public Product call() throws Exception {

                return (Product)productTable.editItem(product);
            };

        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Product> result = executor.submit(f);

        try
        {
            final Product originalProduct = result.get();

            //GetItems();
            presenter.GetItems();

            Snackbar snackbar = Snackbar.make(coordinatorLayout, isRestore ? "Item Restored" : "Item Updated", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view){

                            EditItem(originalProduct, !isRestore);
                        }
                    });
            snackbar.show();

        }catch(final Exception ex)
        {
            createAndShowDialog(getString(R.string.nosql_dialog_title_failed_operation_text), ex.getMessage());
        }
    }

   /* @Override
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

            refreshProductsList();

        }catch(final Exception ex)
        {
            createAndShowDialog(getString(R.string.nosql_dialog_title_failed_operation_text), ex.getMessage());
        }

        // Now we call setRefreshing(false) to signal refresh has finished
        swipeContainer.setRefreshing(false);

    }*/

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
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
}
