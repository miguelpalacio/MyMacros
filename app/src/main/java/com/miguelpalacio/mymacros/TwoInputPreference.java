package com.miguelpalacio.mymacros;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * TwoInputPreference class.
 * Used when storing two correlated <e>numeric</e> data for a preference is needed.
 * <p>
 *     Since a preference can store only one string, this class separates both input strings
 *     by using a <b>'-'</b> character as separator. Therefore, both the stored and retrieved
 *     strings must separate the data related to the two inputs with <b>'-'</b>.
 *     Example: if first input is 3, and second input is 9, the stored string is "3-9".
 * </p>
 * @author Miguel Palacio.
 */
public class TwoInputPreference extends DialogPreference {

    private String mCurrentValue;

    //private LinearLayout layout;
    private EditText inputOne;
    private EditText inputTwo;

    // Class constructor.
    public TwoInputPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        //setDialogLayoutResource(R.layout.preference_two_input);
        setPositiveButtonText(R.string.button_positive);
        setNegativeButtonText(R.string.button_negative);

        setDialogIcon(null);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    /**
     * Notifies whether the setting has a persisted value.
     * @param restorePersistedValue indicates if a value has already been persisted for the setting.
     * @param defaultValue value to be used in case restoredPersistedValue is false.
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state.
            mCurrentValue = this.getPersistedString("0-0");
        } else {
            // Set default state from the XML attribute.
            mCurrentValue = (String) defaultValue;
            persistString(mCurrentValue);
        }
    }

    /**
     * Creates the layout with the two EditText views corresponding to the inputs.
     * @return The view to be shown in the dialog.
     */
    @Override
    protected View onCreateDialogView() {

        // Create main LinearLayout.
        LinearLayout mainLayout = new LinearLayout(getContext());

        // Create children Layouts (they will hold both a TextView and an EditText).
        LinearLayout childLayout1 = new LinearLayout(getContext());
        LinearLayout childLayout2 = new LinearLayout(getContext());

        // Create the EditText widgets.
        inputOne = new EditText(getContext());
        inputTwo = new EditText(getContext());

        // Create the TextView widgets.
        TextView labelOne = new TextView(getContext());
        TextView labelTwo = new TextView(getContext());

        // Define parameters for the layouts and views.
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 0);

        // Set views' parameters.
        mainLayout.setLayoutParams(params1);

        childLayout1.setLayoutParams(params2);
        childLayout2.setLayoutParams(params2);

        inputOne.setLayoutParams(params2);
        inputTwo.setLayoutParams(params2);

        labelOne.setLayoutParams(params3);
        labelTwo.setLayoutParams(params3);

        // Set layouts' orientation.
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        childLayout1.setOrientation(LinearLayout.HORIZONTAL);
        childLayout2.setOrientation(LinearLayout.HORIZONTAL);

        // Convert from dip to their equivalent px (needed for coherent padding).
        Resources r = getContext().getResources();
        int px4dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());
        int px8dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

        // Set specific properties of each view.
        childLayout1.setPadding(px8dp, px8dp, px8dp, 0);
        childLayout2.setPadding(px8dp, px8dp, px8dp, 0);

        labelOne.setPadding(px8dp, 0, px8dp, 0);
        labelTwo.setPadding(px8dp, 0, px8dp, 0);
        labelOne.setText(R.string.two_input_pref_label_one);
        labelTwo.setText(R.string.two_input_pref_label_two);
        labelOne.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
        labelTwo.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);

        inputOne.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputTwo.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Add views to the layouts.
        childLayout1.addView(inputOne);
        childLayout1.addView(labelOne);
        childLayout2.addView(inputTwo);
        childLayout2.addView(labelTwo);
        mainLayout.addView(childLayout1);
        mainLayout.addView(childLayout2);

        return mainLayout;
    }

/*    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);

*//*        inputOne = (EditText) view.findViewById(R.id.input_one);
        inputTwo = (EditText) view.findViewById(R.id.input_two);*//*
    }*/

    protected void showDialog(Bundle state) {
        super.showDialog(state);

        // Split mCurrentValue into two substrings by using '-'.
        String[] values = mCurrentValue.split("-");

        // Set text for both inputs using the value defined by mCurrentValue.
        inputOne.setText(values[0]);
        inputTwo.setText(values[1]);

        // Set focus on inputOne.
        inputOne.requestFocus();

        // Show soft keyboard.
        getDialog().getWindow().
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * Called when the user selects either the positive or negative button.
     * @param positiveResult true if positive button selected, false otherwise.
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new values.
        if (positiveResult) {
            String i1 = inputOne.getText().toString();
            String i2 = inputTwo.getText().toString();

            // Check for empty strings. If empty, set the default value.
            if (i1.equals(""))
                i1 = "0";
            if (i2.equals(""))
                i2 = "0";

            // Parse the two inputs into a single string by using a '-' as separator.
            persistString(i1 + "-" + i2);
        }
    }

}
