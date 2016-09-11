package com.tbd.androidshowcase.view;

import com.tbd.androidshowcase.utility.NotesDO;
import com.tbd.androidshowcase.utility.Product;

/**
 * Created by Trevor on 9/4/2016.
 */
public interface IExampleListView
{
    void GetUserId();
    void AddSampleItems();
    void RemoveSampleItems();

    void AddNewItem(Product product);
    void RemoveItem(String noteId);
    void EditItem(NotesDO note);
    void GetItems();
}
