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

/**
 * Created by Trevor on 9/13/2016.
 */
public class ProductFragment extends android.support.v4.app.DialogFragment
{
    private ImageButton btnDismiss;

    public static ProductFragment newInstance(String title) {
        ProductFragment frag = new ProductFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public interface ProductFragmentListener {
        void onFinishEditDialog(String inputText);
    }

    public ProductFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_custom, container);

        // retrieve display dimensions
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        view.setMinimumWidth((int)(displayRectangle.width() * 0.7f));
        //view.setMinimumHeight((int)(displayRectangle.height() * 0.9f));
        //mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        btnDismiss = (ImageButton) view.findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ProductFragmentListener listener = (ProductFragmentListener) getActivity();
                //listener.onFinishEditDialog(mEditText.getText().toString());
                dismiss();
            }
        });
        String title = getArguments().getString("title", "New Product");
        getDialog().setTitle(title);
        // Show soft keyboard automatically
        //mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return view;
    }

}