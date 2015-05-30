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

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ROW = 1;
    private static final int TYPE_ROW_LAST = 2;

    private final String[] titles;
    private final String[] summaries;
    private final int headerLayout;

    private final boolean oneLineRow;

    private ViewHolder.ClickListener clickListener;

    // Class constructor.
    public ItemListAdapter(String[] titles, String[] summaries, int headerLayout,
                           boolean oneLineRow, ViewHolder.ClickListener clickListener) {
        super();

        this.titles = titles;
        this.summaries = summaries;

        // headerLayout corresponds to the header layout's ID. If zero, then there's no header.
        this.headerLayout = headerLayout;

        this.oneLineRow = oneLineRow;

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
            if (viewType == TYPE_ROW || viewType == TYPE_ROW_LAST) {
                holderId = 1;

                titleTextView = (TextView) itemView.findViewById(R.id.list_item_title);
                summaryTextView = (TextView) itemView.findViewById(R.id.list_item_summary);

                // Set click listener for the row.
                this.listener = listener;
                itemView.setOnClickListener(this);
            } else {
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

    // Inflate item_list_row.xml or item_list_row_last in accordance with viewType.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final int layout;
        if (viewType == TYPE_ROW) {
            layout = oneLineRow ? R.layout.item_list_one_line_row : R.layout.item_list_row;
        } else if (viewType == TYPE_ROW_LAST) {
            layout = oneLineRow ? R.layout.item_list_one_line_row_last : R.layout.item_list_row_last;
        } else {
            layout = headerLayout;
        }

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(v, viewType, clickListener);
    }

    // This method is called when the item in a row needs to be displayed.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int pos = headerLayout == 0 ? position : position - 1;
        if (holder.holderId == 1) {
            holder.titleTextView.setText(titles[pos]);
            holder.summaryTextView.setText(summaries[pos]);
        }
    }

    // Return the number of items present in the list (header + rows).
    @Override
    public int getItemCount() {
        return headerLayout == 0 ? titles.length : titles.length + 1;
    }

    // Return the type of the view that is being passed.
    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerLayout != 0) {
            return TYPE_HEADER;
        } else if (position == getItemCount() - 1) {
            return TYPE_ROW_LAST;
        } else {
            return TYPE_ROW;
        }
    }
}
