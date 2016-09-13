package com.tbd.androidshowcase.ui.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.tbd.androidshowcase.R;

/**
 * Created by Trevor on 9/13/2016.
 */
public class ProductFragment extends android.support.v4.app.DialogFragment
{
    Button cancelButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_custom,container, false);

        cancelButton = (Button) view.findViewById( R.id.custom_button_cancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            dismissDialog();
        }});

        //int title = getArguments().getInt("title");
        //getDialog().setTitle("New Product");
        return view;
    }

    public void dismissDialog()
    {
        this.dismiss();
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
