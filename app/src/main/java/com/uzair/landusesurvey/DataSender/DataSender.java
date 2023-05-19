package com.uzair.landusesurvey.DataSender;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.widget.Toast;

import com.uzair.landusesurvey.R;
import com.uzair.landusesurvey.database.DataBaseSQlite;
import com.uzair.landusesurvey.dialogs.LocationDialogs;
import com.uzair.landusesurvey.dialogs.progressdialog;
import com.uzair.landusesurvey.send_request_to_server.Http_Data_sender;
import com.uzair.landusesurvey.seter_geter.TempData;
import com.uzair.landusesurvey.upload_later.UploaderGui;

import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class DataSender {
    Context context;
    Handler mHandler;
    MultipartEntity mEntity = null;
    Http_Data_sender obj_http;
    String strConnectionError;
    progressdialog ob = new progressdialog();
    LocationDialogs di = new LocationDialogs(context);
    String tableName;
    String URL;
    String strResponse;

    String part1 = "";
    String part2 = "";

    String LocalID = "";
    String Activity = "";

    public DataSender(Context context, String Activity, String URL, String tableName, String json) {
        this.context = context;
        this.tableName = tableName;
        this.URL = URL;
        this.Activity = Activity;

        mEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        try {
//                image_name = TempData.getImageName();
            LocalID = TempData.getLocalId();

            mEntity.addPart("completeAppData", new StringBody(json, "application/json", null));
            mEntity.addPart("district_name", new StringBody(TempData.getDistrict()));
            mEntity.addPart("tehsil_name", new StringBody(TempData.getTehsil()));
            mEntity.addPart("imei", new StringBody(TempData.getImei()));
            mEntity.addPart("version", new StringBody(context.getString(R.string.version)));
            mEntity.addPart("local_id", new StringBody(TempData.getLocalId()));
            mEntity.addPart("parcel_id", new StringBody(TempData.getParcel_id()));
            mEntity.addPart("sub_parcel_id", new StringBody(TempData.getSub_parcel()));
            mEntity.addPart("column_one", new StringBody(""));
            mEntity.addPart("column_two", new StringBody(""));

            mEntity.addPart("PropertyPictureOne", new StringBody(TempData.getImgeFileName())); // Property Picture
            mEntity.addPart("PropertyPictureTwo", new StringBody(TempData.getImgeFileName1())); // PU Hard Form Picture



            System.out.println("json111-"+json);
            System.out.println("imageProperty111-"+TempData.getImgeFileName());
            System.out.println("imagePu111-"+TempData.getImgeFileName1());
//            System.out.println("img_property1"+TempData.getImageFileName2());
            System.out.println("local_id111-"+TempData.getLocalId());
            System.out.println("version111-"+context.getString(R.string.version));
            System.out.println("imei111-"+TempData.getImei());
            System.out.println("district_name-"+TempData.getDistrict());
            System.out.println("tehsil_name-"+TempData.getTehsil());
            System.out.println("parcel_id-"+TempData.getParcel_id());
            System.out.println("sub_parcel_id-"+TempData.getSub_parcel());
            System.out.println("column_one-0");
            System.out.println("column_two-0");

//            mEntity.addPart("img_property1", new StringBody(TempData.getImageFileName2()));

        } catch (UnsupportedEncodingException e)
        {
//            e1.printStackTrace();
            Toast.makeText(context, "Error is99: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        mHandler = new Handler();
    }

    public void androidToServerERS() {
        try {
            if(tableName.equalsIgnoreCase(Constants.TABLE_SURVEY_DATA )) {

                File imageFile = new File(TempData.getImagePath());
                File imageFile1 = new File(TempData.getImagePath1());
//                File imageFile2 = new File(TempData.getImagePath2());

                if (!TempData.getImagePath().equalsIgnoreCase("") && imageFile.exists() && imageFile.isFile()) {
                    mEntity.addPart("PLACEMARKPHOTO", new FileBody(imageFile));
                    System.out.println("PLACEMARKPHOTO"+imageFile);

                }

                if (!TempData.getImagePath1().equalsIgnoreCase("") && imageFile1.exists() && imageFile1.isFile()) {
                    mEntity.addPart("PLACEMARKPHOTO1", new FileBody(imageFile1));
                    System.out.println("PLACEMARKPHOTO1"+imageFile1);

                }
//                if (!TempData.getImagePath2().equalsIgnoreCase("") && imageFile2.exists() && imageFile2.isFile()) {
//                    mEntity.addPart("PLACEMARKPHOTO2", new FileBody(imageFile2));
//                    System.out.println("PLACEMARKPHOTO2"+imageFile2);
//
//                }
//
//                if (!TempData.getImagePath3().equalsIgnoreCase("") && imageFile3.exists() && imageFile3.isFile()) {
//                    mEntity.addPart("PLACEMARKPHOTO3", new FileBody(imageFile3));
//                }
//
//                if (!TempData.getImagePath4().equalsIgnoreCase("") && imageFile4.exists() && imageFile4.isFile()) {
//                    mEntity.addPart("PLACEMARKPHOTO4", new FileBody(imageFile4));
//                }
//
//                if (!TempData.getImagePath5().equalsIgnoreCase("") && imageFile5.exists() && imageFile5.isFile()) {
//                    mEntity.addPart("PLACEMARKPHOTO5", new FileBody(imageFile5));
//                }
//
//                if (!TempData.getImagePath6().equalsIgnoreCase("") && imageFile6.exists() && imageFile6.isFile()) {
//                    mEntity.addPart("PLACEMARKPHOTO6", new FileBody(imageFile6));
//                }
                sendMultipartEntityToUUServer();
            }

        } catch (Exception e) {
            Toast.makeText(context, "Error is: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMultipartEntityToUUServer() {
        obj_http = new Http_Data_sender();
        Thread updateThrad = new Thread(new Runnable() {
            public void run() {
                try {

                    HttpResponse httpResponse = obj_http.sendMultipartHttpRequestWithURLAndValue(URL, mEntity);
                    strResponse = obj_http.convertHTTPResponseToStringUsingEntityUtils(httpResponse);
//                    Toast.makeText(context,strResponse,Toast.LENGTH_LONG).show();
                    String[] parts = strResponse.split(":");
                    part1 = parts[0];
                    part2 = parts[1];
                    if (part1.equalsIgnoreCase("200")) { // Successfully Inserted
                        mHandler.post(updateGUIBasedOnResponse);
                    } else if (part1.equalsIgnoreCase("602")) { // Version Response
                        mHandler.post(showConnectionErrorVersion);
                    } else if (part1.equalsIgnoreCase("302")) { // Data Fail
                        mHandler.post(showConnectionError21);
                    } else if (part1.equalsIgnoreCase("420")) { // Missing Parameter
                        mHandler.post(showConnectionError2);
                    } else if (part1.equalsIgnoreCase("702")) { // Image Problem
                        mHandler.post(showConnectionError3);
                    } else {
                        mHandler.post(showConnectionError22);
                    }
                } catch (Exception e) {
                    strConnectionError = e.getMessage();
                    mHandler.post(showConnectionError24);

                }
            }
        });
        updateThrad.start();
        runProgressDialog();
    }

    final Runnable updateGUIBasedOnResponse = new Runnable() {
        public void run() {
            stopProgressDialog();
            try {
                if (Integer.valueOf(part2) > 0) {
                    ob.dialog(context, "Data Uploaded Successfully,\nDatabase ID is: " + part2, "Congratulations!");
//                    Toast.makeText(context, "Data Uploaded Successfully,\nDatabase ID is: " + part2, Toast.LENGTH_LONG).show();
                    SQLiteDatabase db = DataBaseSQlite.connectToDb(context);
                    try {
                        String q = "UPDATE " + tableName + " set status=1,server_db_id='" + part2 + "' where local_id='" + LocalID + "'";
                        db.execSQL(q);

//                      image_name = "";
                        LocalID = "";
                        if (UploaderGui.listView != null) {
                            try {
                                UploaderGui.removeRow(context);
                            } catch (Exception e) {
                                Toast.makeText(context, "data sender" +e.getMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        db.close();
                    }
                } else {
                    ob.dialog(context, "Data upload  failed.", "Alert!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                ob.dialog(context, "Data upload  failed.", "Alert!");
            }
        }
    };

    final Runnable showConnectionErrorVersion = new Runnable() {
        public void run() {
            stopProgressDialog();
            ob.dialog(context, "Please Update the Application,\n\nData cannot uploaded because of incorrect application version number.", "Alert!");
        }
    };


    final Runnable showConnectionError2 = new Runnable() {
        public void run() {
            stopProgressDialog();
            ob.dialog(context, "Missing Parameter", "Alert!");
        }
    };

    final Runnable showConnectionError22 = new Runnable() {
        public void run() {
            stopProgressDialog();
            ob.dialog(context, "Data Uploading in overall.", "Alert!");
        }
    };

    final Runnable showConnectionError24 = new Runnable() {
        public void run() {
            stopProgressDialog();
            ob.dialog(context, "Data Uploading catch error  "+strConnectionError, "Alert!");
        }
    };
    final Runnable showConnectionError21 = new Runnable() {
        public void run() {
            stopProgressDialog();
            ob.dialog(context, "Data Uploading Data Fail", "Alert!");
        }
    };




    final Runnable showConnectionError3 = new Runnable() {
        public void run() {
            stopProgressDialog();
            ob.dialog(context, "Please register your Device Imei", "Alert!");
        }
    };

    protected void runProgressDialog() {
        di.runMyProgressDialog("Uploading Data...", context);
    }

    public void stopProgressDialog() {
        LocationDialogs.stopMyProgressDialog();
    }
}