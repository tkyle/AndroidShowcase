package com.tbd.androidshowcase.utility;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Trevor on 9/7/2016.
 */
public interface DemoNoSQLOperationListItem {
    int getViewType();
    View getView(LayoutInflater inflater, View convertView);
}
