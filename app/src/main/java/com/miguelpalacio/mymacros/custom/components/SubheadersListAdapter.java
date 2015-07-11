package com.miguelpalacio.mymacros.custom.components;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miguelpalacio.mymacros.R;

/**
 * Adapter for lists of items with <b>subheaders</b> done using RecyclerView.
 */
public class SubheadersListAdapter extends SelectableAdapter<SubheadersListAdapter.ViewHolder> {

    private static final int TYPE_FIRST_SUBHEADER = 0;
    private static final int TYPE_ROW = 1;
    private static final int TYPE_SUBHEADER = 2;

    private final String[] titles;
    private final String[] summaries;
    private final String[] isSubheader;

    private ViewHolder.ClickListener clickListener;

    // Class constructor.
    public SubheadersListAdapter(String[] titles, String[] summaries, String[] isSubheader,
                                 ViewHolder.ClickListener clickListener) {
        super();

        this.titles = titles;
        this.summaries = summaries;
        this.isSubheader = isSubheader;

        this.clickListener = clickListener;
    }

    // ViewHolder Inner Class.
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int holderId;

        TextView titleTextView;
        TextView summaryTextView;

        TextView subheaderTextView;

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
            } else if (viewType == TYPE_SUBHEADER) {
                holderId = 2;
                subheaderTextView = (TextView) itemView.findViewById(R.id.list_item_subheader);
            }
            else {
                holderId = 0;
                subheaderTextView = (TextView) itemView.findViewById(R.id.list_item_first_subheader);
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

    // Inflate subheaders_list_row.xml, subheaders_list_subheader.xml or
    // subheaders_list_subheader_first.xml in accordance with viewType.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //final int layout = viewType == TYPE_ROW ? R.layout.drawer_row : R.layout.drawer_header;
        final int layout;

        if (viewType == TYPE_ROW) {
            layout = R.layout.subheaders_list_row;
        } else if (viewType == TYPE_SUBHEADER) {
            layout = R.layout.subheaders_list_subheader;
        } else {
            layout = R.layout.subheaders_list_subheader_first;
        }

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(v, viewType, clickListener);
    }

    // This method is called when the item in a row needs to be displayed.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.holderId == 1) {
            holder.titleTextView.setText(titles[position]);
            holder.summaryTextView.setText(summaries[position]);
        } else {
            holder.subheaderTextView.setText(titles[position]);
        }
    }

    // Return the number of items present in the list (rows + subheaders).
    @Override
    public int getItemCount() {
        return titles.length;
    }

    // Return the type of the view that is being passed.
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_FIRST_SUBHEADER;
        } else if (isSubheader[position].equals("1")) {
            return TYPE_SUBHEADER;
        } else {
            return TYPE_ROW;
        }
    }
}
