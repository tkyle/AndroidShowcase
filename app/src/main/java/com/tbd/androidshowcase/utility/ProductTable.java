package com.tbd.androidshowcase.utility;

import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

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

        //firstItem.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
        newProduct.setproductId(product.getProductId());
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
    public void removeItem(String noteId) throws AmazonClientException {

        Log.d(LOG_TAG, "Removing item from data.");
        final NotesDO itemToFind = new NotesDO();

        // have to use Hash? That's why I set the ID, but I need to limit it more, so I'm using the range key?
        itemToFind.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
        final Condition rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString()).withAttributeValueList(new AttributeValue().withS(noteId));

        final DynamoDBQueryExpression<NotesDO> queryExpression = new DynamoDBQueryExpression<NotesDO>()
                .withHashKeyValues(itemToFind)
                .withRangeKeyCondition("noteId", rangeKeyCondition)
                .withConsistentRead(false);

        final PaginatedQueryList<NotesDO> results = mapper.query(NotesDO.class, queryExpression);

        Iterator<NotesDO> resultsIterator = results.iterator();

        AmazonClientException lastException = null;

        if (resultsIterator.hasNext()) {
            final NotesDO item = resultsIterator.next();

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
    public void editItem(NotesDO note) throws AmazonClientException {

        Log.d(LOG_TAG, "edit item");
        final NotesDO itemToFind = new NotesDO();

        // have to use Hash? That's why I set the ID, but I need to limit it more, so I'm using the range key?
        itemToFind.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
        final Condition rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString()).withAttributeValueList(new AttributeValue().withS(note.getNoteId()));

        final DynamoDBQueryExpression<NotesDO> queryExpression = new DynamoDBQueryExpression<NotesDO>()
                .withHashKeyValues(itemToFind)
                .withRangeKeyCondition("noteId", rangeKeyCondition)
                .withConsistentRead(false);

        final PaginatedQueryList<NotesDO> results = mapper.query(NotesDO.class, queryExpression);

        Iterator<NotesDO> resultsIterator = results.iterator();

        AmazonClientException lastException = null;

        if (resultsIterator.hasNext()) {
            final NotesDO item = resultsIterator.next();

            item.setContent(note.getContent());
            item.setTitle(note.getTitle());
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
    public ArrayList<NotesDO> getItems() throws AmazonClientException {

        final NotesDO itemToFind = new NotesDO();
        itemToFind.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());

        final DynamoDBQueryExpression<NotesDO> queryExpression = new DynamoDBQueryExpression<NotesDO>()
                .withHashKeyValues(itemToFind)
                .withConsistentRead(false);

        final PaginatedQueryList<NotesDO> results = mapper.query(NotesDO.class, queryExpression);

        Iterator<NotesDO> resultsIterator = results.iterator();

        AmazonClientException lastException = null;

        final List<NotesDO> batchOfItems = new LinkedList<NotesDO>();
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

        ArrayList<NotesDO> n = new ArrayList<NotesDO>();
        n.addAll(batchOfItems);
        return n;
    }

}
