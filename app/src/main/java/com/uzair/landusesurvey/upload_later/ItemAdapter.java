package com.uzair.landusesurvey.upload_later;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uzair.landusesurvey.R;
import com.uzair.landusesurvey.dialogs.progressdialog;

import java.io.File;
import java.util.ArrayList;


public class ItemAdapter extends ArrayAdapter<String> {

    private Context context;
    public static ArrayList<String> Ids;
    private int rowResourceId;
    private Upload up;
    public static int pos = -1;

    final int THUMBNAIL_SIZE = 50;
    String imageFile = "";
    Bitmap b = null;
    progressdialog ob = new progressdialog();
    String tableName;


    public ItemAdapter(Context context, int textViewResourceId, ArrayList<String> objects, String tableName) {
        super(context, textViewResourceId, objects);
        this.context = context;
        Ids = objects;
        this.rowResourceId = textViewResourceId;
        up = new Upload();
        this.tableName = tableName;
    }

    private void showBiggerImage(String imageName) {
        try {
            ImageView ivPreview = null;

//            final String _path = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.images_folder) + "/";

            String _path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator  + context.getString(R.string.images_folder) + "/";

            String imageFile = _path + imageName;
            File imageFile1 = new File(imageFile);

            final Dialog nagDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            nagDialog.setCancelable(false);
            nagDialog.setContentView(R.layout.preview_image);
            ImageView btnClose = (ImageView) nagDialog.findViewById(R.id.btnIvClose);
            ivPreview = (ImageView) nagDialog.findViewById(R.id.iv_preview_image);
            if (!imageFile.equalsIgnoreCase("") && imageFile1.exists() && imageFile1.isFile()) {
                Bitmap b = BitmapFactory.decodeFile(imageFile);
                Drawable dd = new BitmapDrawable(b);
                ivPreview.setBackgroundDrawable(dd);
                btnClose.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        nagDialog.dismiss();
                    }
                });
                nagDialog.show();
            } else {
                ob.dialog(context, "Property Picture is missing.", "Alert!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater;
        View rowView = null;
        if (rowView==null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(rowResourceId, parent, false);
        }
        try {
            final ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
            final ImageView btnUploads = (ImageView) rowView.findViewById(R.id.btnUpload);
            TextView tvCircle = (TextView) rowView.findViewById(R.id.tvCircle);
            TextView tvSrNum = (TextView) rowView.findViewById(R.id.tvSrNum);
            TextView tvPUNum = (TextView) rowView.findViewById(R.id.tvPUNum);
            TextView tvParcel = (TextView)rowView.findViewById(R.id.tvParcel);
//            TextView tvLocality = (TextView) rowView.findViewById(R.id.tvLocality);
//            TextView tvWard = (TextView) rowView.findViewById(R.id.tvWard);
//            TextView tvBlock = (TextView) rowView.findViewById(R.id.tvBlock);
            TextView tvDateTime = (TextView) rowView.findViewById(R.id.tvDateTime);

            final int id = Integer.parseInt(Ids.get(position));
            tvCircle.setText("District: " + UploaderGui.districtName[id]);
            tvSrNum.setText("Tehsil: " + UploaderGui.tehsilName[id]);
            tvPUNum.setText("Local Id: " + UploaderGui.localIDs[id]);
            tvParcel.setText("Parcel Id :" + UploaderGui.path2[id]);

            // to be change
//            tvLocality.setText("Locality: " + UploaderGui.localityIDS[id]);
//            tvWard.setText("Ward: " + UploaderGui.wardIDS[id]);
//            tvBlock.setText("Parcel ID: " + UploaderGui.blockIDS[id]);

            String datetime = UploaderGui.DateTimeIDS[id];
            String parts = datetime.substring(0, 16);
            tvDateTime.setText("Date time: " + parts);

            btnUploads.setTag(id);
            btnUploads.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        pos = Integer.parseInt(btnUploads.getTag().toString());
                        up.upload(getContext(), btnUploads.getTag().toString(), tableName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            imageFile = "";
//            String _path = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.images_folder) + "/";
            String _path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator  + context.getString(R.string.images_folder) + "/";

            imageFile = _path + UploaderGui.path[id];
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 5;
            if (!UploaderGui.path[id].equalsIgnoreCase(""))
				b = BitmapFactory.decodeFile(imageFile, options);
//            b = BitmapFactory.decodeFile(imageFile);
            if (b != null) {
                Bitmap b1 = Bitmap.createScaledBitmap(b, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
                imageView.setImageBitmap(b1);
            }
            imageView.setTag(id);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBiggerImage(UploaderGui.path[(Integer) imageView.getTag()]);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowView;
    }

}
