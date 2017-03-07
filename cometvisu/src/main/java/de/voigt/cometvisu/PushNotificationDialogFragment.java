package de.voigt.cometvisu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class PushNotificationDialogFragment extends DialogFragment {

    private static final String PREFIX = PushNotificationDialogFragment.class.getPackage().toString();
    public static final String TITLE_KEY = "title";
    public static final String TITLE = PREFIX + "."+ TITLE_KEY;
    public static final String MESSAGE_KEY = "message";
    public static final String MESSAGE = PREFIX + "."+ MESSAGE_KEY;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getArguments().getString(MESSAGE))
                .setTitle(getArguments().getString(TITLE))
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }

}
