package com.tbd.androidshowcase.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.model.Product;
import com.tbd.androidshowcase.utility.NumberUtils;

/**
 * Created by Trevor on 9/13/2016.
 */
public class ProductFragment extends android.support.v4.app.DialogFragment
{
    // region Fields

    private ImageButton btnDismiss;
    private Button btnSubmit;
    private EditText productName;
    private EditText productDescription;
    private EditText productCost;
    private Boolean isNew;
    private Product product;
    private View view;

    // endregion

    // region Methods

    public static ProductFragment newInstance(String title, Product _product,  Boolean isNew) {

        ProductFragment frag = new ProductFragment();

        Bundle args = new Bundle();

        args.putString("productId", _product.getProductId());
        args.putString("productName", _product.getName());
        args.putString("productDescription", _product.getDescription());
        args.putString("productCost", Float.toString(_product.getCost()));
        args.putString("title", title);
        args.putBoolean("isNew", isNew);

        frag.setArguments(args);

        return frag;
    }

    public interface ProductFragmentListener {
        void onFinishEditDialog(Product product, Boolean isNew);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.dialog_product, container);

        setProductValues();

        SetupViewsAndContainers();

        return view;
    }

    private void setProductValues()
    {
        product = new Product();
        product.setProductId(getArguments().getString("productId"));
        product.setName(getArguments().getString("productName"));
        product.setDescription(getArguments().getString("productDescription"));
        product.setCost(Float.parseFloat(getArguments().getString("productCost")));
    }

    private void SetupViewsAndContainers()
    {
        // retrieve display dimensions
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        view.setMinimumWidth((int)(displayRectangle.width() * 0.7f));

        String title = getArguments().getString("title");
        getDialog().setTitle(title);

        setupEditTextViews();
        setupSubmitButton();
        setupDismissButton();
    }

    private void setupEditTextViews()
    {
        productName =  (EditText) view.findViewById(R.id.txtProductName);
        productDescription =  (EditText) view.findViewById(R.id.txtProductDescription);
        productCost =  (EditText) view.findViewById(R.id.txtProductCost);

        isNew = getArguments().getBoolean("isNew");

        if(!isNew)
        {
            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            productCost.setText(Float.toString(product.getCost()));
        }
    }

    private void setupDismissButton()
    {
        btnDismiss = (ImageButton) view.findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setupSubmitButton()
    {
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductFragmentListener listener = (ProductFragmentListener) getActivity();

                // Missing product information.
                if((productName.getText().toString() == null || productName.getText().toString().isEmpty()) ||
                        (productDescription.getText().toString() == null || productDescription.getText().toString().isEmpty()) ||
                        (productCost.getText().toString() == null || productCost.getText().toString().isEmpty()))
                {
                    return;
                }

                product.setName(productName.getText().toString());
                product.setDescription(productDescription.getText().toString());

                if(NumberUtils.TryParseFloat(productCost.getText().toString()))
                {
                    product.setCost(Float.parseFloat(productCost.getText().toString()));
                }
                else
                {
                    product.setCost(Float.parseFloat("0.00"));
                }

                listener.onFinishEditDialog(product, isNew);
                dismiss();
            }
        });
    }

    // endregion
}