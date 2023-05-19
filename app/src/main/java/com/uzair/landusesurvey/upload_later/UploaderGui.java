package com.uzair.landusesurvey.upload_later;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.uzair.landusesurvey.DataSender.Constants;
import com.uzair.landusesurvey.R;
import com.uzair.landusesurvey.database.DataBaseSQlite;
import com.uzair.landusesurvey.dialogs.progressdialog;

import java.util.ArrayList;


public class UploaderGui extends AppCompatActivity {
    public static ListView listView;

    public static String[] path = null;
    public static String[] path1 = null;
    public static String[] path2 = null;
    public static String[] path3 = null;
    public static String[] path4 = null;
    public static String[] path5 = null;
    public static String[] path6 = null;
    public static int[] localIDs;
    public static String[] circleIDS = null;
    public static String[] districtIDs = null;
    public static String[] srNumIDS = null;

    public static String[] puNumIDS = null;
    public static String[] DateTimeIDS = null;
    public static String[] DestructionIDS = null;
    public static String[] localityIDS = null;
    public static String[] wardIDS = null;
    public static String[] blockIDS = null;

    public static String[] parcelsIDS = null;
    public static String[] districtName = null;
    public static String[] tehsilName = null;

    public static String tableName = null;
    public static String URL = null;

    private int totalarecords;
    Context context;
    progressdialog ob = new progressdialog();

    public static ItemAdapter adapter;

    String circleName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploaderlayout);
        context = this;


        Intent intent = getIntent();
        tableName = intent.getExtras().getString("tblName");
        URL = intent.getExtras().getString("url");

        if (tableName.equalsIgnoreCase(Constants.TABLE_SURVEY_DATA)) {
            getSupportActionBar().hide();
        }
        SQLiteDatabase db = DataBaseSQlite.connectToDb(context);
        try {
            setSizeofArrays();
            int i = 1;
//            String query = "SELECT parcel_id||' - '||sub_parcel_id as parcel_id,imageProperty,imageBuilding, local_id, circle, sr_no, pu_num,locality,ward,block,imageBuilding, date_time, img_property1, img_ownership, img_utility, img_cnic_front, img_cnic_back FROM " + tableName + " WHERE status = '0' order by date_time desc";
            String query = "SELECT imageProperty,imagePu,imei,local_id,district_name,tehsil_name,parcel_id,mobile_data_time FROM " + tableName + " WHERE status = '0' order by mobile_data_time desc";
            Log.d("touch", query.toString());
            Cursor cur = db.rawQuery(query, null);
            while (cur.moveToNext()) {
                 if (UploaderGui.tableName.equalsIgnoreCase(Constants.TABLE_SURVEY_DATA))
                {
                    localIDs[i] = cur.getInt(cur.getColumnIndexOrThrow("local_id"));


                    districtName[i] = cur.getString(cur.getColumnIndexOrThrow("district_name"));
                    tehsilName[i] = cur.getString(cur.getColumnIndexOrThrow("tehsil_name"));
                    path[i] = cur.getString(cur.getColumnIndexOrThrow("imageProperty"));
                    path1[i] = cur.getString(cur.getColumnIndexOrThrow("imagePu"));
                    path2[i] = cur.getString(cur.getColumnIndexOrThrow("parcel_id"));
                    DateTimeIDS[i] = cur.getString(cur.getColumnIndexOrThrow("mobile_data_time"));
//                    DestructionIDS[i] = cur.getString(cur.getColumnIndexOrThrow("destruction_type"));
                }

                i++;
            }
            cur.close();

            //Model.LoadModel(path, localIDs, circleIDS, srNumIDS, puNumIDS, localityIDS, wardIDS, blockIDS,DateTimeIDS,parcelsIDS);
            Model.LoadModel(path, localIDs, districtName, tehsilName, path1, path2);
            listView = (ListView) findViewById(R.id.listView1);
            ArrayList<String> ids = new ArrayList<String>();
            for (int j = 0; j < totalarecords; j++) {
                ids.add(Integer.toString(j + 1));
            }
            adapter = new ItemAdapter(this, R.layout.row, ids, tableName);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"error is:"+e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            db.close();
        }
    }





    public static void removeRow(Context context) {
        ItemAdapter.Ids.remove(ItemAdapter.Ids.indexOf(ItemAdapter.pos + ""));
        adapter.notifyDataSetChanged();

        SQLiteDatabase db = DataBaseSQlite.connectToDb(context);
        try {
            String query = "SELECT * FROM " + tableName + " WHERE status = '0'";
            Cursor cur = db.rawQuery(query, null);
            cur.moveToFirst();

            if (cur.getCount() == 0) {
                Intent intentDsSurvey = new Intent(context, UploaderGui.class);
                intentDsSurvey.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentDsSurvey.putExtra("alert", "true");
                context.startActivity(intentDsSurvey);
            }
            cur.close();

        } catch (Exception e) {

        } finally {
            db.close();
        }
    }

    private void setSizeofArrays() {
        SQLiteDatabase db = DataBaseSQlite.connectToDb(context);
        try {
            String query = "SELECT count(local_id) FROM " + tableName + " WHERE status = '0' ";
            Cursor cur = db.rawQuery(query, null);
            cur.moveToFirst();
            totalarecords = cur.getInt(0);

            cur.close();

            if (totalarecords > 0) {
                path = new String[totalarecords + 1];
                path1 = new String[totalarecords + 1];
                path2 = new String[totalarecords + 1];
                localIDs = new int[totalarecords + 1];
                districtName = new String[totalarecords + 1];
                tehsilName = new String[totalarecords + 1];
                DateTimeIDS = new String[totalarecords + 1];
                DestructionIDS = new String[totalarecords + 1];

            } else {
            }
        } catch (Exception e) {

        } finally {
            db.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            am.getMemoryInfo(mi);
            long availableMemory = (mi.availMem / 1048576L);
            if (availableMemory < 50) {
                ob.dialog(this, "Your available memory is " + availableMemory + " MB, Please free some space for using the app.", "Alert!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



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


}
