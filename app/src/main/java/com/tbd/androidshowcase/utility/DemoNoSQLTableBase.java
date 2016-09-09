package com.tbd.androidshowcase.utility;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trevor on 9/7/2016.
 */
public abstract class DemoNoSQLTableBase {
    /** The number of sample data entries to be inserted when calling insertSampleData. */
    public static final int SAMPLE_DATA_ENTRIES_PER_INSERT = 20;

    /**
     * @return the name of the table.
     */
    public abstract String getTableName();

    /**
     * @return the primary partition key for the table.
     */
    public abstract String getPartitionKeyName();

    /**
     * @return the human readable partition key type.
     */
    public abstract String getPartitionKeyType();
    /**
     * @return the secondary partition key for the table.
     */
    public abstract String getSortKeyName();

    /**
     * @return the human readable sort key type.
     */
    public abstract String getSortKeyType();

    /**
     * @return the number of secondary indexes for the table.
     */
    public abstract int getNumIndexes();

    /**
     * Insert Sample data into the table.
     */
    public abstract void insertSampleData();

    /**
     * Remove Sample data from the table.
     */
    public abstract void removeSampleData();

    /**
     * Add new Item to table.
     */
    public abstract void addNewItem(NotesDO note);

    /**
     * Remove item from table.
     */
    public abstract void removeItem(String noteId);

    /**
     * edit item in table.
     */
    public abstract void editItem(NotesDO note);

    /**
     * get items from table.
     */
    public abstract List<NotesDO>  getItems();

    /**
     * Handler interface to retrieve the supported table operations.
     */
    public interface SupportedDemoOperationsHandler {
        /**
         * @param supportedOperations the list of supported table operations.
         */
        void onSupportedOperationsReceived(List<DemoNoSQLOperationListItem> supportedOperations);
    }

    /**
     * Get a list of supported demo operations.
     * @return a list of support get, query, and scan operations.
     */
    public abstract void getSupportedDemoOperations(Context context, SupportedDemoOperationsHandler opsHandler);
}