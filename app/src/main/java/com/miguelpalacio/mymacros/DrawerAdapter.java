package com.miguelpalacio.mymacros;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Adapter for the lateral navigation drawer.
 */
public class DrawerAdapter extends SelectableAdapter<DrawerAdapter.ViewHolder> {

    private static final int TYPE_ROW = 1;
    private static final int TYPE_HEADER = 0;

    private final String[] labels;
    private final TypedArray icons;
    private final TypedArray backgrounds;

    private String nameLabel;
    private String emailLabel;

    private ViewHolder.ClickListener clickListener;

    // Class constructor.
    public DrawerAdapter(String[] labels, TypedArray icons, TypedArray backgrounds, String nameLabel, String emailLabel,
                         ViewHolder.ClickListener clickListener) {
        super();

        this.labels = labels;
        this.icons = icons;
        this.backgrounds = backgrounds;

        this.nameLabel = nameLabel;
        this.emailLabel = emailLabel;

        this.clickListener = clickListener;
    }

    // ViewHolder Class.
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int holderId;

        TextView textView;
        ImageView imageView;
        TextView name;
        TextView email;

        View selectedOverlay;
        View itemView;

        private ClickListener listener;

        public ViewHolder(View itemView, int viewType, ClickListener listener) {
            super(itemView);

            this.itemView = itemView;

            // Set the view according with its type.
            if (viewType == TYPE_ROW) {
                holderId = 1;

                textView = (TextView) itemView.findViewById(R.id.drawer_item_text);
                imageView = (ImageView) itemView.findViewById(R.id.drawer_item_icon);
                selectedOverlay = itemView.findViewById(R.id.selected_overlay);

                // Set click listener for the row.
                this.listener = listener;
                itemView.setOnClickListener(this);
            } else {
                holderId = 0;

                name = (TextView) itemView.findViewById(R.id.name);
                email = (TextView) itemView.findViewById(R.id.email);
            }
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getAdapterPosition());
/*                Log.d("Some", "Item clicked at position " + getAdapterPosition());*/
            }
        }

        // Interface to route back click events to Activity.
        public interface ClickListener {
            public void onItemClicked(int position);
        }
    }

    // Inflate either drawer_header.xml or drawer_row.xml in accordance with viewType.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final int layout = viewType == TYPE_ROW ? R.layout.drawer_row : R.layout.drawer_header;

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(v, viewType, clickListener);
    }

    // This method is called when the item in a row needs to be displayed.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder.holderId == 1) {
            holder.textView.setText(labels[position - 1]);
            holder.imageView.setImageDrawable(icons.getDrawable(position - 1));

            // Highlight row if it is selected.
            //holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
            if (isSelected(position)) {
                holder.itemView.setBackgroundResource(R.drawable.custom_bg_selected);
                //holder.itemView.setBackgroundColor(Color.RED);
            } else {
                holder.itemView.setBackgroundResource(R.drawable.custom_bg);
            }
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
