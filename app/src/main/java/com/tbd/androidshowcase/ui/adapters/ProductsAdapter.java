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

    public ProductsAdapter(Context context, ArrayList<Product> notes) {
        super(context, 0, notes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        Product product = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.examplelist_row, parent, false);
        }
        // Lookup view for data population
        TextView noteText = (TextView) convertView.findViewById(R.id.noteText);

        // Populate the data into the template view using the data object
        noteText.setText(product.getName());

        // Return the completed view to render on screen
        return convertView;
    }
}
