package com.tbd.androidshowcase.tables;

import com.tbd.androidshowcase.tables.ITableObject;

import java.util.ArrayList;

/**
 * Created by Trevor on 9/11/2016.
 */
public abstract class NoSQLTableBase
{
    public abstract String getTableName();
    public abstract ITableObject addNewItem(ITableObject tableObject);
    public abstract void removeItem(String objectId);
    public abstract ITableObject editItem(ITableObject tableObject);
    public abstract ArrayList<ITableObject> getItems();
}
