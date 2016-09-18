package com.tbd.androidshowcase.ui.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.tbd.androidshowcase.R;
import com.tbd.androidshowcase.model.Product;
import com.tbd.androidshowcase.presenter.ProductListPresenter;
import com.tbd.androidshowcase.ui.activity.ProductListActivity;

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

    // endregion

    // region Methods

    public static ProductFragment newInstance(String title, Product _product,  Boolean isNew) {

        ProductFragment frag = new ProductFragment();

        Bundle args = new Bundle();

        args.putString("productId", _product.getProductId());
        args.putString("productName", _product.getName());
        args.putString("productDescription", _product.getDescription());
        args.putString("productCost", _product.getCost() != null ? _product.getCost().toString() : "0.00");
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
        View view = inflater.inflate(R.layout.dialog_custom, container);

        // retrieve display dimensions
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        view.setMinimumWidth((int)(displayRectangle.width() * 0.7f));

        productName =  (EditText) view.findViewById(R.id.txtProductName);
        productDescription =  (EditText) view.findViewById(R.id.txtProductDescription);
        productCost =  (EditText) view.findViewById(R.id.txtProductCost);

        btnDismiss = (ImageButton) view.findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductFragmentListener listener = (ProductFragmentListener) getActivity();

                product.setName(productName.getText().toString());
                product.setDescription( productDescription.getText().toString());
                product.setCost(Double.parseDouble(productCost.getText().toString()));

                listener.onFinishEditDialog(product, isNew);
                dismiss();
            }
        });

        String title = getArguments().getString("title");
        getDialog().setTitle(title);

        isNew = getArguments().getBoolean("isNew");

        product = new Product();
        product.setProductId(getArguments().getString("productId"));
        product.setName(getArguments().getString("productName"));
        product.setDescription(getArguments().getString("productDescription"));
        product.setCost(getArguments().getString("productCost") != null ? Double.parseDouble(getArguments().getString("productCost")) : 0.00);

        if(!isNew)
        {
            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            productCost.setText(product.getCost().toString());
        }

        return view;
    }

    // endregion
}