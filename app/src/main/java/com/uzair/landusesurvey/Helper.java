package com.uzair.landusesurvey;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build.VERSION_CODES;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.UUID;


public class Helper {

    String tableName;
    Context context;
    static ProgressDialog pDialog;
    static ProgressDialog pDialog1;
    static ProgressDialog progressDialog;

    public Helper(Context context) {
        this.context = context;
    }

    /*public void ShowSummaryReportAlert(String tableName) {
//        this.context = context;
        this.tableName = tableName;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.survey_status);
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        TextView tv_total = (TextView) dialog.findViewById(R.id.tv_total_records_count);
        TextView tv_uploaded = (TextView) dialog.findViewById(R.id.tv_uploaded_records_count);
        TextView tv_pending = (TextView) dialog.findViewById(R.id.tv_pending_records_count);
        TextView btnOk = (TextView) dialog.findViewById(R.id.btnOk);

        SQLiteDatabase db = DataBaseSQlite.connectToDb(context);

        String query = "select industryType from " + tableName + " where industryType='1'";
        String query1 = "select industryType from " + tableName + "  where industryType not in('2')";

//        String query = "select industryType from " + tableName + " where industryType='1' and username= '" + userCheck + "'";
//        String query1 = "select industryType from " + tableName + "  where industryType not in('2') and username= '" + userCheck + "'";

        Cursor cur = db.rawQuery(query, null);
        Cursor cur1 = db.rawQuery(query1, null);

        int totalUploaded = cur.getCount();
        int totalRecords = cur1.getCount();
        int totalPending = totalRecords - totalUploaded;

        cur.close();
        cur1.close();
        db.close();

        tv_total.setText(String.valueOf(totalRecords));
        tv_uploaded.setText(String.valueOf(totalUploaded));
        tv_pending.setText(String.valueOf(totalPending));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }*/



//    public boolean showReportAlert(String tableName, String status, String userCheck) {
//        SQLiteDatabase db = DataBaseSQlite.connectToDb(context);
//        try {
//            String query = "select status from " + tableName + " where status='" + status + "'";
////            String query = "select status from " + tableName + " where status='" + status + "' and user = '" + userCheck + "'";
//            Cursor cur = db.rawQuery(query, null);
//            int totalUploaded = cur.getCount();
//            cur.close();
//            if (totalUploaded > 0) {
//                return true;
//            }
//        } catch (Exception e1) {
//            return false;
//        } finally {
//            db.close();
//        }
//
//        return false;
//    }

    public boolean isEmpty(EditText etText) {
        if (TextUtils.isEmpty(etText.getText().toString().trim())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public boolean isValidMobile(CharSequence target) {
        return (!TextUtils.isEmpty(target) && target.length() == 11);
    }

    public boolean hasGpsSensor() {
        PackageManager packMan = context.getPackageManager();
        return packMan.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    public boolean checkgps() {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps(context);

            return false;
        }
        return true;
    }

//    public int checkRecordsInDatabase(String TableName) {
//        int count = 0;
//        SQLiteDatabase db = DataBaseSQlite.connectToDb(context);
//        Cursor c = null;
//        try {
//            String query = "Select * from " + TableName;
//            c = db.rawQuery(query, null);
//            count = c.getCount();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (c != null) {
//                c.close();
//            }
//            db.close();
//        }
//        return count;
//    }

    public void buildAlertMessageNoGps(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert!");
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
//                        Intent intentDsSurvey = new Intent(context, MenuScreen.class);
//                        intentDsSurvey.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intentDsSurvey.putExtra("alert", "true");
//                        context.startActivity(intentDsSurvey);
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @SuppressLint("SimpleDateFormat")
    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }

    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;
                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                //		        Log.e("Got exception " + e.getMessage());
                Toast.makeText(context, "Error is" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if (count > 0)
            return true;
        return false;
    }


    public double getShortestPoint(double latitude, double longitude, double tbl_lat, double tbl_lon) {
        Location loc1 = new Location("");
        loc1.setLatitude(latitude);
        loc1.setLongitude(longitude);
        Location loc2 = new Location("");
        loc2.setLatitude(tbl_lat);
        loc2.setLongitude(tbl_lon);
        double distanceInMeters = loc1.distanceTo(loc2);
        return distanceInMeters;
    }

    public void TextChangeListenerLooper(LinearLayout linearLayout) {
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            if (linearLayout.getChildAt(i) instanceof LinearLayout) {
                LinearLayout rl = (LinearLayout) linearLayout.getChildAt(i);
                for (int j = 0; j < rl.getChildCount(); j++) {
                    if (rl.getChildAt(j) instanceof TextInputLayout) {
                        TextInputLayout til = (TextInputLayout) rl.getChildAt(j);
                        EditText et = til.getEditText();
//                        if (!(et.getId() == R.id.et_remarks)) {
//                        et.addTextChangedListener(new GenericTextWatcher(til, et));
//                        }
                    }
                }
            }
        }
    }

    public void ClearAllEditTexts(LinearLayout linearLayout) {
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            if (linearLayout.getChildAt(i) instanceof LinearLayout) {
                LinearLayout rl = (LinearLayout) linearLayout.getChildAt(i);
                for (int j = 0; j < rl.getChildCount(); j++) {
                    if (rl.getChildAt(j) instanceof TextInputLayout) {
                        TextInputLayout til = (TextInputLayout) rl.getChildAt(j);
                        EditText et = til.getEditText();
//                        if (!(et.getId() == R.id.et_remarks)) {
                        et.setText("");
//                        }
                    }
                }
            }
        }
    }

    public void dialog(Context context, String message, String title) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", null);
        alertDialog.create();
        alertDialog.show();
    }

/*    public void dialogUploadAll(final Context context, String message, String title, final String tableName) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                if (tableName.equalsIgnoreCase(Constants.TABLE_SCHEME_MONITORING)) {
                    Intent i = new Intent(context, UploaderGui.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("url", Constants.URL_SCHEME_MONITORING);
                    i.putExtra("tblName", Constants.TABLE_SCHEME_MONITORING);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(i);
                }
            }
        });


        alertDialog.create();
        alertDialog.show();
    }*/

    public void dialogFake(final Context context, String message, String title) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message);
        alertDialog.setTitle(title);
        alertDialog.setPositiveButton("OK", null);
//        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(final DialogInterface dialog, final int id) {
//                Intent intentDsSurvey = new Intent(context, MenuScreen.class);
//                intentDsSurvey.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intentDsSurvey.putExtra("alert", "true");
//                context.startActivity(intentDsSurvey);
//
//            }
//        });
        alertDialog.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void runMyProgressDialog(String message, Context context) {
        this.context = context;
        try {
            pDialog = new ProgressDialog(context);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage(message);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        } catch (Exception ex) {
        }
    }

    public void runMyProgressDialog1(String message, Context context) {
        this.context = context;
        try {
            pDialog1 = new ProgressDialog(context);
            pDialog1.setMessage(message);
            pDialog1.setIndeterminate(true);
            pDialog1.setCancelable(true);
            pDialog1.setCanceledOnTouchOutside(false);
            pDialog1.show();
        } catch (Exception ex) {
        }
    }


    //geting imei
//    @RequiresApi(api = VERSION_CODES.M)
    @RequiresApi(api = VERSION_CODES.M)
//    public String getImei() {
//         TelephonyManager telephonyManager = null;
//         try {
//             telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//             if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                 return null;
//             }
//             return telephonyManager.getDeviceId();
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//
//         Log.d("touch", "Return Sim count in device : "+telephonyManager.getPhoneCount());
//         Log.d("touch", "Defualt device ID: "+telephonyManager.getDeviceId());
//         Log.d("touch", "First Sim IMEI:  "+telephonyManager.getDeviceId(0));
//         Log.d("touch", "Secand Sim IMEI "+telephonyManager.getDeviceId(1));
//
//        String uniqueID = (UUID.randomUUID().toString()).substring(0,5);
//        return uniqueID;
//
//    }

    public static void stopMyProgressDialog() {
        pDialog.cancel();
    }

    public static void stopMyProgressDialog1() {
        pDialog1.cancel();
    }

//    public boolean checkInternetConnection() {
//        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        // ARE WE CONNECTED TO THE NET
//        if (conMgr.getActiveNetworkInfo() != null
//                && conMgr.getActiveNetworkInfo().isAvailable()
//                && conMgr.getActiveNetworkInfo().isConnected()) {
//            return true;
//        } else {
//            return false;
//        }
//    }
}
