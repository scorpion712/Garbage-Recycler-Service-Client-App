package com.example.lauti.finalintromoviles.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;


/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented.
 * To find the project:
 * @link: https://github.com/scorpion712/Rest-Service-Garbage-Recycler
 */

// Class created to show a help dialog on RecyclingActivity
public class RecyclingDialog extends AppCompatDialogFragment {
    private static final String DIALOG_TITLE = "Ayuda";
    private static final String HELP_TEXT = "Para registrar un reciclado debe completar los campos que corresponda. Si no posee reciclado de alguna clase coloque 0 (cero). \n";
    private static final String LOAD_BUTTON="CARGAR: almacena el reciclado ingresado en el dispositivo.\n";
    private static final String SEND_BUTTON="ENVIAR: envia el reciclado ingresado al servidor.\n";
    private static final String LIST_VIEW="VER RECICLADOS: muestra la lista de todos los reciclados que posee.\n";
    private static final String VIEW_ALL="VER TOTAL: muestra el total en cantidad de reciclados y su peso en toneladas.";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); // Dialog builder
        builder.setTitle(DIALOG_TITLE)
                .setMessage(HELP_TEXT + "\n" + LOAD_BUTTON + "\n" + SEND_BUTTON + "\n" + LIST_VIEW + "\n" + VIEW_ALL)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create(); // return de dialog created

    }
}
