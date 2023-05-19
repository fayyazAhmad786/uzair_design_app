package com.uzair.landusesurvey;


import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class ParcelData implements Serializable, Parcelable
{

    private Integer uu_id;
   private String zone;
   private String district;


   String imei;

    private Integer key;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public final static Creator<ParcelData> CREATOR = new Creator<ParcelData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ParcelData createFromParcel(android.os.Parcel in) {
            return new ParcelData(in);
        }

        public ParcelData[] newArray(int size) {
            return (new ParcelData[size]);
        }

    }
            ;
    private final static long serialVersionUID = 2353298322527872938L;

    protected ParcelData(android.os.Parcel in) {
        this.uu_id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.zone = ((String) in.readValue((String.class.getClassLoader())));
        this.district = ((String) in.readValue((String.class.getClassLoader())));
        this.key = ((Integer) in.readValue((String.class.getClassLoader())));
        this.additionalProperties = ((Map<String, Object> ) in.readValue((Map.class.getClassLoader())));
    }

    public ParcelData() {
    }
    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String setImei(String imei) {
        this.imei = imei;
        return imei;
    }

    public String getImei() {
        return imei;
    }

    public Integer getUu_id() {
        return uu_id;
    }

    public void setUu_id(Integer uu_id) {
        this.uu_id = uu_id;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(uu_id);
        dest.writeValue(zone);
        dest.writeValue(district);
        dest.writeValue(key);
        dest.writeValue(additionalProperties);
    }

    public int describeContents() {
        return 0;
    }

}
