package com.uzair.landusesurvey.database;



import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.uzair.landusesurvey.DataSender.Constants;
import com.uzair.landusesurvey.ParcelData;
import com.uzair.landusesurvey.R;
import com.uzair.landusesurvey.seter_geter.TempData;


public class Querries {

    public static long insertIntoLocalDB_Survey_Data(Context context) {
        long local_id = 0;
        SQLiteDatabase db = DataBaseSQlite.connectToDb(context);
        try {
            ContentValues values = new ContentValues();

            values.put("completeAppData", TempData.getCompleteAppDataJsonString());
            values.put("status", "0");

            values.put("imei", TempData.getImei());
            values.put("mobile_data_time", TempData.getDateTime());
            values.put("district_name", TempData.getDistrict());
            values.put("tehsil_name", TempData.getTehsil());

            //Saving Image names in db
            values.put("imageProperty", TempData.getImgeFileName());
            values.put("imagePu", TempData.getImgeFileName1());
            values.put("parcel_id", TempData.getParcel_id());
            values.put("sub_parcel_id", TempData.getSub_parcel());

//            values.put("lat", TempData.getlatitude());
//            values.put("lng", TempData.getlongitude());

            values.put("version", context.getString(R.string.version));

            try {
                 local_id = db.insertWithOnConflict(Constants.TABLE_SURVEY_DATA, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            TempData.setonclick("1");
        } finally {
            db.close();
        }
        return local_id;
    }






}
