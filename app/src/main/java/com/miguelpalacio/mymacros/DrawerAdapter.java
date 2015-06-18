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
public class DrawerAdapter extends SelectableAdapter<DrawerAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ROW = 1;
    private static final int TYPE_DIVIDER = 2;

    private final String[] entries;
    private final TypedArray icons;
    private final TypedArray icons_selected;

    private String headerGoal;
    private String headerProgress;

    private ViewHolder.ClickListener clickListener;

    // Class constructor.
    public DrawerAdapter(String[] entries, TypedArray icons, TypedArray icons_selected,
                         String headerGoal, String headerProgress,  ViewHolder.ClickListener clickListener) {
        super();

        this.entries = entries;
        this.icons = icons;
        this.icons_selected = icons_selected;

        this.headerGoal = headerGoal;
        this.headerProgress = headerProgress;

        this.clickListener = clickListener;
    }

    // ViewHolder Class.
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int holderId;

        TextView entryLabel;
        ImageView entryIcon;

        TextView goal;
        TextView progress;

        View itemView;

        private ClickListener listener;

        public ViewHolder(View itemView, int viewType, ClickListener listener) {
            super(itemView);

            this.itemView = itemView;

            // Set the view according with its type.
            if (viewType == TYPE_ROW) {
                holderId = 1;

                entryLabel = (TextView) itemView.findViewById(R.id.drawer_item_text);
                entryIcon = (ImageView) itemView.findViewById(R.id.drawer_item_icon);

                // Set click listener for the row.
                this.listener = listener;
                itemView.setOnClickListener(this);
            } else if (viewType == TYPE_DIVIDER) {
                holderId = 2;
            }
            else {
                holderId = 0;

                goal = (TextView) itemView.findViewById(R.id.drawer_header_goal);
                progress = (TextView) itemView.findViewById(R.id.drawer_header_progress);
            }
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onDrawerItemClick(getAdapterPosition());
            }
        }

        // Interface to route back click events to Activity.
        public interface ClickListener {
            void onDrawerItemClick(int position);
        }
    }

    // Inflate drawer_header.xml, drawer_row.xml or drawer_divider in accordance with viewType.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //final int layout = viewType == TYPE_ROW ? R.layout.drawer_row : R.layout.drawer_header;
        final int layout;

        if (viewType == TYPE_ROW) {
            layout = R.layout.drawer_row;
        } else if (viewType == TYPE_DIVIDER) {
            layout = R.layout.drawer_divider;
        } else {
            layout = R.layout.drawer_header;
        }

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(v, viewType, clickListener);
    }

    // This method is called when the item in a row needs to be displayed.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder.holderId == 1) {

            int pos;
            if (position < 6) {
                // Main entries.
                pos = position - 1;
            } else {
                // Settings and FAQ.
                pos = position - 2;
            }

            holder.entryLabel.setText(entries[pos]);

            // For selected row, highlight background and icon.
            if (isSelected(position) && position < 6) {
                holder.itemView.setBackgroundResource(R.drawable.custom_bg_selected);
                holder.entryIcon.setImageDrawable(icons_selected.getDrawable(pos));
            } else {
                holder.itemView.setBackgroundResource(R.drawable.custom_bg);
                holder.entryIcon.setImageDrawable(icons.getDrawable(pos));
            }
        } else if (holder.holderId == 0) {

            holder.goal.setText(headerGoal);
            holder.progress.setText(headerProgress);
        }
    }

    // Return the number of items present in the list (rows + header + divider).
    @Override
    public int getItemCount() {
        return entries.length + 2;
    }

    // Return the type of the view that is being passed.
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == 6) {
            return TYPE_DIVIDER;
        } else {
            return TYPE_ROW;
        }
    }
}
