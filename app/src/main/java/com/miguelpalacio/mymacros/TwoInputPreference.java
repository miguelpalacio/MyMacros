package com.miguelpalacio.mymacros;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * TwoInputPreference class.
 * Used when storing two correlated (numeric) data for a preference is needed.
 * @author Miguel Palacio.
 */
public class TwoInputPreference extends DialogPreference {

    private String mCurrentValue;

    private EditText inputOne;
    private EditText inputTwo;

    public TwoInputPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.preference_two_input);
        setPositiveButtonText(R.string.button_positive);
        setNegativeButtonText(R.string.button_negative);

        setDialogIcon(null);
    }

    /**
     * Defines the default value to be used by the preference.
     * @param a Array used to get at the custom attributes in the XML layout.
     * @param index The wanted attribute to retrieve.
     * @return The default value object.
     */
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

    @Override
    protected void onBindDialogView(View view) {
        inputOne = (EditText) view.findViewById(R.id.input_one);
        inputTwo = (EditText) view.findViewById(R.id.input_two);

        String[] values = mCurrentValue.split("-");
        inputOne.setText(values[0]);
        inputTwo.setText(values[1]);
    }

    /**
     * Called when the user selects either the positive or negative button.
     * @param positiveResult true if positive button selected, false otherwise.
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {

        // When the user selects "OK", persist the new values.
        if (positiveResult) {
            // Parse the two inputs into a single string by using a '-' as separator.
            persistString(inputOne.getText() + "-" + inputTwo.getText());
        }
    }

}
