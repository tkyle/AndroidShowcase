package com.tbd.androidshowcase.utility;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Trevor on 9/11/2016.
 */
@DynamoDBTable(tableName = "Products")
public interface ITableObject {
}
