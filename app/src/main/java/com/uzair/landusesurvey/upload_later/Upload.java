package com.uzair.landusesurvey.upload_later;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.Log;

import com.esri.arcgisruntime.internal.apachehttp.client5.http.HttpHostConnectException;
import com.uzair.landusesurvey.R;
import com.uzair.landusesurvey.database.DataBaseSQlite;
import com.uzair.landusesurvey.dialogs.progressdialog;
import com.uzair.landusesurvey.seter_geter.TempData;


import java.io.File;

public class Upload {
    String imageProperty, imagepu, completeAppData, local_id, circle ,district,srNum,puNum ,locality_name,locality_code,ward,block,destruction_type, imei,parcel_id;

    String image2, image3, image4, image5, image6;
    //    JSONObject json = new JSONObject();
    progressdialog ob = new progressdialog();
    Context context;
    String tableName = "";
    public void upload(Context context, String s, String tableName) throws HttpHostConnectException {
        SQLiteDatabase db = DataBaseSQlite.connectToDb(context);
        try {
            this.context = context;
            this.tableName = tableName;
                String query = "SELECT * FROM " + UploaderGui.tableName + " WHERE status = '0' and local_id = '" + UploaderGui.localIDs[Integer.parseInt(s)] + "'";
                Log.d("touch", "" + query);
                Cursor cur = db.rawQuery(query, null);
                if (cur.getCount() > 0)
                {
                    cur.moveToFirst();
                    while (cur.isAfterLast() == false)
                    {
                        imageProperty = cur.getString(cur.getColumnIndexOrThrow("imageProperty"));
                        imagepu = cur.getString(cur.getColumnIndexOrThrow("imagePu"));
//                        image2 = cur.getString(cur.getColumnIndexOrThrow("img_property1"));
                        completeAppData = cur.getString(cur.getColumnIndexOrThrow("completeAppData"));
                        local_id = cur.getString(cur.getColumnIndexOrThrow("_id_pk"));
                        imei = cur.getString(cur.getColumnIndexOrThrow("imei"));
                        destruction_type = cur.getString(cur.getColumnIndexOrThrow("destruction_type"));

                        cur.moveToNext();
                    }
                    cur.close();
//                    final String _path = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.images_folder) + "/";
                    String _path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator  + context.getString(R.string.images_folder) + "/";

                    TempData.setImagePath(_path + imageProperty);
                    TempData.setImageName(imageProperty);
                    TempData.setImageFileName(imageProperty);

                    TempData.setImagePath1(_path + imagepu);
                    TempData.setImageName1(imagepu);
                    TempData.setImageFileName1(imagepu);

//                    TempData.setImagePath2(_path + image2);
//                    TempData.setImageName2(image2);
//                    TempData.setImageFileName2(image2);

//                    TempData.setCircle(circle);
                    TempData.setDistrict(district);
//

                    TempData.setLocalId(local_id);
                    TempData.setImei(imei);

//                    TempData.setLocality_Name(locality_name);
//                    TempData.setLocality_code(locality_code);
//
//                    TempData.setDestruction_type(destruction_type);



                    try {
                        if (checkInternetConnection())
                        {
//                            DataSender ob = new DataSender(context, "UploadSurveyForm", UploaderGui.URL, UploaderGui.tableName, completeAppData);
//                            ob.androidToServerERS();
                        } else {
                            ob.dialog(context, "Please check your data connection.", "No Internet!");
                        }
                    } catch (Exception e) {
                        ob.dialog(context, "Internet Connection Error.", "Alert!");
                    }
                } else {
                    ob.dialog(context, "Record Already Uploaded.", "Alert!");
                }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
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
        } catch (Exception e)
        {
            return false;
        }
    }
}