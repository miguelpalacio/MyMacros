package com.miguelpalacio.mymacros;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;


/**
 * TwoInputPreference class.
 * Used when storing two correlated data for a preference is needed.
 * @author Miguel Palacio.
 */
public class TwoInputPreference extends DialogPreference {

    private String currentValue1;
    private String currentValue2;
    private String newValue1;
    private String newValue2;

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
            //currentValue1 = this.getPer
        } else {
            // Set default state from the XML attribute.

        }


    }

    /**
     * Called when the user selects either the positive or negative button.
     * @param positiveResult true if positive button selected, false otherwise.
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {

        // When the user selects "OK", persist the new values.
        if (positiveResult) {
            persistString(newValue1);
            persistString(newValue2);
        }

    }

}
