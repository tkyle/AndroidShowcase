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
    IProductListView view;
    private NoSQLTableBase productTable;
    private final String tableName = "Products";
    Context _context;
    ArrayList<? extends ITableObject> items;

    public ProductListPresenter(IProductListView view, Context context)
    {
        this.view = view;
        productTable = TableFactory.instance(context).getNoSQLTableByTableName(tableName);
        _context = context;
    }

    public void GetItems()
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
            items = result.get();

            view.RefreshProductsList((ArrayList<Product>)items);

        }catch(final Exception ex)
        {
            view.ShowError(_context.getString(R.string.nosql_dialog_title_failed_operation_text), ex.getMessage());
        }

        // Now we call setRefreshing(false) to signal refresh has finished
        view.SetSwipeContainerRefreshStatus(false);

    }

    public void onAddNewItemClicked(final Product product)
    {
        //view.AddNewItem(product);

        // Obtain a reference to the identity manager.
        // TODO: Implement callable instead so I can return the new product?
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
            final Product newItem = result.get();

            //GetItems();
            this.GetItems();

            view.ShowItemAddedSnackbar(newItem);

        }catch(final Exception ex)
        {
            view.ShowError(_context.getString(R.string.nosql_dialog_title_failed_operation_text), ex.getMessage());
        }

    }

    public void onEditItemClicked(Product product, Boolean isRestore)
    {
        view.EditItem(product, isRestore);
    }

    public void onDeleteItemClicked(Product product)
    {
        view.RemoveItem(product);
    }

}
