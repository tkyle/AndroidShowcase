package com.tbd.androidshowcase.view;

import com.tbd.androidshowcase.utility.NotesDO;

/**
 * Created by Trevor on 9/4/2016.
 */
public interface IExampleListView
{
    void GetUserId();
    void AddSampleItems();
    void RemoveSampleItems();

    void AddNewItem(NotesDO note);
    void RemoveItem(String noteId);
    void EditItem(NotesDO note);
    void GetItems();
}
