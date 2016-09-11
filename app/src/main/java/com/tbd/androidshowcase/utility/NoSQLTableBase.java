package com.tbd.androidshowcase.utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trevor on 9/11/2016.
 */
public abstract class NoSQLTableBase
{
    public abstract String getTableName();
    public abstract void addNewItem(ITableObject tableObject);
    public abstract void removeItem(String objectId);
    public abstract void editItem(ITableObject tableObject);
    public abstract ArrayList<ITableObject> getItems();
}
