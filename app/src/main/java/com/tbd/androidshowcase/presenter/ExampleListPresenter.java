package com.tbd.androidshowcase.presenter;

import com.tbd.androidshowcase.model.ExampleItem;
import com.tbd.androidshowcase.view.IExampleListView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Trevor on 9/4/2016.
 */
public class ExampleListPresenter
{
    IExampleListView view;

    public ExampleListPresenter(IExampleListView view) { this.view = view; }

    public ExampleItem[] GetExampleItems()
    {
        return new ExampleItem[]{
                new ExampleItem("Bob Shark", "bshark@example.com"),
                new ExampleItem("Fred Test", "ftest@example.com"),
                new ExampleItem("Susie Fakeperson", "sfakeperson@example.com"),
                new ExampleItem("Anoth Ername", "aername@example.com"),
                new ExampleItem("One More", "omore@example.com")
        };
    }

    public ArrayList<String> GetExampleList()
    {
        // Create and populate a List of friends names.
        String[] exampleList = new String[] { "Bob Shark", "Fred Test", "Susie Fakeperson", "Anoth Ername", "One More"};
        ArrayList<String> exampleSourceList = new ArrayList<String>();
        exampleSourceList.addAll( Arrays.asList(exampleList) );

        return exampleSourceList;
    }
}