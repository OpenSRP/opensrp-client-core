package org.smartregister.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.smartregister.R;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 16-04-2020.
 */
public class ResetAppDialog extends DialogFragment {

    private TextView instructionsTv;
    private DialogInterface.OnCancelListener onCancelListener;
    private String waitingText = null;

    public static ResetAppDialog newInstance() {
        ResetAppDialog frag = new ResetAppDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_reset_progress, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        return new AlertDialog.Builder(requireActivity())
                .setView(view)
                .setTitle(R.string.app_reset_status)
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (onCancelListener != null) {
                                    onCancelListener.onCancel(dialog);
                                }
                            }
                        }
                )
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        instructionsTv = getDialog().findViewById(R.id.resetApp_text);

        showText(waitingText);
    }

    public void showText(@NonNull String showText) {
        if (instructionsTv != null) {
            waitingText = null;
            instructionsTv.setText(showText);
        } else {
            waitingText = showText;
        }
    }

    public void setOnCancelListener(@NonNull DialogInterface.OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

}
