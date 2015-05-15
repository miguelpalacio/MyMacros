package com.miguelpalacio.mymacros;

import android.content.Context;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This preference class will be used only to show information on a Dialog when
 * the preference is clicked by the user.
 */
public class InfoDialogPreference extends DialogPreference {

    public InfoDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {

        LinearLayout layout = new LinearLayout(getContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Convert from dip to their equivalent px (needed for coherent padding).
        Resources r = getContext().getResources();
        int px16dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, r.getDisplayMetrics());

        layout.setPadding(px16dp, px16dp, px16dp, px16dp);

        // Create the text view that will display the information wanted.
        TextView textView = new TextView(getContext());

        // Load the string that will be shown in the dialog, and style it.
        textView.setText(R.string.profile_recommended_dist_dialog);
        textView.setTextAppearance(getContext(), android.R.style.TextAppearance_Small);

        // Add textView to layout.
        layout.addView(textView);

        return layout;
    }
}
