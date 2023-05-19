package com.uzair.landusesurvey;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.uzair.landusesurvey.DataSender.Constants.TABLE_SURVEY_DATA;
import static com.uzair.landusesurvey.DataSender.Constants.URL_SURVEY_DATA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.uzair.landusesurvey.app.AppController;
import com.uzair.landusesurvey.database.DataBaseSQlite;
import com.uzair.landusesurvey.dialogs.progressdialog;
import com.uzair.landusesurvey.seter_geter.TempData;
import com.uzair.landusesurvey.upload_later.UploaderGui;
import com.uzair.landusesurvey.view_sent_records.ShowDetailReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class MenuScreen extends AppCompatActivity {

    Context context;
    progressdialog ob;

    ProgressDialog progressDialog;
    ProgressDialog insertDialog;
    ProgressDialog progress;

    private ArrayList<ParcelData> arrParcelsData = new ArrayList<>();

    AutoCompleteTextView et_parcel;
    Boolean CheckOk = true;
    ArrayList<String> parcels = new ArrayList<>();
    Boolean parcel_verify_dialog = false;
    String parcel_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_200)));

        context = this;
        ob = new progressdialog();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait fetching records...");
        progressDialog.setCancelable(false);

        insertDialog = new ProgressDialog(this);
        insertDialog.setMessage("Inserting Into Database Wait...");
        insertDialog.setCancelable(false);



    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main_screen,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
     int id = item.getItemId();
     if (id==R.id.sync){
         if (checkInternetConnection()) {
             fetchDataJson();
         } else {
             ob.dialog(context, "Internet is required for getting the data", "No Internet Connection");
         }
     }
//     if (id==R.id.updateapp){
//         Toast.makeText(this, "Update App", Toast.LENGTH_SHORT).show();
//     }
     if (id==R.id.number){
//         Toast.makeText(this, "View registered number", Toast.LENGTH_SHORT).show();
         ob.dialog(context, TempData.getImei(), "IMEI");
     }
     if (id==R.id.logout){
//         Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
         SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0); // 0 - for private mode
         SharedPreferences.Editor editor = settings.edit();
         //  editor.putBoolean("hasLoggedOut", true);

         editor.clear();
         editor.apply();
         finish();
         Intent intent = new Intent(MenuScreen.this, MainActivity.class);
         startActivity(intent);
         finish();
     }

        return super.onOptionsItemSelected(item);
    }

    public void btnsurveyform(View view) {
//        Intent intent = new Intent(MenuScreen.this, LandUseForm.class);
//        startActivity(intent);

        Dialog dialog=new Dialog(MenuScreen.this);
        dialog.setContentView(R.layout.searchserialnumber_layout);
        dialog.setCancelable(false);
        dialog.show();
        TextView canceldialog=dialog.findViewById(R.id.canceldialog);
        et_parcel = dialog.findViewById(R.id.searchserialnumber);
        dialog.show();
        canceldialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_parcel.getText().clear();
                et_parcel.setEnabled(true);
                dialog.dismiss();
                parcel_id ="";
            }
        });

        et_parcel.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                CheckOk = false;
                et_parcel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);


            }
        });

        ImageButton refresh = (ImageButton) dialog.findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_parcel.getText().clear();
                et_parcel.setEnabled(true);
                parcel_id ="";
            }
        });

        parcels.clear();


        try {
            int counted = 0;
            SQLiteDatabase db = DataBaseSQlite.connectToDb(context);
            String query = "select uu_id from data_gis";
            Cursor cur = db.rawQuery(query, null);
            counted = cur.getCount();
            if (counted > 0) {
//                        Toast.makeText(context, "Parcel Number Found", Toast.LENGTH_LONG).show();
                parcel_verify_dialog = true;
                while (cur.moveToNext()) {
                    parcels.add(cur.getString(cur.getColumnIndexOrThrow("uu_id")));
                }
                cur.close();
                db.close();
            }

            CustomAdapter customAdapter = new CustomAdapter(parcels, context);

//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, parcels);
            et_parcel.setThreshold(2);
            et_parcel.setAdapter(customAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageButton searchserialnumberbtn = dialog.findViewById(R.id.searchserialnumberbtn);

        searchserialnumberbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parcel_id = et_parcel.getText().toString().trim();
                if (parcels.contains(parcel_id)){
                    et_parcel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_circle, 0);
                    TempData.setParcel_id(parcel_id);
                }
                else {
                    Toast.makeText(context, "Parcel Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView okdialog=dialog.findViewById(R.id.okdialog);

        okdialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (parcels.contains(parcel_id)){
                    Intent i = new Intent(MenuScreen.this, LandUseForm.class);
                    i.putExtra("parcel_id",parcel_id);
                    startActivity(i);

                    parcel_id ="";
                }else {
                    Toast.makeText(MenuScreen.this, "Please Select a valid Parcel Id", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void MapsFunc(View view) {
        Intent intent = new Intent(MenuScreen.this, MapsAtivity.class);
        startActivity(intent);
    }

    public void btnOutbox(View view) {
        openSentRecords();
    }

    private void openSentRecords(){

//                    try {
                        if (showReportAlert(TABLE_SURVEY_DATA, "1")) {
                            Intent i = new Intent(MenuScreen.this, ShowDetailReport.class);
                            i.putExtra("tblName", TABLE_SURVEY_DATA);
                            startActivity(i);

                        } else {
                            ob.dialog(context, "No uploaded records to display.", "Alert!");
                        }
//                    } catch (Exception e) {
//                        ob.dialog(context, "Cannot Access Your Memory card.", "Alert!");
//                        SplashActivity.CreateNewDataBase();
//                    }
    }
    private boolean showReportAlert(String tableName, String status) {
        SQLiteDatabase db = DataBaseSQlite.connectToDb(context);
        try {
            String query = "select * from " + tableName + " where status='" + status + "'";
//            String query = "select status,circle from " + tableName + " where status='" + status + "'";
//            String query = "select status,circle from " + tableName + " where circle='" + circleName + "'";
            Cursor cur = db.rawQuery(query, null);
            int totalUploaded = cur.getCount();
            cur.close();
            if (totalUploaded > 0) {
                return true;
            }
        } catch (Exception e1) {
            Toast.makeText(getApplicationContext(),"Error: "+e1.getMessage(),Toast.LENGTH_LONG).show();
            return false;
        } finally {
            db.close();
        }
        return false;
    }

    public void btnPending(View view) {
        uploadPendingData();
    }

    private void uploadPendingData() {
        //   ob.dialog(context, "Under Progress.", "Alert!");
        try {
            //showDialog();
            if (showReportAlert(TABLE_SURVEY_DATA, "0")) {
                Intent i = new Intent(MenuScreen.this, UploaderGui.class);
                i.putExtra("url", URL_SURVEY_DATA);
                i.putExtra("tblName", TABLE_SURVEY_DATA);
                startActivity(i);
            } else {
                ob.dialog(context, "No records to display.", "Alert!");
            }
        } catch (Exception e) {
            ob.dialog(this, "Cannot Access Your Memory card.", "Alert!");
            SplashActivity.CreateNewDataBase();
        }
    }

//    private boolean showReportsAlert(String tableName, String status) {
//        SQLiteDatabase db = DataBaseSQlite.connectToDb(context);
//        try {
//            String query = "select status from " + tableName + " where status='" + status + "' ";
////            String query = "select status,circle from " + tableName + " where status='" + status + "'";
////            String query = "select status,circle from " + tableName + " where circle='" + circleName + "'";
//            Cursor cur = db.rawQuery(query, null);
//            int totalUploaded = cur.getCount();
//            cur.close();
//            if (totalUploaded > 0) {
//                return true;
//            }
//        } catch (Exception e1) {
//            Toast.makeText(getApplicationContext(),"Error: "+e1.getMessage(),Toast.LENGTH_LONG).show();
//            return false;
//        } finally {
//            db.close();
//        }
//        return false;
//    }

    public boolean checkInternetConnection() {
        try {
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // ARE WE CONNECTED TO THE NET
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                return true;//just to check readsqlitedatabase class
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void fetchDataJson() {
//        final String URL_Parcels_Data_JSON = "http://gdev.urbanunit.gov.pk/Apps/FloodDamage/sync";
//        final String URL_Parcels_Data_JSON = "http://gdev.urbanunit.gov.pk/Apps/quetta/GISDataSync.php";
        final String URL_Parcels_Data_JSON = "http://gdev.urbanunit.gov.pk/Apps/quetta/gissync/00/00";


        showpDialogFetch();

        JsonArrayRequest req = new JsonArrayRequest(URL_Parcels_Data_JSON,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i(TAG, response.toString());

                        arrParcelsData.clear();
                        try {

                            for (int i = 0; i < response.length(); i++) {

                                ParcelData parcelData = new ParcelData();

                                JSONObject jsonObject = (JSONObject) response.get(i);

                                parcelData.setUu_id((jsonObject.getInt("uu_id")));
                                parcelData.setZone(jsonObject.getString("zone"));

                                parcelData.setDistrict(jsonObject.getString("district"));


                                arrParcelsData.add(parcelData);
                            }

                            hidepDialogFetch();
                            new AsynTask().execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ob.dialog(context, "Data Insertion Problem Contact To Admin.", "Alert");

//                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(),
//                                    Toast.LENGTH_LONG).show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), "No Data of this Circle: ",
//                        Toast.LENGTH_LONG).show();


                ob.dialog(context,"No Data is available against Your Circle Kindly contact to Admin.","Alert!");

                SplashActivity.CreateNewDataBase();
//                Intent intent=new Intent(MenuScreen.this,LoginActivity.class);
//                startActivity(intent);
//                finish();
                hidepDialogFetch();



            }
        });

        // Adding request to request queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(req);
    }

    private void showpDialogFetch() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hidepDialogFetch() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private class AsynTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(context, "", "Inserting Into Database wait...", true);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progress.isShowing()) {
                progress.dismiss();
                if (result == true) {
                    ob.dialog(MenuScreen.this, "DataBase Successfully Synced", "Congratulation!");
                } else {
                    ob.dialog(MenuScreen.this, "DataBase Not Synced", "Sorry!");
                }
            }
        }

        //         && FillParcelData2()
//            && arrParcelsData2.size() > 0
        @Override
        protected Boolean doInBackground(Void... arg0) {

            Boolean result = false;
            if (arrParcelsData.size() > 0 ) {
                if (FillParcelData() )  {
                    result = true;
                } else {
                    result = false;
                }
            } else {
                result = false;
            }
            return result;
        }
    }

    private boolean FillParcelData() {
        boolean val = false;
        String query = "";
        try {
            try {

                SQLiteDatabase db = DataBaseSQlite.connectToDb(context);

                db.beginTransaction();


                String delete = "DELETE from data_gis";
                db.execSQL(delete);
                for (int i = 0; i < arrParcelsData.size(); i++) {
                    query = "INSERT into data_gis(uu_id,zone,district)" +
                            " VALUES ('"
                            + arrParcelsData.get(i).getUu_id()  + "','" + arrParcelsData.get(i).getZone() + "','"
                            + arrParcelsData.get(i).getDistrict()  + "')";
                    db.execSQL(query);
                    System.out.println(query);
                }
                val = true;

                db.setTransactionSuccessful();
                db.endTransaction();

                return val;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(query);
                System.out.println(e.getMessage());
                val = false;
                return val;
            }
        } catch (SQLiteAbortException e) {

            e.printStackTrace();
            System.out.println(query);
            System.out.println(e.getMessage());
            val = false;
            return val;

        }
    }

}