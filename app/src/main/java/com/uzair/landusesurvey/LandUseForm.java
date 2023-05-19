package com.uzair.landusesurvey;

import static android.view.View.VISIBLE;

import static com.uzair.landusesurvey.R.color.black;
import static com.uzair.landusesurvey.R.color.purple_200;
import static com.uzair.landusesurvey.R.color.red;
import static com.uzair.landusesurvey.R.color.teal_200;
import static com.uzair.landusesurvey.R.color.white;
import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.internal.apachehttp.client5.http.HttpHostConnectException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.uzair.landusesurvey.DataSender.Constants;
import com.uzair.landusesurvey.DataSender.DataSender;
import com.uzair.landusesurvey.database.DataBaseSQlite;
import com.uzair.landusesurvey.database.Querries;
import com.uzair.landusesurvey.dialogs.progressdialog;
import com.uzair.landusesurvey.seter_geter.TempData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.UUID;

import me.echodev.resizer.Resizer;

public class LandUseForm extends AppCompatActivity {

    Spinner spn_landuse, spn_usage, spn_htofbuilding, spn_condofbuilding, spn_typeofbuilding, spn_sub_parcel, spn_district,
            spn_tehsil, spn_mc, spn_town_uc;

    EditText et_observed_by, et_remarks;

    ImageView img_property, img_property1;
//    ImageView img_property2;

    ArrayList<String> arrayList_landuse, htofbuilding, arrayList_condofbuilding, arrayList_typeofbuilding;
    ArrayAdapter<String> arrayAdapter_parent, arrayAdapter_htofbuilding, arrayAdapter_condofbuilding, arrayAdapter_typeofbuilding,
                        arrayAdapter_sub_parcel, arrayAdapter_district, arrayAdapter_tehsil, arrayAdapter_mc, arrayAdapter_town_uc;
    ArrayAdapter<String> arrayAdapter_child;
    ArrayList<String> arrayList_Residential, arrayList_Commercial, arrayList_Mixed_Land_use, arrayList_Industries,
                        arrayList_Health, arrayList_Education, arrayList_Religious, arrayList_Public_Buildings,
                        arrayList_Green_and_Open_spaces, arrayList_Tourism, arrayList_Sports_and_Recreational;

    Button search_parcel,send_save;

    LinearLayout parent_layout, spnusagelayout, subparcellayout;
    ScrollView landuselayout;
    JSONObject json;
    boolean edEmptyCheck = false;
    long timeNow;
    private Location mLocation;
    String provider = null;
    Helper helper;
    Context context;
    String imageName;
    String _path = "";
    int camera1 = 0;
    Boolean imageok = false;
    Boolean imageok1 = false;
    Bitmap bitmapOrg;
    private int THUMBNAIL_SIZE = 60;
    boolean Location_Yes = false;
    LocationManager lm;
    LocationListener ls;
    Boolean extraValue = true;
    Boolean user_id = false;
    Boolean empty = false;
    progressdialog ob;

    TextView tv_usage, greenSpaces, streetFurniture, buildingLine, perceivedDensity, tv_landuse, tv_sub_parcel, tv_parcel_id;

    RadioGroup rad1, rad2, rad3, rad4, rad5;
    RadioButton radyes, radno, rbhigh, rbmedium, rblow, rblinear, rbsomeinstaces, rbencroachment, rbavailable,
            rbsomeavailable, rbnonexistent, rbfrequent, rbnone;

    int position;
    int usagepos, hbuildingpos, cbuildingpos, tbuildingpos, tehsilpos, mcpos, townucpos;
//    Boolean radioOk = false;
    Boolean CheckOk = true;
    Boolean parcel_verify_dialog = false;
    AutoCompleteTextView et_parcel;
    ArrayList<String> parcels = new ArrayList<>();
    String parcel_id;

    String [] subparcels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_use_form);

        getSupportActionBar().hide();


        context = this;
        helper = new Helper(context);
        ob = new progressdialog();

        findViews();
        fillSpinners();
        radioButtons();
        CameraProperty();
        functionSendDataFinal();

        Intent intent = getIntent();

        parcel_id = intent.getStringExtra("parcel_id");

        tv_parcel_id.setText("Parcel Id : "+parcel_id);


//        search_parcel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                search_parcel_function();
//            }
//        });

    }

    private void radioButtons() {

        json = new JSONObject();

        rad5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.radyes:
                        subparcellayout.setVisibility(VISIBLE);
                        subparcels = new String[]{"Select Sub Parcel", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
                                "20", "21", "22", "23", "24", "25"};
                        arrayAdapter_sub_parcel = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, subparcels);
                        spn_sub_parcel.setAdapter(arrayAdapter_sub_parcel);

                        spn_sub_parcel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                TempData.setSub_parcel(String.valueOf(i));
                                if (i==0){
                                    spn_tehsil.setEnabled(false);
                                    spn_tehsil.setSelection(0);
                                }else {
                                    if (i>0){
                                        spn_tehsil.setEnabled(true);
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        break;
                    case R.id.radno:

                        spn_tehsil.setEnabled(true);
                        spn_tehsil.setSelection(0);
                        subparcellayout.setVisibility(View.GONE);


                        subparcels = new String[]{"0"};
                        arrayAdapter_sub_parcel = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, subparcels);
                        spn_sub_parcel.setAdapter(arrayAdapter_sub_parcel);

                        TempData.setSub_parcel(radno.getTag().toString());

//                        spn_sub_parcel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
////                                TempData.setSub_parcel(String.valueOf(i));
//
//                            }
//
//                            @Override
//                            public void onNothingSelected(AdapterView<?> adapterView) {
//
//                            }
//                        });
                        break;
                }
            }
        });


        rad1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (!(rad1.getCheckedRadioButtonId()==-1)){
                    buildingLine.setVisibility(VISIBLE);
                    rad2.setVisibility(VISIBLE);
                }else {
                    buildingLine.setVisibility(View.GONE);
                    rad2.setVisibility(View.GONE);
                    rad2.clearCheck();
                }
            }
        });

        rad2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (!(rad2.getCheckedRadioButtonId()==-1)){
                        streetFurniture.setVisibility(VISIBLE);
                        rad3.setVisibility(VISIBLE);
                }else {
                    streetFurniture.setVisibility(View.GONE);
                    rad3.setVisibility(View.GONE);
                    rad3.clearCheck();
                }
            }
        });

        rad3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (!(rad3.getCheckedRadioButtonId()==-1)){
                    greenSpaces.setVisibility(VISIBLE);
                    rad4.setVisibility(VISIBLE);
                }else {
                    greenSpaces.setVisibility(View.GONE);
                    rad4.setVisibility(View.GONE);
                    rad4.clearCheck();
                }
            }
        });


    }

    private void getdata() throws JSONException {

        json = new JSONObject();

        json.put("parcel_id", parcel_id);

        for (int i = 0; i < parent_layout.getChildCount(); i++) {
            if (parent_layout.getChildAt(i) instanceof LinearLayout) {
                LinearLayout rl = (LinearLayout) parent_layout.getChildAt(i);
                getAllLayouts(rl);
            }
        }

        try {
            String currentTime = null;
            DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mmZ");
            if (timeNow > 0)
                currentTime = dateFormat1.format(new Date(timeNow));

            if (currentTime == null || currentTime.equalsIgnoreCase("")) {
                currentTime = dateFormat1.format(new Date());
                json.put("date_time_mobile", currentTime);
            } else {
                json.put("date_time_mobile", currentTime);
            }

            TempData.setDateTime(currentTime);

            json.put("lat", TempData.getlatitude().toString());
            json.put("lng", TempData.getlongitude().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("data_json : "+json);
    }

    private void getAllLayouts(LinearLayout rl) throws JSONException {
        for (int i = 0; i < rl.getChildCount(); i++) {
            if (rl.getChildAt(i) instanceof TextInputLayout) {
                TextInputLayout til = (TextInputLayout) rl.getChildAt(i);
                EditText et = til.getEditText();
                if (et.getVisibility() == VISIBLE) {
                    if (isEmpty(et)) {
                        String value = et.getTag().toString();
                        if ( value.equalsIgnoreCase("et_observed_by")  || value.equalsIgnoreCase("et_remarks"))
                        {
                            edEmptyCheck = false;
                            String a = et.getText().toString();
                            til.setErrorEnabled(true);
                            til.setError("Compulsory field");
                        }else{
                            json.put(et.getTag().toString(), "NA");
                        }


                    } else {
                        try {
                            json.put(et.getTag().toString(), et.getText().toString());
                            til.setErrorEnabled(false);
                            til.setError(null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else { json.put(et.getTag().toString(), "NA");


                }
            }

            if (rl.getChildAt(i) instanceof Spinner) {
                Spinner spn = (Spinner) rl.getChildAt(i);
                if (spn.getVisibility() == VISIBLE) {
                    if(spn != null && spn.getSelectedItem().toString().trim() !=null){
                        try {
                            json.put(spn.getTag().toString(), spn.getSelectedItem().toString().trim());

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"bilal: "+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }else{

                    }

                } else {
                    try {
                        json.put(spn.getTag().toString(), "NA");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (rl.getChildAt(i) instanceof RadioGroup) {

                RadioGroup chk = (RadioGroup) rl.getChildAt(i);
                RadioButton rb = (RadioButton) findViewById(chk.getCheckedRadioButtonId());

                if (chk.getVisibility() == VISIBLE) {
                    if (!(chk.getCheckedRadioButtonId() == -1)) {
                        try {
                            json.put(chk.getTag().toString(), rb.getTag().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                }
            }
    }

    private boolean isEmpty(EditText et) {
        if (TextUtils.isEmpty(et.getText().toString().trim())) {
            return true;
        } else {
            return false;
        }
    }

//    private void search_parcel_function() {
//        Dialog dialog=new Dialog(LandUseForm.this);
//        dialog.setContentView(R.layout.searchserialnumber_layout);
//        dialog.setCancelable(false);
//        TextView canceldialog=dialog.findViewById(R.id.canceldialog);
//        et_parcel = dialog.findViewById(R.id.searchserialnumber);
//        dialog.show();
//        canceldialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                et_parcel.getText().clear();
//                et_parcel.setEnabled(true);
//                if (rad5.isEnabled()){
//                    radyes.setEnabled(false);
//                    radno.setEnabled(false);
//                    rad5.clearCheck();
//                    subparcellayout.setVisibility(View.GONE);
//                }
//                dialog.dismiss();
//            }
//        });
////                propertyimagebtn=dialog.findViewById(R.id.propertyimagebtn);
////                propertyimagebtn.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View view) {
////                        Intent intent=new Intent(SurveyFormDetailsActivity.this, imageViewActivity.class);
////                        startActivity(intent);
////                    }
////                });
//
//
//        et_parcel.addTextChangedListener(new TextWatcher() {
//
//            public void afterTextChanged(Editable s) {
//            }
//
//            public void beforeTextChanged(CharSequence s, int start,
//                                          int count, int after) {
//            }
//
//            public void onTextChanged(CharSequence s, int start,
//                                      int before, int count) {
//                CheckOk = false;
//                et_parcel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
//
//
//            }
//        });
//
//
//        ImageButton refresh = (ImageButton) dialog.findViewById(R.id.refresh);
//
//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                et_parcel.getText().clear();
//                et_parcel.setEnabled(true);
//
//            }
//        });
//
//        parcels.clear();
//        try {
//            int counted = 0;
//            SQLiteDatabase db = DataBaseSQlite.connectToDb(context);
//            String query = "select uu_id from data_gis";
//            Cursor cur = db.rawQuery(query, null);
//            counted = cur.getCount();
//            if (counted > 0) {
////                        Toast.makeText(context, "Parcel Number Found", Toast.LENGTH_LONG).show();
//                parcel_verify_dialog = true;
//                while (cur.moveToNext()) {
//                    parcels.add(cur.getString(cur.getColumnIndexOrThrow("uu_id")));
//                }
//                cur.close();
//                db.close();
//            }
//
//            CustomAdapter customAdapter = new CustomAdapter(parcels, context);
//
////                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, parcels);
//            et_parcel.setThreshold(2);
//            et_parcel.setAdapter(customAdapter);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        ImageButton searchserialnumberbtn = dialog.findViewById(R.id.searchserialnumberbtn);
//
//        searchserialnumberbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                parcel_id = et_parcel.getText().toString().trim();
//                if (parcels.contains(parcel_id)){
//                    et_parcel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_circle, 0);
//                    TempData.setParcel_id(parcel_id);
//                }
//                else {
//                    Toast.makeText(context, "Parcel Not Found", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        TextView okdialog=dialog.findViewById(R.id.okdialog);
//
//        okdialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                if (parcels.contains(parcel_id)){
//                    radyes.setClickable(true);
//                    radno.setClickable(true);
//                    radyes.setEnabled(true);
//                    radno.setEnabled(true);
//                }
//            }
//        });

//    }

    private void fillSpinners() {

//        parent of landuse
        arrayList_landuse = new ArrayList<>();
        arrayList_landuse.add(0,"Select Landuse");
        arrayList_landuse.add("Commercial");
        arrayList_landuse.add("Education");
        arrayList_landuse.add("Green and Open spaces");
        arrayList_landuse.add("Health");
        arrayList_landuse.add("Industries");
        arrayList_landuse.add("Mixed Land use");
        arrayList_landuse.add("Public Buildings");
        arrayList_landuse.add("Religious");
        arrayList_landuse.add("Residential");
        arrayList_landuse.add("Sports and Recreational");
        arrayList_landuse.add("Tourism");

//        Collections.sort(arrayList_landuse, new Comparator<String>()
//        {
//            @Override
//            public int compare(String text1, String text2)
//            {
//                return text1.compareToIgnoreCase(text2);
//            }
//        });

        arrayAdapter_parent = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, arrayList_landuse);
        spn_landuse.setAdapter(arrayAdapter_parent);

//        childs
//        position8
        arrayList_Residential = new ArrayList<>();
        arrayList_Residential.add(0,"Select Usage");
        arrayList_Residential.add("Apartment/Flat");
        arrayList_Residential.add("House");


//        position0
        arrayList_Commercial = new ArrayList<>();
        arrayList_Commercial.add(0, "Select Usage");
        arrayList_Commercial.add("Hotels");
        arrayList_Commercial.add("Marriage Halls");
        arrayList_Commercial.add("Offices");
        arrayList_Commercial.add("Petrol/CNG Station");
        arrayList_Commercial.add("Restaurants");
        arrayList_Commercial.add("Retail shops");
        arrayList_Commercial.add("Wholesale shops");


//        position5
        arrayList_Mixed_Land_use = new ArrayList<>();
        arrayList_Mixed_Land_use.add(0, "Select Usage");
        arrayList_Mixed_Land_use.add("Commercial/industrial ");
        arrayList_Mixed_Land_use.add("Commercial/institutional");
        arrayList_Mixed_Land_use.add("Miscellaneous");
        arrayList_Mixed_Land_use.add("Residential/commercial");
        arrayList_Mixed_Land_use.add("Residential/industrial");
        arrayList_Mixed_Land_use.add("Residential/Institutional");

//        position4
        arrayList_Industries = new ArrayList<>();
        arrayList_Industries.add(0, "Select Usage");
        arrayList_Industries.add("Beverage");
        arrayList_Industries.add("Cold Storage");
        arrayList_Industries.add("Construction Material");
        arrayList_Industries.add("Food");
        arrayList_Industries.add("Furniture Industry");
        arrayList_Industries.add("Handicrafts");
        arrayList_Industries.add("Industrial Warehouse");
        arrayList_Industries.add("Light Engineering");
        arrayList_Industries.add("Marble Processing");
        arrayList_Industries.add("Miscellaneous");
        arrayList_Industries.add("Packaging Unit");
        arrayList_Industries.add("Pharmaceuticals");
        arrayList_Industries.add("PVC Pipes");
        arrayList_Industries.add("RCC Pipes");
        arrayList_Industries.add("Soap Factory");
        arrayList_Industries.add("Steel Mill");
        arrayList_Industries.add("Textile");
        arrayList_Industries.add("Tobacco Factory");

//        Collections.sort(arrayList_Industries, new Comparator<String>()
//        {
//            @Override
//            public int compare(String text1, String text2)
//            {
//                return text1.compareToIgnoreCase(text2);
//            }
//        });


//        position3
        arrayList_Health = new ArrayList<>();
        arrayList_Health.add(0, "Select Usage");
        arrayList_Health.add("Clinics");
        arrayList_Health.add("Dispensaries");
        arrayList_Health.add("Emergency Response");
        arrayList_Health.add("Labs");
        arrayList_Health.add("Pharmacies");
        arrayList_Health.add("Private Hospital");
        arrayList_Health.add("Public Hospital");
        arrayList_Health.add("Veterinary Clinic/Hospital");


//        position1
        arrayList_Education = new ArrayList<>();
        arrayList_Education.add(0, "Select Usage");
        arrayList_Education.add("Colleges");
        arrayList_Education.add("Higher Secondary School");
        arrayList_Education.add("Middle School");
        arrayList_Education.add("Primary School");
        arrayList_Education.add("Secondary School");
        arrayList_Education.add("Technical & vocational institutes");
        arrayList_Education.add("Universities");

//        position7
        arrayList_Religious = new ArrayList<>();
        arrayList_Religious.add(0, "Select Usage");
        arrayList_Religious.add("Churches");
        arrayList_Religious.add("Graveyards");
        arrayList_Religious.add("Gurdwaras");
        arrayList_Religious.add("Imam Bargah");
        arrayList_Religious.add("Mausoleums");
        arrayList_Religious.add("Mosques");
        arrayList_Religious.add("Prayer Grounds");


//        position6
        arrayList_Public_Buildings = new ArrayList<>();
        arrayList_Public_Buildings.add(0, "Select Usage");
        arrayList_Public_Buildings.add("Banks and FIs");
        arrayList_Public_Buildings.add("Community halls");
        arrayList_Public_Buildings.add("Courts");
        arrayList_Public_Buildings.add("Government Offices");
        arrayList_Public_Buildings.add("Libraries");
        arrayList_Public_Buildings.add("Police stations");
        arrayList_Public_Buildings.add("Post Office");


//        position2
        arrayList_Green_and_Open_spaces = new ArrayList<>();
        arrayList_Green_and_Open_spaces.add(0, "Select Usage");
        arrayList_Green_and_Open_spaces.add("Agricultural Land");
        arrayList_Green_and_Open_spaces.add("Green Belts");
        arrayList_Green_and_Open_spaces.add("Parks");
        arrayList_Green_and_Open_spaces.add("Playgrounds");
        arrayList_Green_and_Open_spaces.add("Urban Forest");
        arrayList_Green_and_Open_spaces.add("Vacant Land");

//        Collections.sort(arrayList_Green_and_Open_spaces, new Comparator<String>()
//        {
//            @Override
//            public int compare(String text1, String text2)
//            {
//                return text1.compareToIgnoreCase(text2);
//            }
//        });


//        position10
        arrayList_Tourism = new ArrayList<>();
        arrayList_Tourism.add(0, "Select Usage");
        arrayList_Tourism.add("Archeological Site");
        arrayList_Tourism.add("Forts");
        arrayList_Tourism.add("Heritage Site");
        arrayList_Tourism.add("Monument");
        arrayList_Tourism.add("Museum");


//        position9
        arrayList_Sports_and_Recreational = new ArrayList<>();
        arrayList_Sports_and_Recreational.add(0, "Select Usage");
        arrayList_Sports_and_Recreational.add("Cinemas");
        arrayList_Sports_and_Recreational.add("Fair Grounds/Recreation Centre");
        arrayList_Sports_and_Recreational.add("Indoor Sports Centre");
        arrayList_Sports_and_Recreational.add("Sports Complex");
        arrayList_Sports_and_Recreational.add("Stadiums");
        arrayList_Sports_and_Recreational.add("Theaters");

        spn_landuse.setEnabled(false);


        spn_landuse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                position = i;

                if (i == 0){
                    spnusagelayout.setVisibility(View.GONE);
                    spn_htofbuilding.setSelection(0);
                    spn_htofbuilding.setEnabled(false);
                }

                if (i == 1){
                    arrayAdapter_child = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item,arrayList_Commercial);
                    spn_usage.setAdapter(arrayAdapter_child);
                    spnusagelayout.setVisibility(VISIBLE);
                    spn_usage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            usagepos =i;
                            checkusageposition();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                if (i == 2){
                    arrayAdapter_child = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item,arrayList_Education);
                    spn_usage.setAdapter(arrayAdapter_child);
                    spnusagelayout.setVisibility(VISIBLE);
                    spn_usage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            usagepos =i;
                            checkusageposition();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                if (i == 3){
                    arrayAdapter_child = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item,arrayList_Green_and_Open_spaces);
                    spn_usage.setAdapter(arrayAdapter_child);
                    spnusagelayout.setVisibility(VISIBLE);
                    spn_usage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            usagepos =i;
                            checkusageposition();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                if (i == 4){
                    arrayAdapter_child = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item,arrayList_Health);
                    spn_usage.setAdapter(arrayAdapter_child);
                    spnusagelayout.setVisibility(VISIBLE);
                    spn_usage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            usagepos =i;
                            checkusageposition();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                if (i == 5){
                    arrayAdapter_child = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item,arrayList_Industries);
                    spn_usage.setAdapter(arrayAdapter_child);
                    spnusagelayout.setVisibility(VISIBLE);
                    spn_usage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            usagepos =i;
                            checkusageposition();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                if (i == 6){
                    arrayAdapter_child = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item,arrayList_Mixed_Land_use);
                    spn_usage.setAdapter(arrayAdapter_child);
                    spnusagelayout.setVisibility(VISIBLE);
                    spn_usage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            usagepos =i;
                            checkusageposition();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                if (i == 7){
                    arrayAdapter_child = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item,arrayList_Public_Buildings);
                    spn_usage.setAdapter(arrayAdapter_child);
                    spnusagelayout.setVisibility(VISIBLE);
                    spn_usage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            usagepos =i;
                            checkusageposition();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                if (i == 8){
                    arrayAdapter_child = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item,arrayList_Religious);
                    spn_usage.setAdapter(arrayAdapter_child);
                    spnusagelayout.setVisibility(VISIBLE);
                    spn_usage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            usagepos =i;
                            checkusageposition();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                if (i == 9){
                    arrayAdapter_child = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item,arrayList_Residential);
                    spn_usage.setAdapter(arrayAdapter_child);
                    spnusagelayout.setVisibility(VISIBLE);
                    spn_usage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            usagepos =i;
                            checkusageposition();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                if (i == 10){
                    arrayAdapter_child = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item,arrayList_Sports_and_Recreational);
                    spn_usage.setAdapter(arrayAdapter_child);
                    spnusagelayout.setVisibility(VISIBLE);
                    spn_usage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            usagepos =i;
                            checkusageposition();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                if (i == 11){
                    arrayAdapter_child = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item,arrayList_Tourism);
                    spn_usage.setAdapter(arrayAdapter_child);
                    spnusagelayout.setVisibility(VISIBLE);
                    spn_usage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            usagepos =i;
                            checkusageposition();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        height of building
        htofbuilding = new ArrayList<>();
        htofbuilding.add(0,"Select Height of Building");
        htofbuilding.add("Single Story");
        htofbuilding.add("Double Story");
        htofbuilding.add("Triple Story");
        htofbuilding.add("High Rise Building(>Storey)");

        arrayAdapter_htofbuilding = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, htofbuilding);
        spn_htofbuilding.setAdapter(arrayAdapter_htofbuilding);

        spn_htofbuilding.setEnabled(false);

        spn_htofbuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                hbuildingpos =i;
                if (i==0){
                    spn_condofbuilding.setEnabled(false);
                    spn_condofbuilding.setSelection(0);
                }else {
                    if (i>0){
                        spn_condofbuilding.setEnabled(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        condition of building
        arrayList_condofbuilding = new ArrayList<>();
        arrayList_condofbuilding.add(0,"Select Condition of Building");
        arrayList_condofbuilding.add("Bad");
        arrayList_condofbuilding.add("Good");
        arrayList_condofbuilding.add("Normal");


        arrayAdapter_condofbuilding = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, arrayList_condofbuilding);
        spn_condofbuilding.setAdapter(arrayAdapter_condofbuilding);

        spn_condofbuilding.setEnabled(false);

        spn_condofbuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cbuildingpos =i;
                if (i==0){
                    spn_typeofbuilding.setEnabled(false);
                    spn_typeofbuilding.setSelection(0);
                }else {
                    if (i>0){
                        spn_typeofbuilding.setEnabled(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        type of building
        arrayList_typeofbuilding = new ArrayList<>();
        arrayList_typeofbuilding.add(0,"Select Type of Building");
        arrayList_typeofbuilding.add("Katcha House");
        arrayList_typeofbuilding.add("Pacca House");
        arrayList_typeofbuilding.add("Semi Pacca");

//        Collections.sort(arrayList_typeofbuilding, new Comparator<String>()
//        {
//            @Override
//            public int compare(String text1, String text2)
//            {
//                return text1.compareToIgnoreCase(text2);
//            }
//        });

        arrayAdapter_typeofbuilding = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, arrayList_typeofbuilding);
        spn_typeofbuilding.setAdapter(arrayAdapter_typeofbuilding);

        spn_typeofbuilding.setEnabled(false);

        spn_typeofbuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tbuildingpos =i;
                if(i==0){
                    rbhigh.setClickable(false);
                    rbmedium.setClickable(false);
                    rblow.setClickable(false);
                    rad1.clearCheck();
                }else {
                    if (i>0){
                        rbhigh.setClickable(true);
                        rbmedium.setClickable(true);
                        rblow.setClickable(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] districts = {"Quetta"};

        arrayAdapter_district = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, districts);
        spn_district.setAdapter(arrayAdapter_district);


        spn_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String distvalue = spn_district.getSelectedItem().toString().trim();
                TempData.setDistrict(distvalue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] tehsils = {"Select Tehsil","Tehsil Quetta City","Tehsil Quetta Saddar","Sub-Tehsil Panjpai"};

        arrayAdapter_tehsil = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, tehsils);
        spn_tehsil.setAdapter(arrayAdapter_tehsil);

        spn_tehsil.setEnabled(false);

        spn_tehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String tehsilvalue = spn_tehsil.getSelectedItem().toString().trim();
                TempData.setTehsil(tehsilvalue);
                tehsilpos = i;
                if (i==0){
                    spn_mc.setEnabled(false);
                    spn_mc.setSelection(0);
                }else {
                    if (i>0){
                        spn_mc.setEnabled(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] mc = {"Select MC","Metropolitan Corporation Quetta"};

        arrayAdapter_mc = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, mc);
        spn_mc.setAdapter(arrayAdapter_mc);

        spn_mc.setEnabled(false);

        spn_mc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mcpos = i;
                if (i==0){
                    spn_town_uc.setEnabled(false);
                    spn_town_uc.setSelection(0);
                }else {
                    if (i>0){
                        spn_town_uc.setEnabled(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] townuc = {"Select Town/UC","Kechi Baig","Shadenzai","Sara Ghurgai"};

        arrayAdapter_town_uc = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, townuc);
        spn_town_uc.setAdapter(arrayAdapter_town_uc);

        spn_town_uc.setEnabled(false);

        spn_town_uc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                townucpos = i;
                if (i==0){
                    spn_landuse.setEnabled(false);
                    spn_landuse.setSelection(0);
                }else {
                    if (i>0){
                        spn_landuse.setEnabled(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void findViews() {

        spn_landuse = findViewById(R.id.spn_landuse);
        spn_usage = findViewById(R.id.spn_usage);
        spn_htofbuilding = findViewById(R.id.spn_htofbuilding);
        spn_condofbuilding = findViewById(R.id.spn_condofbuilding);
        spn_typeofbuilding = findViewById(R.id.spn_typeofbuilding);
        spn_sub_parcel = findViewById(R.id.spn_sub_parcel);
        spn_district = findViewById(R.id.spn_district);
        spn_tehsil = findViewById(R.id.spn_tehsil);
        spn_mc = findViewById(R.id.spn_mc);
        spn_town_uc = findViewById(R.id.spn_town_uc);
//        spn_feature = findViewById(R.id.spn_feature);
//        spn_scale = findViewById(R.id.spn_scale);

        et_observed_by = findViewById(R.id.et_observed_by);
        et_remarks = findViewById(R.id.et_remarks);

        img_property = findViewById(R.id.img_property);
        img_property1 = findViewById(R.id.img_property1);
//        img_property2 = findViewById(R.id.img_property2);

//        search_parcel = findViewById(R.id.search_parcel);
        send_save = findViewById(R.id.send_save);

        parent_layout = findViewById(R.id.parent_layout);

        tv_usage = findViewById(R.id.tv_usage);
        tv_landuse = findViewById(R.id.tv_landuse);
        tv_sub_parcel = findViewById(R.id.tv_sub_parcel);

        tv_parcel_id = findViewById(R.id.tv_parcel_id);

        spnusagelayout = findViewById(R.id.spnusagelayout);

        landuselayout = findViewById(R.id.landuselayout);

        subparcellayout = findViewById(R.id.subparcellayout);

        perceivedDensity = findViewById(R.id.perceivedDensity);
        buildingLine = findViewById(R.id.buildingLine);
        streetFurniture = findViewById(R.id.streetFurniture);
        greenSpaces = findViewById(R.id.greenSpaces);

        rad1 = findViewById(R.id.rad1);
        rad2 = findViewById(R.id.rad2);
        rad3 = findViewById(R.id.rad3);
        rad4 = findViewById(R.id.rad4);
        rad5 = findViewById(R.id.rad5);

        radyes = findViewById(R.id.radyes);
        radno = findViewById(R.id.radno);

        rbhigh = findViewById(R.id.rbhigh);
        rbmedium = findViewById(R.id.rbmedium);
        rblow = findViewById(R.id.rblow);
        rblinear = findViewById(R.id.rblinear);
        rbsomeinstaces = findViewById(R.id.rbsomeinstaces);
        rbencroachment = findViewById(R.id.rbencroachment);
        rbavailable = findViewById(R.id.rbavailable);
        rbsomeavailable = findViewById(R.id.rbsomeavailable);
        rbnonexistent = findViewById(R.id.rbnonexistent);
        rbfrequent = findViewById(R.id.rbfrequent);
        rbnone = findViewById(R.id.rbnone);

    }

    private void CameraProperty() {
        img_property.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                camera1 = 0;
//                cameraOn("image1", true);
                checkLocationAndOpenCamera("image1", true);
            }
        });

        img_property1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera1 = 1;
//                cameraOn("image1", true);
                checkLocationAndOpenCamera("image1", true);
            }
        });

//        img_property2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                camera1 = 2;
////                cameraOn("image1", true);
//                checkLocationAndOpenCamera("image1", true);
//            }
//        });

    }

    private void checkLocationAndOpenCamera(String prefix, boolean locationMandatory) {
        try {


            if (locationMandatory) {
                if (checkgps()) {

                    if (android.os.Build.VERSION.SDK_INT >= 23) { //
                        helper.runMyProgressDialog1("*Getting GPS Coordinates...", context);
//                        Toast.makeText(context,"Working in Above 23",Toast.LENGTH_LONG).show();
//                        System.out.println("");
                        getCurrentLocation(20, "image", prefix, locationMandatory);

                    } else {
//                        Toast.makeText(context,"Working in Below 23",Toast.LENGTH_LONG).show();

                        if (helper.isMockSettingsON(context) && helper.areThereMockPermissionApps(context)) {
                            helper.dialogFake(context, "Please disable mock/fake location.", "Alert!");

                        } else {

                            helper.runMyProgressDialog1("Getting GPS Coordinates...", context);
                            getCurrentLocation(20, "image", prefix, locationMandatory);

                        } // mock
                    }
                }
            } else {
                cameraOn(prefix, locationMandatory);
            }
        } catch (Exception e) {
            e.printStackTrace();
            helper.dialog(context, e.getMessage().toString(), "Alert");
        }
    }

    public void getCurrentLocation(final int acc, final String event, final String prefix,
                                   final boolean locationMandatory) {

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ls = new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override

            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
                timeNow = location.getTime();
                provider = location.getProvider();

                int MIN_ACCURACY = acc; // 20 in metters
                int val = Math.round(location.getAccuracy());
//                Toast.makeText(context,"val"+val,Toast.LENGTH_LONG).show();

//                float acc = location.getAccuracy();

                if (val < MIN_ACCURACY) {
                    TempData.setlatitude(Double.toString(location.getLatitude()));
                    TempData.setlongitude(Double.toString(location.getLongitude()));

                    if (Build.VERSION.SDK_INT >= 23) {
                        if (null != location && !location.isFromMockProvider()) {

                            String strPMLong1 = TempData.getlongitude().toString();
                            String strPMLat1 = TempData.getlatitude().toString();

                            if ((strPMLong1.equalsIgnoreCase("") || strPMLong1 == null || strPMLong1.equalsIgnoreCase("0")) ||
                                    (strPMLat1.equalsIgnoreCase("") || strPMLat1 == null || strPMLat1.equalsIgnoreCase("0")) || !helper.checkgps()) {
                                helper.stopMyProgressDialog1();
                                helper.dialog(context, "Sorry, Your Location Not Found.", "Alert!");
                            } else {
                                if (event.equalsIgnoreCase("image")) {
                                    helper.stopMyProgressDialog1();
                                    lm.removeUpdates(ls);
                                    cameraOn(prefix, locationMandatory);
                                }
                            }

                        } else {
                            helper.stopMyProgressDialog1();
                            helper.dialogFake(context, "Please disable fake/mock location.", "Alert!");
                        }
                    } else {

                        String strPMLong1 = TempData.getlongitude().toString();
                        String strPMLat1 = TempData.getlatitude().toString();

                        if ((strPMLong1.equalsIgnoreCase("") || strPMLong1 == null || strPMLong1.equalsIgnoreCase("0")) ||
                                (strPMLat1.equalsIgnoreCase("") || strPMLat1 == null || strPMLat1.equalsIgnoreCase("0")) || !helper.checkgps()) {
                            helper.stopMyProgressDialog1();
                            helper.dialog(context, "Sorry, Your Location Not Found.", "Alert!");
                        } else {
                            if (event.equalsIgnoreCase("image")) {
                                helper.stopMyProgressDialog1();
                                lm.removeUpdates(ls);
                                cameraOn(prefix, locationMandatory);
                            }
                        }


                    }
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, ls);
    }


    public boolean checkgps() {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            helper.buildAlertMessageNoGps(context);
            return false;
        }
        return true;
    }


    private void cameraOn(String prefix, boolean locationMandatory) {
        imageName = getPictureName(prefix, locationMandatory);


//        String strFolder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator  + context.getString(R.string.images_folder);
        String strFolder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator  + context.getString(R.string.images_folder) + "/";

//        String strFolder = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.images_folder) + "/";
        _path = strFolder + imageName;
        File folder = new File(strFolder);
        if (!folder.exists()) {
            if (folder.mkdir()) {
                File file = new File(_path);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri outputFileUri;
                if (Build.VERSION.SDK_INT >= 24) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    outputFileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                } else {
                    outputFileUri = Uri.fromFile(file);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(intent, 0);
            } else {
                helper.dialog(context, "Cannot create directory.", "Alert!");
            }
        } else {
            File file = new File(_path);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri outputFileUri;
            if (Build.VERSION.SDK_INT >= 24) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                outputFileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            } else {
                outputFileUri = Uri.fromFile(file);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, 0);
        }
    }

    private String getPictureName(String prefix, boolean locationMandatory) {
        String uniqueID = (UUID.randomUUID().toString()).substring(0, 4);
        String lat = TempData.getlatitude();
        String lng = TempData.getlongitude();
        lat = lat.replace(".", "-");
        lng = lng.replace(".", "-");
        String strDate;
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        if (null != mLocation) {
            if (locationMandatory)
                strDate = dateFormat.format(new Date(mLocation.getTime())).toString() + "_"  + uniqueID + ".jpg";
            else
                strDate = dateFormat.format(new Date(mLocation.getTime())).toString() + "_" + uniqueID + ".jpg";
        } else {
            strDate = dateFormat.format(new Date()).toString() + "_" + uniqueID + ".jpg";
        }
        return TempData.getImei() + "_" + prefix + "_" + strDate;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("touch", "resultCode: " + resultCode);
        Log.d("touch", "resultOk: " + RESULT_OK);
        switch (resultCode) {
            case 0:
                if (camera1 == 0) { // Property
                    img_property.setImageResource(R.drawable.camera_100);
                    imageok = false;
                    TempData.setImagePath("");
                    TempData.setImageFileName("");
                    TempData.setImageName("");
                }
                if (camera1 == 1) { // PU
                    img_property1.setImageResource(R.drawable.camera_100);
                    imageok1 = false;
                    TempData.setImagePath1("");
                    TempData.setImageFileName1("");
                    TempData.setImageName1("");
                }
//                if (camera1 == 2) { // PU
//                img_property2.setImageResource(R.drawable.camera_100);
//                imageok1 = false;
//                TempData.setImagePath2("");
//                TempData.setImageFileName2("");
//                TempData.setImageName2("");
//            }
                break;

            case -1:
                if (camera1 == 0) { // Property
                    onPhotoTaken();
                    if (resizeImage2()) {
                        if (imageok = true){
                            send_save.setEnabled(true);
                            send_save.setBackgroundResource(R.color.design_default_color_secondary_variant);
                        }else {
                            send_save.setEnabled(false);
                            send_save.setBackgroundResource(white);
                        }
                    }
                }
                if (camera1 == 1) {  // PU
                    onPhotoTaken();
                    if (resizeImage2()) {
                        imageok1 = true;
                    }
                }
//                if (camera1 == 2) {  // property other
//                    onPhotoTaken();
//                    if (resizeImage2()) {
//                        imageok1 = true;
//                    }
//                }

                break;
        }
    }

    public boolean resizeImage2()  { // Property
        Boolean abc = false;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 6;

        String path1 = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator  + context.getString(R.string.images_folder) + "/" + imageName;
        String folder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator  + context.getString(R.string.images_folder)+ "";


//        String path1 = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.images_folder) + "/" + imageName;
//        String folder = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.images_folder) + "";
        File f = new File(folder);
        if (!f.exists()) {
            f.mkdir();
        }
        File imgFile = new File(path1);
        if (imgFile.exists()) {
            bitmapOrg = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
        } else {
            Toast.makeText(context, "No Image Is Present", Toast.LENGTH_SHORT).show();
        }
        try {
            StringTokenizer tokens = new StringTokenizer(imageName, ".");
            String first = tokens.nextToken();// this will contain name of picture without extension(.jepg)
            File ff = new File(path1);
            File resizedImage = new Resizer(this)
                    .setTargetLength(1440)
                    .setQuality(80)
                    .setOutputFormat("JPEG")
                    .setOutputFilename(first)
                    .setOutputDirPath(folder)
                    .setSourceImage(ff)
                    .getResizedFile();

            if(camera1 == 0)
                TempData.setImagePath(path1);
            else if(camera1 == 1)
                TempData.setImagePath1(path1);
            else if(camera1 == 2)
                TempData.setImagePath2(path1);
            abc = true;

        } catch (Exception e) {
            Toast.makeText(context, "Error is" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return abc;
    }

    protected void onPhotoTaken() { // Property
        try {
            //TempData.setImageName(imageName);
            Log.i("MakeMachine", "onPhotoTaken");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 6;

            if (camera1 == 0) {
                Bitmap bitmap = BitmapFactory.decodeFile(_path, options);
                Bitmap b1 = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
                Bitmap bitmapRotated = rotateImage(b1, _path);
                img_property.setImageBitmap(bitmapRotated);
                TempData.setImagePath(_path);
                TempData.setImageFileName(imageName);
                TempData.setImage(bitmap);
            } else if (camera1 == 1) {
                Bitmap bitmap = BitmapFactory.decodeFile(_path, options);
                Bitmap b1 = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
                Bitmap bitmapRotated = rotateImage(b1, _path);

                img_property1.setImageBitmap(bitmapRotated);

                TempData.setImagePath1(_path);
                TempData.setImageFileName1(imageName);
                TempData.setImage1(bitmap);
            }
//            else if (camera1 == 2) {
//                Bitmap bitmap = BitmapFactory.decodeFile(_path, options);
//                Bitmap b1 = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
//                Bitmap bitmapRotated = rotateImage(b1, _path);
//
//                img_property2.setImageBitmap(bitmapRotated);
//
//                TempData.setImagePath2(_path);
//                TempData.setImageFileName2(imageName);
//                TempData.setImage2(bitmap);
//
//            }

            setResult(RESULT_OK);
        } catch (Exception e) {
            Toast.makeText(context, "Error is" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap rotateImage(Bitmap bitmap, String path) throws IOException {
        int rotate = 0;
        ExifInterface exif;
        exif = new ExifInterface(path);

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void functionSendDataFinal() {

        send_save.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            public void onClick(View view) {
                try {

                    try {
                        getdata();
                    }catch (Exception e){
                        extraValue = false;
                    }

//                   check data if form is filled(conditions goes here)

                    if (tehsilpos == 0){
                        ob.dialog(context, "Please Select Tehsil", "Alert!");
                        TextView errorText = (TextView)spn_tehsil.getSelectedView();
//                        errorText.setError("");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        spn_tehsil.setBackgroundResource(R.drawable.error_background);
                        landuselayout.scrollTo(0, (int) spn_tehsil.getY());
                    }else {
                        if (tehsilpos>0){
                            spn_tehsil.setBackgroundResource(R.drawable.spinner_bg);
                        }
                        if (mcpos==0){
                            ob.dialog(context, "Please Select MC", "Alert!");
                            TextView errorText = (TextView)spn_mc.getSelectedView();
//                        errorText.setError("");
                            errorText.setTextColor(Color.RED);//just to highlight that this is an error
                            spn_mc.setBackgroundResource(R.drawable.error_background);
                            landuselayout.scrollTo(0, (int) spn_mc.getY());
                        }
                        else {
                            if (mcpos>0){
                                spn_mc.setBackgroundResource(R.drawable.spinner_bg);
                            }
                            if (townucpos==0){
                                ob.dialog(context, "Please Select Town/UC", "Alert!");
                                TextView errorText = (TextView)spn_town_uc.getSelectedView();
//                        errorText.setError("");
                                errorText.setTextColor(Color.RED);//just to highlight that this is an error
                                spn_town_uc.setBackgroundResource(R.drawable.error_background);
                                landuselayout.scrollTo(0, (int) spn_town_uc.getY());
                            }
                            else {
                                if (townucpos>0){
                                    spn_town_uc.setBackgroundResource(R.drawable.spinner_bg);
                                }


                                if (position == 0){

                                    ob.dialog(context, "Please Select Landuse type", "Alert!");
                                    TextView errorText = (TextView)spn_landuse.getSelectedView();
//                        errorText.setError("");
                                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                                    spn_landuse.setBackgroundResource(R.drawable.error_background);
                                    landuselayout.scrollTo(0, (int) spn_landuse.getY());

                                }
                                else {
                                    if (position > 0){
                                        spn_landuse.setBackgroundResource(R.drawable.spinner_bg);
                                    }
                                    if (usagepos == 0){
                                        TextView errorText = (TextView)spn_usage.getSelectedView();
//                          errorText.setError("");
                                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                                        spn_usage.setBackgroundResource(R.drawable.error_background);
                                        landuselayout.scrollTo(0, (int) spn_usage.getY());
                                        ob.dialog(context, "Please Select Usage", "Alert!");
                                    }else {
                                        if (usagepos > 0){
                                            spn_usage.setBackgroundResource(R.drawable.spinner_bg);
                                        }
                                        if (hbuildingpos == 0){
                                            TextView errorText = (TextView)spn_htofbuilding.getSelectedView();
//                              errorText.setError("");
                                            errorText.setTextColor(Color.RED);//just to highlight that this is an error
                                            spn_htofbuilding.setBackgroundResource(R.drawable.error_background);
                                            landuselayout.scrollTo(0, (int) spn_htofbuilding.getY());
                                            ob.dialog(context, "Please Select Height of Building", "Alert!");
                                        }else {
                                            if (hbuildingpos > 0){
                                                spn_htofbuilding.setBackgroundResource(R.drawable.spinner_bg);
                                            }
                                            if (cbuildingpos == 0){
                                                TextView errorText = (TextView)spn_condofbuilding.getSelectedView();
//                                  errorText.setError("");
                                                errorText.setTextColor(Color.RED);//just to highlight that this is an error
                                                spn_condofbuilding.setBackgroundResource(R.drawable.error_background);
                                                landuselayout.scrollTo(0, (int) spn_condofbuilding.getY());
                                                ob.dialog(context, "Please Select Condition of Building", "Alert!");
                                            }else {
                                                if (cbuildingpos > 0){
                                                    spn_condofbuilding.setBackgroundResource(R.drawable.spinner_bg);
                                                }
                                                if (tbuildingpos == 0){
                                                    TextView errorText = (TextView)spn_typeofbuilding.getSelectedView();
//                                      errorText.setError("");
                                                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                                                    spn_typeofbuilding.setBackgroundResource(R.drawable.error_background);
                                                    landuselayout.scrollTo(0, (int) spn_typeofbuilding.getY());
                                                    ob.dialog(context, "Please Select Type of Building", "Alert!");
                                                }else {
                                                    if (tbuildingpos > 0){
                                                        spn_typeofbuilding.setBackgroundResource(R.drawable.spinner_bg);
                                                    }
//                                        Next condition code starts from here
                                                    if (rad1.getCheckedRadioButtonId()==-1){
                                                        ob.dialog(context, "Choose Perceived Density", "Alert!");
                                                        perceivedDensity.setTextColor(Color.RED);
                                                        landuselayout.scrollTo(0, (int) perceivedDensity.getY());
                                                    }else {

                                                        perceivedDensity.setTextColor(black);

                                                        if (rad2.getCheckedRadioButtonId()==-1){
                                                            ob.dialog(context, "Choose Building Line", "Alert!");
                                                            buildingLine.setTextColor(Color.RED);
                                                            landuselayout.scrollTo(0, (int) buildingLine.getY());
                                                        }else {

                                                            buildingLine.setTextColor(black);

                                                            if (rad3.getCheckedRadioButtonId()==-1){
                                                                ob.dialog(context, "Choose Street Furniture", "Alert!");
                                                                streetFurniture.setTextColor(Color.RED);
                                                                landuselayout.scrollTo(0, (int) streetFurniture.getY());
                                                            }else {

                                                                streetFurniture.setTextColor(black);

                                                                if (rad4.getCheckedRadioButtonId()==-1){
                                                                    ob.dialog(context, "Choose Green Spaces", "Alert!");
                                                                    greenSpaces.setTextColor(Color.RED);
                                                                    landuselayout.scrollTo(0, (int) greenSpaces.getY());
                                                                }else {
                                                                    greenSpaces.setTextColor(black);

                                                                    if(et_observed_by.length()==0){
                                                                        ob.dialog(context, "Please Enter Observed by", "Alert!");
                                                                        et_observed_by.requestFocus();
                                                                    }else{
                                                                        if (et_remarks.length()==0){
                                                                            ob.dialog(context, "Please Enter Remarks", "Alert!");
                                                                            et_remarks.requestFocus();
                                                                        }
                                                                        else {
                                                                            if (imageok) {
                                                                                sendingDataFunction();
                                                                            } else {
                                                                                ob.dialog(context, "Please Take Atleast 1 Picture.", "Alert!");
                                                                                img_property.requestFocus();
                                                                            }
                                                                        }
                                                                    }

                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }


                                }



                            }
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error is" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    //    ob.dialog(context, "Please Download The Data of these Districts \n 1. Sargodha(circle4)\n 2. Sahiwal(BlockVIA)\n 3. Narowal(BadoMalhi) \n And Fill Again.  ", "Alert");

                }
            }
        });
    }

    private void sendingDataFunction() {
        try {
            try {
                sendData();
                if (empty) {
                    clearAllfields();
                }
            } catch (HttpHostConnectException e) {
                e.printStackTrace();

                clearImagesFunction();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error is" + e.getMessage(), Toast.LENGTH_SHORT).show();
            clearImagesFunction();
        }
    }

    public void sendData() throws HttpHostConnectException {
        try {
            TempData.setCompleteAppDataJsonString(json.toString().replace("\\/","/"));
            Log.d("touch", json.toString());


            if (checkInternetConnection()) {
                if (!(json.toString().length() == 0)) {

                    long id = Querries.insertIntoLocalDB_Survey_Data(context);

                    TempData.setLocalId(String.valueOf(id));
                    if (id > 0) {

                        DataSender ob = new DataSender(context, "SurveyForm", Constants.URL_SURVEY_DATA, Constants.TABLE_SURVEY_DATA, json.toString().replace("\\/","/"));
                        ob.androidToServerERS();
                        empty = true;
                        clearImagesFunction();
                        Toast.makeText(LandUseForm.this, "Survey Form data is saved successfully", Toast.LENGTH_LONG).show();

                        //finish();

                    } else {
                        empty = false;
                        ob.dialog(context, "Your Data Cannot be saved, DataBase Connection Problem.", "Alert!");
                    }
                } else {
                    empty = false;
                    ob.dialog(context, "Your Data Cannot be sent, Data is not saved.", "Alert!");
                }
            }
            else {
                if (!(json.toString().length() == 0)) {


                    long id = Querries.insertIntoLocalDB_Survey_Data(context);
                    TempData.setLocalId(String.valueOf(id));
                    if (id > 0) {

                        ob.dialog(context, "Data Saved Successfully in Local Storage\n"+"id :" +id, "No Internet!");

                    } else {
                        ob.dialog(context, "Your Data Cannot be saved, DataBase Connection Problem.", "Alert!");
                    }
                    empty = true;
                    clearImagesFunction();
                } else {
                    ob.dialog(context, "Your Data Cannot be saved, DataBase Connection Problem.", "Alert!");
                }
            }
        } catch (Exception e) {
            TempData.setCompleteAppDataJsonString(json.toString().replace("\\/","/"));
            clearImagesFunction();
            //Toast.makeText(context, "Error is" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void clearAllfields() {
        et_observed_by.setText("");
        et_remarks.setText("");

        rad1.clearCheck();
        rad2.clearCheck();
        rad3.clearCheck();
        rad4.clearCheck();
        rad5.clearCheck();

        spn_landuse.setSelection(0);
        spn_tehsil.setSelection(0);
        spn_tehsil.setEnabled(false);
        spn_usage.setSelection(0);
        spn_condofbuilding.setSelection(0);
        spn_htofbuilding.setSelection(0);
        spn_typeofbuilding.setSelection(0);

    }

    private void clearImagesFunction() {
        imageok = false;
        img_property.setImageResource(R.drawable.camera_100);
        TempData.setImageFileName("");
        TempData.setImagePath("");
        TempData.setImageName("");

        imageok1 = false;
        img_property1.setImageResource(R.drawable.camera_100);
        TempData.setImageFileName1("");
        TempData.setImagePath1("");
        TempData.setImageName1("");

//        img_property2.setImageResource(R.drawable.camera_100);
//        TempData.setImageFileName2("");
//        TempData.setImagePath2("");
//        TempData.setImageName2("");

    }

    public boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // ARE WE CONNECTED TO THE NET
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void checkusageposition(){
        if (usagepos==0){
            spn_htofbuilding.setEnabled(false);
            spn_htofbuilding.setSelection(0);
        }else {
            if (usagepos>0){
                spn_htofbuilding.setEnabled(true);
            }
        }
    }


}