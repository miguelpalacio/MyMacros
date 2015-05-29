package com.miguelpalacio.mymacros;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Adapter for lists of items done using RecyclerView.
 */
public class ItemListAdapter extends SelectableAdapter<ItemListAdapter.ViewHolder> {

    private static final int TYPE_ROW_LAST = 0;
    private static final int TYPE_ROW = 1;

    private final String[] titles;
    private final String[] summaries;

    private ViewHolder.ClickListener clickListener;

    // Class constructor.
    public ItemListAdapter(String[] titles, String[] summaries, ViewHolder.ClickListener clickListener) {
        super();

        this.titles = titles;
        this.summaries = summaries;

        this.clickListener = clickListener;
    }

    // ViewHolder Inner Class.
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleTextView;
        TextView summaryTextView;

        View itemView;

        private ClickListener listener;

        public ViewHolder(View itemView, int viewType, ClickListener listener) {
            super(itemView);

            this.itemView = itemView;

                titleTextView = (TextView) itemView.findViewById(R.id.list_item_title);
                summaryTextView = (TextView) itemView.findViewById(R.id.list_item_summary);

                // Set click listener for the row.
                this.listener = listener;
                itemView.setOnClickListener(this);
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

    // Inflate item_list_row.xml or item_list_row_last in accordance with viewType.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final int layout = viewType == TYPE_ROW ? R.layout.item_list_row : R.layout.item_list_row_last;

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(v, viewType, clickListener);
    }

    // This method is called when the item in a row needs to be displayed.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titleTextView.setText(titles[position]);
        holder.summaryTextView.setText(summaries[position]);
    }

    // Return the number of items present in the list (rows).
    @Override
    public int getItemCount() {
        return titles.length;
    }

    // Return the type of the view that is being passed.
    @Override
    public int getItemViewType(int position) {
        if (position == titles.length - 1) {
            return TYPE_ROW_LAST;
        } else {
            return TYPE_ROW;
        }
    }
}
