package com.example.lauti.finalintromoviles.dialogs;
/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented Architecture.
 * To find the project:
 * @link:https://github.com/scorpion712/Rest-Service-Garbage-Recycler
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;

public class RegisterDialog extends AppCompatDialogFragment {
    private static final String DIALOG_TITLE = "Ayuda";
    private static final String HELP_TEXT = "Para registrar un usuario debe completar, al menos, los campos \"nombre\", \"apellido\" y \"username\".\n";
    private static final String REGISTER_BUTTON="REGISTRAR: almacena el nuevo usuario ingresado haciendo uso del servicio.\n";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); // Dialog builder
        builder.setTitle(DIALOG_TITLE)
                .setMessage(HELP_TEXT + "\n" + REGISTER_BUTTON)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create(); // return de dialog created

    }
}
