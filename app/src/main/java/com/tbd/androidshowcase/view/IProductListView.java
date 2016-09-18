package com.tbd.androidshowcase.view;

import com.tbd.androidshowcase.model.Product;

import java.util.ArrayList;

/**
 * Created by Trevor on 9/4/2016.
 */
public interface IProductListView
{
    void ShowProductAddedSnackbar(Product newItem);
    void ShowEditProductSnackbar(Product originalItem, Boolean isRestore);
    void ShowRemoveProductSnackbar(Product product);
    void ShowError(String message, String ExceptionMessage);
    void RefreshProductsList(ArrayList<Product> items);
    void SetSwipeContainerRefreshStatus(Boolean status);
}
