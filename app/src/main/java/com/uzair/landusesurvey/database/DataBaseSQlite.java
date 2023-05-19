package com.uzair.landusesurvey.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

import com.uzair.landusesurvey.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataBaseSQlite {
    private String folderName;
    private static String folder;
    private String strInputDBFileName;
    private String strInputDBFileName1;
    private String ac_name;
    private String dbName = "/uu.db";
    private String dbName1 = "/TrainingManual.pdf"; // Manual
    private Context context;

    public DataBaseSQlite(String folderName, Context context) {
        this.strInputDBFileName = "/" + folderName + dbName;
        this.strInputDBFileName1 = "/" + folderName + dbName1; // Manual
        DataBaseSQlite.folder = folderName;
        this.folderName = "/" + folderName;
        this.context = context;
        dataBase();
    }

    public DataBaseSQlite(Context applicationContext) {


    }

    public static SQLiteDatabase connectToDb(Context context) {
        String folderName = context.getString(R.string.db_name);
//        String dbPath = Environment.getExternalStorageDirectory() + "/" + folderName + "/uu.db";//rwp_citysurvey_dp/uu.db";
        String dbPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator  + folderName + "/uu.db";
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
        return db;
    }

    public boolean dataBase() {
        boolean b = false;
        boolean isExist = checkingDatabaseFileOnSdCard();
        if (isExist == true) {
            /**************** Read Database File or do Nothing ********************/
//            ReadWriteDatabase();
            b = true;
        } else {
            /*********************** Copy DB File From Assets Folder to SD Card *******************/
            copyFromAssetsToSDCard("uu.db");
//            copyManualFromAssetsToSdCard("TrainingManual.pdf");  // Manual
            ReadWriteDatabase();
            b = true;
        }
        return b;
    }//end of dataBase
    private String ReadWriteDatabase() {
        try {


        } catch (Exception ex) {
            Toast.makeText(context, "Error Reading Database file = " + ex.getMessage() + "", Toast.LENGTH_LONG).show();
        }//end of try-catch
        return ac_name;
    }//end of ReadWriteDatabase

    private void copyManualFromAssetsToSdCard(String fileName) {

        // TODO Auto-generated method stub
        try {
            //Open your local db as the input stream
            InputStream myInput = context.getAssets().open(fileName);

            //Open the empty db as the output stream
            String fPath = Environment.getExternalStorageDirectory() + folderName;
            File f = new File(fPath);
            if (!f.exists()) {
                f.mkdirs();
            }
            String dbFilePath = Environment.getExternalStorageDirectory() + strInputDBFileName1;
            File dbFile = new File(dbFilePath);
            OutputStream myOutput = new FileOutputStream(dbFile);
            //transfer bytes from the input-file to the output-file
            byte[] buffer = new byte[2048];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception ex) {
            Toast.makeText(context, "Error Copying Training Manual file = " + ex.getMessage() + "", Toast.LENGTH_LONG).show();
        }
    }






















    private void copyFromAssetsToSDCard(String strDBFile) {
        // TODO Auto-generated method stub
        try {
            //Open your local db as the input stream
            InputStream myInput = context.getAssets().open(strDBFile);

            //Open the empty db as the output stream
            String fPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + folderName;

//            String fPath = Environment.getExternalStorageDirectory() + folderName;
            File f = new File(fPath);
            if (!f.exists()) {
                f.mkdirs();
            }

            String dbFilePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + strInputDBFileName;

            System.out.println("pathhhh:"+dbFilePath);
//            String dbFilePath = Environment.getExternalStorageDirectory() + strInputDBFileName;
            File dbFile = new File(dbFilePath);
            OutputStream myOutput = new FileOutputStream(dbFile);
            //transfer bytes from the input-file to the output-file
            byte[] buffer = new byte[2048];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception ex) {
            Toast.makeText(context, "Error Copying Database file = " + ex.getMessage() + "", Toast.LENGTH_LONG).show();
        }
    }//end of copyFromAssetsToSDCard

//    private String ReadWriteDatabase() {
//        try {
//
//
//        } catch (Exception ex) {
//            Toast.makeText(context, "Error Reading Database file = " + ex.getMessage() + "", Toast.LENGTH_LONG).show();
//        }//end of try-catch
//        return ac_name;
//    }//end of ReadWriteDatabase


    private boolean checkingDatabaseFileOnSdCard() {
        boolean isExist = false;
        try {
            String strFilePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + strInputDBFileName;
//            String strFilePath = Environment.getExternalStorageDirectory() + strInputDBFileName;
            File dbFile = new File(strFilePath);
            if (dbFile.exists()) {
                isExist = true;
            } else {
                isExist = false;
            }
        } catch (Exception ex) {
            Toast.makeText(context, "Error Reading Database file = " + ex.getMessage() + "", Toast.LENGTH_LONG).show();
            return false;
        }//end of try-catch
        return isExist;
    }//end of checkingDatabaseFileOnSdCard


    public List<String> getAllLabels() {
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT survey_form FROM uipt_summary_data30";


        SQLiteDatabase db = DataBaseSQlite.connectToDb(context);
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }
}
