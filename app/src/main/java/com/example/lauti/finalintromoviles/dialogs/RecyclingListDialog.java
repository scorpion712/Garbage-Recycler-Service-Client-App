package com.example.lauti.finalintromoviles.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;

public class RecyclingListDialog extends AppCompatDialogFragment {
    private static final String DIALOG_TITLE = "Ayuda";
    private static final String HELP_TEXT = "Se muestra la lista de reciclados que se han enviado al servidor, junto con su nombre, su cantidad enviada y la fecha.\n";

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
