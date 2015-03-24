package com.miguelpalacio.mymacros;

import android.content.Context;
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

    private final Context context;
    private final String[] values;

    public DrawerAdapter(Context context, String[] values) {
        super(context, R.layout.drawer_row, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.drawer_row, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.drawer_item_text);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.drawer_item_icon);
        textView.setText(values[position]);

        // Test
        String s = values[position];
        if (s.startsWith("Meal")) {
            imageView.setImageResource(R.drawable.abc_ic_menu_share_mtrl_alpha);
        } else {
            imageView.setImageResource(R.drawable.abc_ic_menu_cut_mtrl_alpha);
        }

        return rowView;
    }
}
