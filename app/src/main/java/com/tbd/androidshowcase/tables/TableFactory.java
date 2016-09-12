package com.tbd.androidshowcase.tables;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Trevor on 9/11/2016.
 */
public class TableFactory {

    /** Singleton instance. */
    private volatile static TableFactory instance;

    /** Map containing an instance of each of the supporting tables by table name. */
    private LinkedHashMap<String, NoSQLTableBase> supportedTablesMap = new LinkedHashMap<>();

    TableFactory(final Context context) {
        final List<NoSQLTableBase> supportedTablesList = new ArrayList<>();
        supportedTablesList.add(new ProductTable());
        for (final NoSQLTableBase table : supportedTablesList) {
            supportedTablesMap.put(table.getTableName(), table);
        }
    }

    public synchronized static TableFactory instance(final Context context) {
        if (instance == null) {
            instance = new TableFactory(context);
        }
        return instance;
    }

    public Collection<NoSQLTableBase> getNoSQLSupportedTables() {
        return supportedTablesMap.values();
    }


    public <T extends NoSQLTableBase> T getNoSQLTableByTableName(final String tableName) {
        return (T) supportedTablesMap.get(tableName);
    }
}
