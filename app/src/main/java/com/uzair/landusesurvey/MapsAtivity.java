package com.uzair.landusesurvey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.ArrayList;

public class MapsAtivity extends AppCompatActivity {

    MapView mMapView;
    ArcGISMap arcGISMap;
    LocationDisplay mLocationDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_ativity);

        getSupportActionBar().hide();

        mMapView = findViewById(R.id.mapview);
        arcGISMap = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP,31.5617,74.3369,15);
//        arcGISMap = new ArcGISMap(BasemapStyle.ARCGIS_IMAGERY);
        mMapView.setMap(arcGISMap);

//        mLocationDisplay = mMapView.getLocationDisplay();


    }
}