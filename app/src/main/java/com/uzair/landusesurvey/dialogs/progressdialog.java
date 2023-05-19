package com.uzair.landusesurvey.dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;



public class progressdialog extends Activity {
    private ProgressDialog progressDialog;

    public void runDialog(final int seconds, Context cn) {
        progressDialog = ProgressDialog.show(cn, "Request Sending ....", "Please Wait");

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(seconds * 1000);
                    progressDialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressWarnings("deprecation")
    public void dialog(Context context, String message, String title) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message);
        alertDialog.setTitle(title);
        alertDialog.setPositiveButton("OK", null);
        alertDialog.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void dialogFake(final Context context, String message, String title) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message);
        alertDialog.setTitle(title);
//        alertDialog.setPositiveButton("OK", null);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                Intent intentDsSurvey = new Intent(context, progressdialog.class);
                intentDsSurvey.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentDsSurvey.putExtra("alert", "true");
                context.startActivity(intentDsSurvey);
            }
        });
        alertDialog.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

}
