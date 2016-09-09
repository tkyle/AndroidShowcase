package com.tbd.androidshowcase.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tbd.androidshowcase.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trevor on 9/8/2016.
 */
public class NotesAdapter extends ArrayAdapter<NotesDO> {
    public NotesAdapter(Context context, ArrayList<NotesDO> notes) {
        super(context, 0, notes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        NotesDO note = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.examplelist_row, parent, false);
        }
        // Lookup view for data population
        TextView noteText = (TextView) convertView.findViewById(R.id.noteText);

        // Populate the data into the template view using the data object
        noteText.setText(note.getContent());

        // Return the completed view to render on screen
        return convertView;
    }
}