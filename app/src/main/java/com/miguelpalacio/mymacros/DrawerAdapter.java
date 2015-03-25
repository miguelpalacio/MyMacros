package com.miguelpalacio.mymacros;

import android.app.Activity;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for the lateral navigation drawer.
 */
public class DrawerAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] labels;
    private final TypedArray icons;

    // ViewHolder pattern implementation.
    private static class ViewHolder {
        public TextView text;
        public ImageView image;
    }

    public DrawerAdapter(Activity context, String[] labels, TypedArray icons) {
        super(context, R.layout.drawer_row, labels);
        this.context = context;
        this.labels = labels;
        this.icons = icons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        // Reuse views.
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.drawer_row, null);
            // Configure view holder.
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.drawer_item_text);
            viewHolder.image = (ImageView) rowView.findViewById(R.id.drawer_item_icon);
            rowView.setTag(viewHolder);
        }

        // Fill data.
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.text.setText(labels[position]);
        holder.image.setImageDrawable(icons.getDrawable(position));

        return rowView;
    }
}
