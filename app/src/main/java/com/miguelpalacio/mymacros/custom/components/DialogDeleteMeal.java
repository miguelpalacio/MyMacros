package com.miguelpalacio.mymacros.custom.components;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.miguelpalacio.mymacros.R;

public class DialogDeleteMeal extends DialogFragment {

    public interface DialogDeleteMealListener {
        void onDeleteMeal(DialogFragment dialog);
    }

    DialogDeleteMealListener dialogDeleteMealListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dialogDeleteMealListener = (DialogDeleteMealListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DialogDeleteMealListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the dialog properties.
        builder.setMessage(R.string.dialog_meal_delete_message);
        builder.setPositiveButton(R.string.dialog_meal_delete_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogDeleteMealListener.onDeleteMeal(DialogDeleteMeal.this);
            }
        });
        builder.setNegativeButton(R.string.dialog_meal_delete_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User cancelled the dialog.
            }
        });

        return builder.create();
    }
}
