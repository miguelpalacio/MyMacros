package com.miguelpalacio.mymacros;

import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for the lateral navigation drawer.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ROW = 1;

    private final String[] labels;
    private final TypedArray icons;

    private String nameLabel;
    private String emailLabel;

    // ViewHolder.
    public static class ViewHolder extends RecyclerView.ViewHolder {

        int holderId;

        TextView textView;
        ImageView imageView;
        TextView name;
        TextView email;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            // Set the view according with its type.
            if (viewType == TYPE_ROW) {
                textView = (TextView) itemView.findViewById(R.id.drawer_item_text);
                imageView = (ImageView) itemView.findViewById(R.id.drawer_item_icon);
                holderId = 1;
            } else {
                name = (TextView) itemView.findViewById(R.id.name);
                email = (TextView) itemView.findViewById(R.id.email);
                holderId = 0;
            }
        }
    }

    // DrawerAdapter's constructor.
    public DrawerAdapter(String[] labels, TypedArray icons, String nameLabel, String emailLabel) {

        this.labels = labels;
        this.icons = icons;
        this.nameLabel = nameLabel;
        this.emailLabel = emailLabel;
    }

    // Inflate either drawer_header.xml or drawer_row.xml in accordance with viewType.
    @Override
    public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ROW) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row, parent, false);
            return new ViewHolder(v, viewType);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false);
            return new ViewHolder(v, viewType);
        }
    }

    // This method is called when the item in a row needs to be displayed.
    @Override
    public void onBindViewHolder(DrawerAdapter.ViewHolder holder, int position) {

        if(holder.holderId == 1) {
            holder.textView.setText(labels[position - 1]);
            holder.imageView.setImageDrawable(icons.getDrawable(position - 1));
        } else {
            holder.name.setText(nameLabel);
            holder.email.setText(emailLabel);
        }
    }

    // Return the number of items present in the list (rows + header).
    @Override
    public int getItemCount() {
        return labels.length + 1;
    }

    // Return the type of the view that is being passed.
    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        return TYPE_ROW;
    }
}
