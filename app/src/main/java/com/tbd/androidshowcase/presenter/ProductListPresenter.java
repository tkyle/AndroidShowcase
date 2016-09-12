package com.tbd.androidshowcase.presenter;

import com.tbd.androidshowcase.view.IProductListView;

/**
 * Created by Trevor on 9/4/2016.
 */
public class ProductListPresenter
{
    IProductListView view;

    public ProductListPresenter(IProductListView view) { this.view = view; }

    public void onGetItemsClicked()
    {
        view.GetItems();
    }

    public void onGetUserIdClicked()
    {
        view.GetUserId();
    }

}
