package com.tbd.androidshowcase.utility;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Trevor on 9/11/2016.
 */
@DynamoDBTable(tableName = "Products")
public class Product implements ITableObject
{
    private String _productId;
    private String _name;
    private String _description;
    private Double _cost;

    @DynamoDBHashKey(attributeName = "ProductId")
    @DynamoDBIndexHashKey(attributeName = "ProductId")
    public String getProductId() {
        return _productId;
    }
    public void setproductId(final String _productId) {
        this._productId = _productId;
    }

    @DynamoDBAttribute(attributeName = "Name")
    public String getName() {
        return _name;
    }
    public void setName(final String _name) {
        this._name = _name;
    }

    @DynamoDBAttribute(attributeName = "Description")
    public String getDescription() {
        return _description;
    }
    public void setDescription(final String _description) {
        this._description = _description;
    }

    @DynamoDBIndexRangeKey(attributeName = "Cost", globalSecondaryIndexName = "DateSorted")
    public Double getCost() {
        return _cost;
    }
    public void setCost(final Double _cost) {
        this._cost = _cost;
    }

    @Override
    public String toString() {
        return "Product: " + this._name;
    }

}
