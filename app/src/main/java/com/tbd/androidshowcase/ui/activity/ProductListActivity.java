package com.tbd.androidshowcase.ui.activity;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.presenter.ProductListPresenter;
import com.tbd.androidshowcase.ui.fragment.ProductFragment;
import com.tbd.androidshowcase.tables.ITableObject;
import com.tbd.androidshowcase.model.Product;
import com.tbd.androidshowcase.ui.adapters.ProductsAdapter;
import com.tbd.androidshowcase.view.IProductListView;
import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity implements IProductListView, ProductFragment.ProductFragmentListener {

    // region Fields

    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeContainer;
    private ProductListPresenter presenter;
    ListView productsListView;
    ArrayList<? extends ITableObject> productList;
    ProductsAdapter productsAdapter;
    FloatingActionButton newButton;

    // endregion

    // region Constructor

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Grab the needed view references for the activity
        SetupViewsAndContainers();

        // Get Products list on Activity creation
        presenter.GetProducts();
    }

    // endregion

    // region Setup Methods

    private void SetupViewsAndContainers()
    {
        presenter = new ProductListPresenter(ProductListActivity.this, this.getApplicationContext());
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        setupSwipeContainer();

        setupProductsList();

        setupActionButton();

        setupActionBar();
    }

    private void setupSwipeContainer()
    {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // Pull to refresh for products list.
                presenter.GetProducts();
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
        productList = new ArrayList<Product>();

        productsAdapter = new ProductsAdapter(this, (ArrayList<Product>)productList);

        productsListView = (ListView) findViewById( R.id.productsListView );
        productsListView.setAdapter(productsAdapter);

        registerForContextMenu(productsListView);
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

    // region Methods

    public void showDialog(Boolean isNew, Product product)
    {
        FragmentManager fm = getSupportFragmentManager();
        ProductFragment productDialog = ProductFragment.newInstance("Type your name", product, isNew);
        productDialog.show(fm, "fragment_product");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.productsListView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(((Product)productList.get(info.position)).getName());

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.note_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.edit:
                showDialog(false, (Product)productList.get(info.position));
                return true;
            case R.id.delete:
                presenter.onDeleteItemClicked((Product)productList.get(info.position));
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
            presenter.onEditItemClicked(product, false);
    }

    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    // endregion

    // region IProductListView Implementation

    @Override
    public void ShowProductAddedSnackbar(final Product newItem)
    {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item added", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view){

                        presenter.onDeleteItemClicked(newItem);
                    }
                });
        snackbar.show();
    }

    @Override
    public void ShowEditProductSnackbar(final Product originalProduct, final Boolean isRestore)
    {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, isRestore ? "Item Restored" : "Item Updated", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view){

                        presenter.onEditItemClicked(originalProduct, !isRestore);
                    }
                });

        snackbar.show();
    }

    @Override
    public void ShowRemoveProductSnackbar(final Product product)
    {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item Removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view){

                        //AddNewItem(product);
                        presenter.onAddNewItemClicked(product);
                    }
                });

        snackbar.show();
    }

    @Override
    public void ShowError(String message, String exceptionMessage)
    {
        createAndShowDialog(getString(R.string.nosql_dialog_title_failed_operation_text), exceptionMessage);
    }

    @Override
    public void RefreshProductsList(final ArrayList<Product> products)
    {
        productsListView.invalidate();

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

    // endregion

}
