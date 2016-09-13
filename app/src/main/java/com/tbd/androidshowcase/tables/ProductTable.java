package com.tbd.androidshowcase.tables;

import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.tbd.androidshowcase.model.Product;
import com.tbd.androidshowcase.utility.AWSMobileClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Trevor on 9/11/2016.
 */
public class ProductTable extends NoSQLTableBase
{
    private static final String LOG_TAG = ProductTable.class.getSimpleName();
    private final DynamoDBMapper mapper;

    /** Inner classes use this value to determine how many results to retrieve per service call. */
    private static final int RESULTS_PER_RESULT_GROUP = 40;

    /** Removing sample data removes the items in batches of the following size. */
    private static final int MAX_BATCH_SIZE_FOR_DELETE = 50;

    public ProductTable() {
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
    }

    @Override
    public String getTableName() {
        return "Products";
    }

    @Override
    public void addNewItem(ITableObject tableObject) throws AmazonClientException {

        Product product = (Product)tableObject;

        Log.d(LOG_TAG, "Inserting new product data.");
        final Product newProduct = new Product();

        newProduct.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
        newProduct.setProductId(product.getProductId());
        newProduct.setName(product.getName());
        newProduct.setDescription(product.getDescription());
        newProduct.setCost(product.getCost());

        AmazonClientException lastException = null;

        try {
            mapper.save(newProduct);
        } catch (final AmazonClientException ex) {
            Log.e(LOG_TAG, "Failed saving product : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void removeItem(String productId) throws AmazonClientException {

        Log.d(LOG_TAG, "Removing item from data.");
        final Product itemToFind = new Product();

        // have to use Hash? That's why I set the ID, but I need to limit it more, so I'm using the range key?
        itemToFind.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
        final Condition rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString()).withAttributeValueList(new AttributeValue().withS(productId));

        final DynamoDBQueryExpression<Product> queryExpression = new DynamoDBQueryExpression<Product>()
                .withHashKeyValues(itemToFind)
                .withRangeKeyCondition("ProductId", rangeKeyCondition)
                .withConsistentRead(false);

        final PaginatedQueryList<Product> results = mapper.query(Product.class, queryExpression);

        Iterator<Product> resultsIterator = results.iterator();

        AmazonClientException lastException = null;

        if (resultsIterator.hasNext()) {
            final Product item = resultsIterator.next();

            // Demonstrate deleting a single item.
            try {
                mapper.delete(item);
            } catch (final AmazonClientException ex) {
                Log.e(LOG_TAG, "Failed deleting item : " + ex.getMessage(), ex);
                throw ex;
            }
        }
    }

    @Override
    public void editItem(ITableObject tableObject) throws AmazonClientException {

        Product product = (Product)tableObject;

        Log.d(LOG_TAG, "edit item");
        final Product itemToFind = new Product();

        // have to use Hash? That's why I set the ID, but I need to limit it more, so I'm using the range key?
        itemToFind.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
        final Condition rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString()).withAttributeValueList(new AttributeValue().withS(product.getProductId()));

        final DynamoDBQueryExpression<Product> queryExpression = new DynamoDBQueryExpression<Product>()
                .withHashKeyValues(itemToFind)
                .withRangeKeyCondition("ProductId", rangeKeyCondition)
                .withConsistentRead(false);

        final PaginatedQueryList<Product> results = mapper.query(Product.class, queryExpression);

        Iterator<Product> resultsIterator = results.iterator();

        AmazonClientException lastException = null;

        if (resultsIterator.hasNext()) {
            final Product item = resultsIterator.next();

            item.setName(product.getName());
            item.setDescription(product.getDescription());
            item.setCost(product.getCost());
            // Demonstrate editing a single item.
            try {
                mapper.save(item);
            } catch (final AmazonClientException ex) {
                Log.e(LOG_TAG, "Failed editing item : " + ex.getMessage(), ex);
                throw ex;
            }
        }
    }

    @Override
    public ArrayList<ITableObject> getItems() throws AmazonClientException {

        final Product itemToFind = new Product();
        itemToFind.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());

        final DynamoDBQueryExpression<Product> queryExpression = new DynamoDBQueryExpression<Product>()
                .withHashKeyValues(itemToFind)
                .withConsistentRead(false);

        final PaginatedQueryList<Product> results = mapper.query(Product.class, queryExpression);

        Iterator<Product> resultsIterator = results.iterator();

        AmazonClientException lastException = null;

        final List<ITableObject> batchOfItems = new LinkedList<ITableObject>();
        while (resultsIterator.hasNext()) {
            // Build a batch of books to delete.
            for (int index = 0; index < MAX_BATCH_SIZE_FOR_DELETE && resultsIterator.hasNext(); index++) {
                batchOfItems.add(resultsIterator.next());
            }
        }

        if (lastException != null) {
            // Re-throw the last exception encountered to alert the user.
            // The logs contain all the exceptions that occurred during attempted delete.
            throw lastException;
        }

        ArrayList<ITableObject> n = new ArrayList<ITableObject>();
        n.addAll(batchOfItems);
        return n;
    }

}
