package com.tbd.androidshowcase.presenter;

import com.tbd.androidshowcase.view.IMainView;

/**
 * Created by Trevor on 9/4/2016.
 */
public class MainPresenter
{
    IMainView view;

    public MainPresenter(IMainView view)
    {
        this.view = view;
    }

    public void onProductListActivityClicked()
    {
        view.openProductListActivity();
    }

    public void onMoveCircleActivityClicked()
    {
        view.openMoveCircleActivity();
    }
}
