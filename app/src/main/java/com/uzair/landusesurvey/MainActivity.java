package com.uzair.landusesurvey;

import static com.uzair.landusesurvey.SplashActivity.context;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.uzair.landusesurvey.app.AppController;
import com.uzair.landusesurvey.database.DataBaseSQlite;
import com.uzair.landusesurvey.dialogs.progressdialog;
import com.uzair.landusesurvey.seter_geter.TempData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText et_username, et_password;
    Button btnlogin;

    progressdialog ob;
    ProgressDialog progress;
    private ArrayList<ParcelData> arrParcelsData = new ArrayList<>();
    public static final String PREFS_NAME = "MyPrefsFile";
    private SharedPreferences  sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        context = this;
        ob = new progressdialog();
        progress = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences("epd_shared_prefs", MODE_PRIVATE);


        et_username = findViewById(R.id.etusername);
        et_password = findViewById(R.id.etpassword);
        btnlogin = findViewById(R.id.loginbtn);





    }

    public void login(View view) {

//        Intent intent = new Intent(MainActivity.this, MenuScreen.class);
//        startActivity(intent);

        try {
            if (!checkInternetConnection()){
//                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                ob.dialog(context, "No Internet Connection", "Note");
            } else {
                String username = et_username.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                if (username.length()==0){
                    ob.dialog(context, "User name is empty", "Note");
                }else{
                    if (password.length()==0){
                        ob.dialog(context, "Password is empty", "Note");
                    }else {
                        if (username.length()>0){
                            if (password.length()>0){
                                fetchDataJson(username,password);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void fetchDataJson(final String username, final String password) {

        String version = context.getString(R.string.version);

//        final String URL_Parcels_Data_JSON = "http://gdev.urbanunit.gov.pk/Apps/FloodDamage/auth/"+username+"/"+password+"";
        final String URL_Parcels_Data_JSON = "http://gdev.urbanunit.gov.pk/Apps/quetta/auth/"+username+"/"+password+"/"+version+"";

        System.out.println("URL_Parcels_Data_JSON"+URL_Parcels_Data_JSON);

        JsonArrayRequest req = new JsonArrayRequest(URL_Parcels_Data_JSON,
                new Response.Listener<JSONArray>() {


                    @Override
                    public void onResponse(JSONArray response) {
                        // Log.i(TAG, response.toString());

                        arrParcelsData.clear();
                        try {
                            SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0); // 0 - for private mode
                            SharedPreferences.Editor editor = settings.edit();
                            // Parsing json array response
                            // loop through each json object



                            for (int i = 0; i < response.length(); i++) {

                                ParcelData parcelData = new ParcelData();

                                JSONObject jsonObject = (JSONObject) response.get(i);
                                parcelData.setKey(jsonObject.getInt("id"));
                                arrParcelsData.add(parcelData);

                                TempData tempData = new TempData();

                                tempData.setImei(jsonObject.getString("imei"));

                                tempData.getImei();

                                System.out.println("IMEI :"+tempData.getImei());

                            }
                            Intent intent = new Intent(context, MenuScreen.class);
                            startActivity(intent);
                            finish();
                            new AsynTask().execute();



//                            new AsynTask().execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(),
//                                    Toast.LENGTH_LONG).show();
                            //   Toast.makeText(getApplicationContext(), "UserName Or Password is incorrect " , Toast.LENGTH_LONG).show();
                            ob.dialog(context, "Response error", "Error");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());

//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_SHORT).show();
                ob.dialog(context, "Username or Password is incorrect", "Note");

                //  Toast.makeText(getApplicationContext(), "UserName Or Password is incorrect " , Toast.LENGTH_LONG).show();

            }
        });

        // Adding request to request queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(req);
    }

    private class AsynTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(context, "", "Login...", true);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progress.isShowing()) {
                progress.dismiss();
                if (result == true) {

//                    ob.dialog(MainActivity.this, " Successfully Login", "Congratulation!");

                } else {
                    ob.dialog(MainActivity.this, "UserName Or Password Is Incorrect", "Sorry!");
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


                String delete = "DELETE from data_user";
                db.execSQL(delete);
                for (int i = 0; i < arrParcelsData.size(); i++) {
                    query = "INSERT into data_user(sur_id)" +
                            " VALUES ('" + arrParcelsData.get(i).getKey() +
                            "')";
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



    public boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // ARE WE CONNECTED TO THE NET
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }

}