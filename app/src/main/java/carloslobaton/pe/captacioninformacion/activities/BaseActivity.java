package carloslobaton.pe.captacioninformacion.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TextView;

import carloslobaton.pe.captacioninformacion.R;

/**
 * Created by DanielRolando on 14/06/2015.
 */
public class BaseActivity extends AppCompatActivity {
    //https://encipher.it/
    //encryptation password = "onpe_registration_256"

    protected AlertDialog displayMessage(String message) {
        return displayMessage(message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    protected AlertDialog displayMessage(String message,DialogInterface.OnClickListener positiveAction) {
        return displayMessage(message,positiveAction,null);
    }

    protected AlertDialog displayMessage(String message,DialogInterface.OnClickListener positiveAction,DialogInterface.OnClickListener negativeAction) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_default_title));
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.dialog_default_positive), positiveAction);
        if (negativeAction!=null) {
            builder.setNegativeButton(getString(R.string.dialog_default_negative), negativeAction);
        }
        AlertDialog dialog = builder.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        return dialog;
    }
}
