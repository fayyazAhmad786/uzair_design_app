package com.uzair.landusesurvey.dialogs;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class LocationDialogs {
    Context locationContext;
    Dialog settingDialog;
    static ProgressDialog pDialog;
    static ProgressDialog pDialog1;

    public LocationDialogs(Context lContext) {
        locationContext = lContext;
    }

    public void padAlertbox(String tit, String message) {
        try {
            new AlertDialog.Builder(locationContext).setMessage(message)
                    .setTitle(tit)
                    .setCancelable(true)
                    .setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            }).show();
        } catch (Exception ex) {
            Toast.makeText(locationContext, ex.getMessage() + "ld", Toast.LENGTH_SHORT).show();
        }
    }

    public void runMyProgressDialog(String message, Context lContext) {
        this.locationContext = lContext;
        try {
            pDialog = new ProgressDialog(locationContext);
            pDialog.setMessage(message);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();

        } catch (Exception ex) {
            // String strError = ex. getMessage();
        }

    }

    public void runMyProgressDialog1(String message, Context lContext) {
        this.locationContext = lContext;
        try {
            pDialog1 = new ProgressDialog(locationContext);
            pDialog1.setMessage(message);
            pDialog1.setIndeterminate(true);
            pDialog1.setCancelable(true);
            pDialog1.setCanceledOnTouchOutside(false);
            pDialog1.show();
        } catch (Exception ex) {
        }
    }

    public static void stopMyProgressDialog() {
        pDialog.cancel();

    }

    public static void stopMyProgressDialog1() {
        pDialog1.cancel();

    }

}
