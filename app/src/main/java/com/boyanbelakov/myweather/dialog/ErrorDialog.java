package com.boyanbelakov.myweather.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.boyanbelakov.myweather.R;


public class ErrorDialog extends DialogFragment {
    private static final String ARG_USER_TEXT = "user_text";
    private static final String ARG_TECHNICAL_TEXT = "technical_text";

    public static ErrorDialog newInstance(String userText, String technicalText) {
        Bundle args = new Bundle();
        args.putString(ARG_USER_TEXT, userText);
        args.putString(ARG_TECHNICAL_TEXT, technicalText);

        ErrorDialog fragment = new ErrorDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_error, null);

        CheckBox checkBox = (CheckBox) v.findViewById(R.id.dialog_error_checkBox);
        final TextView textView = (TextView) v.findViewById(R.id.dialog_error_textView);

        checkBox.setChecked(false);
        textView.setText(getArguments().getString(ARG_USER_TEXT));

        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    textView.setText(getArguments().getString(ARG_TECHNICAL_TEXT));
                } else {
                    textView.setText(getArguments().getString(ARG_USER_TEXT));
                }
            }

        });

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.error)
                .setView(v)
                .create();
    }
}
