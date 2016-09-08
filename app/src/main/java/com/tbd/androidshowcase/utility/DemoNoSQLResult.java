package com.tbd.androidshowcase.utility;

import android.content.Context;
import android.view.View;

/**
 * Created by Trevor on 9/7/2016.
 */
public interface DemoNoSQLResult {
    /**
     * Synchronously update an item.
     */
    void updateItem();

    /**
     * Synchronously delete an item.
     */
    void deleteItem();

    View getView(Context context, final View convertView, int position);
}