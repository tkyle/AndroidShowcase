package com.tbd.androidshowcase.view;

import com.tbd.androidshowcase.model.Product;

import java.util.ArrayList;

/**
 * Created by Trevor on 9/4/2016.
 */
public interface IProductListView
{
    //void AddNewItem(Product product);
    void RemoveItem(Product product);
    void EditItem(Product product, Boolean isRestore);
    //void GetItems();
    void ShowItemAddedSnackbar(Product newItem);
    void ShowError(String message, String ExceptionMessage);
    void RefreshProductsList(ArrayList<Product> items);
    void SetSwipeContainerRefreshStatus(Boolean status);
}
