package com.miguelpalacio.mymacros;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Adapter for the lists of items done using RecyclerView.
 */
public class RecyclerListAdapter extends SelectableAdapter<RecyclerListAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ROW = 1;
    private static final int TYPE_DIVIDER = 2;

    private final String[] titles;
    private final String[] summaries;

    private ViewHolder.ClickListener clickListener;

    // Class constructor.
    public RecyclerListAdapter(String[] titles, String[] summaries,
                               ViewHolder.ClickListener clickListener) {
        super();

        this.titles = titles;
        this.summaries = summaries;

        this.clickListener = clickListener;
    }

    // ViewHolder Inner Class.
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int holderId;

        TextView titleTextView;
        TextView summaryTextView;

        View itemView;

        private ClickListener listener;

        public ViewHolder(View itemView, int viewType, ClickListener listener) {
            super(itemView);

            this.itemView = itemView;

            // Set the view according with its type.
            if (viewType == TYPE_ROW) {
                holderId = 1;

                titleTextView = (TextView) itemView.findViewById(R.id.list_item_title);
                summaryTextView = (TextView) itemView.findViewById(R.id.list_item_summary);

                // Set click listener for the row.
                this.listener = listener;
                itemView.setOnClickListener(this);
            } else if (viewType == TYPE_DIVIDER) {
                holderId = 2;
            }
            else {
                holderId = 0;
            }
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onListItemClick(getAdapterPosition());
            }
        }

        // Interface to route back click events to Activity.
        public interface ClickListener {
            void onListItemClick(int position);
        }
    }

    // Inflate drawer_header.xml, drawer_row.xml or drawer_divider in accordance with viewType.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //final int layout = viewType == TYPE_ROW ? R.layout.drawer_row : R.layout.drawer_header;
        final int layout;

        if (viewType == TYPE_ROW) {
            layout = R.layout.list_row;
        } else if (viewType == TYPE_DIVIDER) {
            //layout = R.layout.drawer_divider;
            layout = R.layout.list_row; // Wrong, just to initialize.
        } else {
            //layout = R.layout.drawer_header;
            layout = R.layout.list_row; // Wrong, just to initialize.
        }

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(v, viewType, clickListener);
    }

    // This method is called when the item in a row needs to be displayed.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder.holderId == 1) {

/*            int pos;
            if (position < 6) {
                // Main entries.
                pos = position - 1;
            } else {
                // Settings and FAQ.
                pos = position - 2;
            }*/

            holder.titleTextView.setText(titles[position]);
            holder.summaryTextView.setText(summaries[position]);

/*            // For selected row, highlight background and icon.
            if (isSelected(position) && position < 6) {
                holder.itemView.setBackgroundResource(R.drawable.custom_bg_selected);
            } else {
                holder.itemView.setBackgroundResource(R.drawable.custom_bg);
            }*/
        } else if (holder.holderId == 0) {

        }
    }

    // Return the number of items present in the list (rows).
    @Override
    public int getItemCount() {
        return titles.length;
    }

    // Return the type of the view that is being passed.
    @Override
    public int getItemViewType(int position) {
        return TYPE_ROW;
/*        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == 6) {
            return TYPE_DIVIDER;
        } else {
            return TYPE_ROW;
        }*/
    }
}
