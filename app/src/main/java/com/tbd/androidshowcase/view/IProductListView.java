package com.tbd.androidshowcase.view;

import com.tbd.androidshowcase.model.Product;

/**
 * Created by Trevor on 9/4/2016.
 */
public interface IProductListView
{
    void AddNewItem(Product product);
    void RemoveItem(Product product);
    void EditItem(Product product);
    void GetItems();
}
