package com.uzair.landusesurvey.view_sent_records;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uzair.landusesurvey.R;


public class SimpleArrayAdapterDS extends ArrayAdapter<String> {
    private final Context context;
    private final String[] status;
    private final String[] server_db_id;
    private final String[] date_time;
    ///////////////////////////////
    private final String[] arrayList1;
    private final String[] arrayList2;
    private final String[] arrayList3;
//    private final String[] arrayList4;
//    private final String[] arrayList5;

    String tableName;
    String cityCheck;

    ImageView sentimg;

    public SimpleArrayAdapterDS(Context context, String[] status, String[] server_db_id, String[] date_time,
                                String[] arrayList1, String[] arrayList3,
                                String[] list3, String[] arrayList2, String tableName, String cityCheck) {
        super(context, R.layout.list_item_ds, status);
        this.context = context;
        this.status = status;
        this.server_db_id = server_db_id;
        this.date_time = date_time;
        //////////////////////////
        this.arrayList1 = arrayList1;
//        this.arrayList2 = arrayList2;
        this.arrayList3 = arrayList3;
        this.arrayList2 = arrayList2;
//        this.arrayList4 = arrayList4;
//        this.arrayList5 = arrayList5;
        //////////////////////////
        this.tableName = tableName;
        this.cityCheck = cityCheck;
    }

    class MyViewHolder {
        //      ImageView hdStatus;
        LinearLayout imageLayout;
        TextView hdDate;
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tv4;
        TextView tv5;
        TextView tv6;

        MyViewHolder(View rowView) {
//          hdStatus = (ImageView) rowView.findViewById(R.id.hdStatus);
            hdDate = (TextView) rowView.findViewById(R.id.tvDateTime);
            tv1 = (TextView) rowView.findViewById(R.id.tv1);
            tv2 = (TextView) rowView.findViewById(R.id.tv2);
            tv3 = (TextView) rowView.findViewById(R.id.tv3);
            tv4 = (TextView) rowView.findViewById(R.id.tv4);
            tv5 = (TextView) rowView.findViewById(R.id.tv5);
            tv6 = (TextView) rowView.findViewById(R.id.tv6);
            sentimg = (ImageView) rowView.findViewById(R.id.sentimg);

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater;
        View row = convertView;
        MyViewHolder holder = null;
        if (row == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item_ds, parent, false);
            holder = new MyViewHolder(row);
            row.setTag(holder);
            Log.d("touch", "Creating: " + position);
        } else {
            holder = (MyViewHolder) row.getTag();
            Log.d("touch", "Recycling: " + position);
        }
        holder.hdDate.setText("Date time: " + date_time[position].substring(0, 16).toString());
        holder.tv1.setText("IMEI: " + arrayList1[position]);
//        holder.tv2.setText("Form Number: " + arrayList2[position]);
        holder.tv3.setText("ID_PK: " + arrayList3[position]);
        //holder.tv4.setText("PU Number: " + arrayList4[position]);
        //holder.tv4.setText("Parcel ID: " + arrayList5[position]);
//        holder.tv6.setText("Database ID: " + server_db_id[position]);
        holder.tv6.setText("Status: " + status[position]);
//        holder.tv3.setVisibility(View.GONE);
        holder.tv2.setVisibility(View.GONE);
        holder.tv4.setVisibility(View.GONE);
        holder.tv5.setVisibility(View.GONE);
//        if (tableName.equalsIgnoreCase(Constants.TABLE_VILLAGE_MAPPING)) {
//            holder.tv1.setVisibility(View.GONE);
//        }

        return row;
    }

}

