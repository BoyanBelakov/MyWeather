package com.boyanbelakov.myweather.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.boyanbelakov.myweather.R;


public class AboutDialog extends DialogFragment {

    public static AboutDialog newInstance() {
        return new AboutDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        try {
            Activity activity = getActivity();
            PackageManager packageManager = activity.getPackageManager();
            String pn = activity.getPackageName();
            PackageInfo info = packageManager.getPackageInfo(pn, 0);
            String text = getResources().getString(R.string.about_app, info.versionName);
            builder.setMessage(text);
        } catch (NameNotFoundException ignored) {
        }

        return builder.create();
    }

}