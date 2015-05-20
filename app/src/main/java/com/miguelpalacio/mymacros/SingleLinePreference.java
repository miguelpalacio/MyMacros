package com.miguelpalacio.mymacros;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by miguel on 5/14/15.
 */
public class SingleLinePreference extends Preference {

    float titleSize;

    public SingleLinePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onBindView(View view) {
        super.onBindView(view);
        makeSingleLine(view);
    }

    // Place the Summary's TextView at the right side of the Title's TextView.
    protected void makeSingleLine(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int index = 0; index < viewGroup.getChildCount(); index++) {
                makeSingleLine(viewGroup.getChildAt(index));
            }
        } else if (view instanceof TextView) {
            TextView textView = (TextView) view;
            // Get Title's text size.
            if (textView.getId() == android.R.id.title) {
                titleSize = textView.getTextSize();
            }
            // Check that the TextView is the Summary.
            else if (textView.getId() == android.R.id.summary) {
/*                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) names.getLayoutParams();
                layoutParams.addRule(RelativeLayout.BELOW);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);*/

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                //names.setTextAppearance(getContext(), android.R.style.TextAppearance_DeviceDefault_Medium);

                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
                textView.setLayoutParams(layoutParams);
            }
        }
    }
}
