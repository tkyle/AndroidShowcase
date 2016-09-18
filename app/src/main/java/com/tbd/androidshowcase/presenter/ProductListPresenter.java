package com.tbd.androidshowcase.presenter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.model.Product;
import com.tbd.androidshowcase.tables.ITableObject;
import com.tbd.androidshowcase.tables.NoSQLTableBase;
import com.tbd.androidshowcase.tables.TableFactory;
import com.tbd.androidshowcase.utility.AWSMobileClient;
import com.tbd.androidshowcase.view.IProductListView;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Trevor on 9/4/2016.
 */
public class ProductListPresenter
{
    // region Fields

    IProductListView view;
    private NoSQLTableBase productTable;
    private final String tableName = "Products";
    Context _context;
    ArrayList<? extends ITableObject> products;

    // endregion

    // region Constructor

    public ProductListPresenter(IProductListView view, Context context)
    {
        this.view = view;
        productTable = TableFactory.instance(context).getNoSQLTableByTableName(tableName);
        _context = context;
    }

    // endregion

    // region Methods

    public void GetProducts()
    {
        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(_context);

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
            // Wait for the results
            products = result.get();

            view.RefreshProductsList((ArrayList<Product>)products);

        }catch(final Exception ex)
        {
            view.ShowError(_context.getString(R.string.failed_operation), ex.getMessage());
        }

        // setRefreshing(false) to signal refresh has finished
        view.SetSwipeContainerRefreshStatus(false);

    }

    public void AddProduct(final Product product)
    {
        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(_context);

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
            // Wait for the results
            final Product newItem = result.get();

            // Refresh the list
            this.GetProducts();

            // Show snackbar to give the user a chance to remove the product from the list
            view.ShowProductAddedSnackbar(newItem);

        }catch(final Exception ex)
        {
            view.ShowError(_context.getString(R.string.failed_operation), ex.getMessage());
        }
    }

    public void EditProduct(final Product product, Boolean isRestore)
    {
        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(_context);

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
            // Wait for the results
            final Product originalProduct = result.get();

            // Refresh list
            this.GetProducts();

            // Give the user a chance to undo the edit
            view.ShowEditProductSnackbar(originalProduct, !isRestore);

        }catch(final Exception ex)
        {
            view.ShowError(_context.getString(R.string.failed_operation), ex.getMessage());
        }
    }

    public void RemoveProduct(final Product product)
    {
        // Obtain a reference to the identity manager.
        AWSMobileClient.initializeMobileClientIfNecessary(_context);

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
            // Wait for the results
            final Void voidResult = result.get();

            // Refresh
            this.GetProducts();

            // Give the user a chance to undo the delete
            view.ShowRemoveProductSnackbar(product);

        }catch(final Exception ex)
        {
            view.ShowError(_context.getString(R.string.failed_operation), ex.getMessage());
        }
    }

    // endregion
}
