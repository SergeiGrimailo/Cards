package com.gmail.sgrimailo.cards;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Sergey on 6/27/2017.
 */

public class FileNameDialogFragment extends DialogFragment {

    public static final String ARGUMENT_FILE_NAME = genTag("ARGUMENT_FILE_NAME");
    public static final String ARGUMENT_FILE_EXT = genTag("ARGUMENT_FILE_EXT");

    private static String genTag(String aTagName) {
        return String.format("%s.%s", FileNameDialogFragment.class.getSimpleName(), aTagName);
    }

    public interface Listener {
        void onFileNameSelected(String aFileName);

        class Adapter implements Listener {
            @Override
            public void onFileNameSelected(String aFileName) {
            }
        }
    }

    private Listener mListener = new Listener.Adapter();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.file_name_dialog, null);

        EditText etFileName = (EditText) dialogView.findViewById(R.id.etFileName);
        String fileName = getArguments().getString(ARGUMENT_FILE_NAME);
        if (fileName != null) {
            String fileExt = getArguments().getString(ARGUMENT_FILE_EXT);
            if (fileExt != null) {
                fileName = String.format("%s.%s", fileName, fileExt);
            }
            etFileName.setText(fileName);
        }
        dialogBuilder.setView(dialogView);

        dialogBuilder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etFileName = (EditText) getDialog().findViewById(R.id.etFileName);
                        mListener.onFileNameSelected(etFileName.getText().toString());
                    }
                });
        dialogBuilder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        return dialogBuilder.create();
    }

    @Override
    public void onDetach() {
        mListener = new Listener.Adapter();
        super.onDetach();
    }
}
