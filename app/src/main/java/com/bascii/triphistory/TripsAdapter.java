package com.bascii.triphistory;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TripsAdapter extends ArrayAdapter<TripInfo> {
    // View lookup cache
    private static class ViewHolder {
        TextView text1;
        TextView text2;
    }

    public TripsAdapter(Context context, ArrayList<TripInfo> users) {
        super(context, android.R.layout.simple_list_item_2, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TripInfo trip = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            viewHolder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
            viewHolder.text2 = (TextView) convertView.findViewById(android.R.id.text2);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.text1.setText(trip.Name);
        viewHolder.text2.setText(trip.FormattedLastModifiedDate());

        // Return the completed view to render on screen
        return convertView;
    }
}
