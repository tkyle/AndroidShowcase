package com.tbd.androidshowcase.utility;

import java.util.List;

/**
 * Created by Trevor on 9/11/2016.
 */
public abstract class NoSQLTableBase
{
    public abstract String getTableName();
    public abstract void addNewItem(ITableObject note);
    public abstract void removeItem(String noteId);
    public abstract void editItem(NotesDO note);
    public abstract List<NotesDO> getItems();
}
