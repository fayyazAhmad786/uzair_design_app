package com.uzair.landusesurvey.view_sent_records;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.uzair.landusesurvey.Helper;
import com.uzair.landusesurvey.R;
import com.uzair.landusesurvey.database.DataBaseSQlite;

import java.util.ArrayList;

public class ShowDetailReport extends AppCompatActivity {

    private LinearLayout layoutListView;
    private ListView listView;

    Context context;
    String tableName;
    int counted = 0;
    SimpleArrayAdapterDS adapter;

    ArrayList<String> server_db_id = null;
    ArrayList<String> statusArray;
    ArrayList<String> dateTime = null;

    ArrayList<String> arrayList1 = null;
    ArrayList<String> arrayList2 = null;
    ArrayList<String> arrayList3 = null;
    ArrayList<String> arrayList4 = null;
    ArrayList<String> arrayList5 = null;
    ArrayList<String> arrayList6 = null;
    String circle_id = null;

    private int mYear, mMonth, mDay;

    Helper helper;

    public static String cityCheck = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail_report);

        getSupportActionBar().hide();

        context = this;
        helper = new Helper(context);

        Intent intent = getIntent();
        tableName = intent.getStringExtra("tblName");
        cityCheck = intent.getExtras().getString("cityCheckParam");
//        districtAndCircleCode();
        findViews();

        server_db_id = new ArrayList<String>();
        statusArray = new ArrayList<String>();
        dateTime = new ArrayList<String>();

        arrayList1 = new ArrayList<String>();
        arrayList2 = new ArrayList<String>();
        arrayList3 = new ArrayList<String>();
        arrayList4 = new ArrayList<String>();
        arrayList5 = new ArrayList<String>();
        arrayList6 = new ArrayList<String>();

        statusArray.clear();
        server_db_id.clear();
        dateTime.clear();

        arrayList1.clear();
        arrayList2.clear();
        arrayList3.clear();
        arrayList4.clear();
        arrayList5.clear();
        arrayList6.clear();

        SQLiteDatabase db = DataBaseSQlite.connectToDb(context);

        String query = "SELECT * FROM '" + tableName + "' where status = '0' order by mobile_data_time desc";
//        String query = "SELECT * FROM '" + tableName + "' where status = '1' order by mobile_data_time desc";
        Log.d("touch", query.toString());
        Cursor cur = db.rawQuery(query, null);
        counted = cur.getCount();

        try {
            if (counted > 0) {
                while (cur.moveToNext()) {
                    arrayList1.add(cur.getString(cur.getColumnIndexOrThrow("imei")));
                    arrayList2.add(cur.getString(cur.getColumnIndexOrThrow("_id_pk")));
//                    arrayList3.add(cur.getString(cur.getColumnIndex("district")));
//                    arrayList4.add(cur.getString(cur.getColumnIndex("pu_num")));
//                    arrayList5.add(cur.getString(cur.getColumnIndex("block")));
//                    server_db_id.add(cur.getString(cur.getColumnIndex("server_db_id")));
                    statusArray.add(cur.getString(cur.getColumnIndexOrThrow("status")));
                    dateTime.add(cur.getString(cur.getColumnIndexOrThrow("mobile_data_time")));
                }

                cur.close();
                String[] arrayList1 = new String[this.arrayList1.size()];
                arrayList1 = this.arrayList1.toArray(arrayList1);

                String[] arrayList2 = new String[this.arrayList2.size()];
                arrayList2 = this.arrayList2.toArray(arrayList2);

                String[] arrayList3 = new String[this.arrayList3.size()];
                arrayList3 = this.arrayList3.toArray(arrayList3);

//                String[] arrayList4 = new String[this.arrayList4.size()];
//                arrayList4 = this.arrayList4.toArray(arrayList4);
//                String[] arrayList5 = new String[this.arrayList5.size()];
//                arrayList5 = this.arrayList5.toArray(arrayList5);

                ////////////////////////////////////////////////
                String[] server_db_id = new String[this.server_db_id.size()];
                server_db_id = this.server_db_id.toArray(server_db_id);
                String[] statusArr = new String[statusArray.size()];
                statusArr = statusArray.toArray(statusArr);
                String[] date_time = new String[dateTime.size()];
                date_time = dateTime.toArray(date_time);
                /////////////////          Yah sahi karna hy          ///////////////
//                adapter = new SimpleArrayAdapterDS(this, statusArr,server_db_id, date_time, arrayList1, arrayList2, arrayList3, arrayList4,  arrayList5, tableName, cityCheck);
                adapter = new SimpleArrayAdapterDS(this, statusArr,server_db_id, date_time, arrayList1, arrayList2, arrayList3, arrayList2, tableName, cityCheck);
                listView.setAdapter(adapter);
//                etSearchBar.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
////						 When user changed the Text
//                        ShowDetailReport.this.adapter.getFilter().filter(cs);
//                    }
//
//                    @Override
//                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable arg0) {
//                    }
//
//                });
            } else {
                helper.dialog(this, "There are no records to display.", "Alert!");
            }
        }
//        catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(context, "Exception:" + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
        finally {
            db.close();
        }



    }

    private void findViews() {
        layoutListView = (LinearLayout) findViewById(R.id.layoutListView);
        listView = (ListView) findViewById(R.id.listView);
    }
}