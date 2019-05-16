package com.example.lauti.finalintromoviles.dialogs;

/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented.
 * To find the project:
 * @link:https://github.com/scorpion712/Rest-Service-Garbage-Recycler
 */
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;

public class AllRecyclingDialog extends AppCompatDialogFragment {
    private static final String DIALOG_TITLE = "Ayuda";
    private static final String HELP_TEXT = "Se muestra el total reciclados que se han enviado al servidor, con su nombre y su cantidad total.\nAdemas se muestra el peso total de sus reciclados en toneladas.";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); // Dialog builder
        builder.setTitle(DIALOG_TITLE)
                .setMessage(HELP_TEXT)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create(); // return de dialog created

    }
}