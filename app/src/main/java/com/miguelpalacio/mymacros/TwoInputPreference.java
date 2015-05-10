package com.miguelpalacio.mymacros;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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

    private EditText inputOne;
    private EditText inputTwo;

    InputMethodManager imm;

    // Class constructor.
    public TwoInputPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.preference_two_input);
        setPositiveButtonText(R.string.button_positive);
        setNegativeButtonText(R.string.button_negative);

        setDialogIcon(null);

        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
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

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);

        inputOne = (EditText) view.findViewById(R.id.input_one);
        inputTwo = (EditText) view.findViewById(R.id.input_two);
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

}
