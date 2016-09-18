package com.tbd.androidshowcase.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.model.Product;

import java.util.ArrayList;

/**
 * Created by Trevor on 9/11/2016.
 */
public class ProductsAdapter  extends ArrayAdapter<Product> {

    private ArrayList<Product> products;

    public ProductsAdapter(Context context, ArrayList<Product> products) {
        super(context, 0, products);

        this.products = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        Product product = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.productlist_row, parent, false);
        }
        // Lookup view for data population
        TextView productNameText = (TextView) convertView.findViewById(R.id.lstProductName);
        TextView productDescriptionText = (TextView) convertView.findViewById(R.id.lstProductDescription);
        TextView productCostText = (TextView) convertView.findViewById(R.id.lstProductCost);

        // Populate the data into the template view using the data object
        productNameText.setText(product.getName());
        productDescriptionText.setText(product.getDescription());
        productCostText.setText(product.getCost().toString());

        // Return the completed view to render on screen
        return convertView;
    }

    public void refreshProducts(ArrayList<Product> productList) {
        this.products.clear();
        this.products.addAll(productList);
        notifyDataSetChanged();
    }
}
