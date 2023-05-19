package com.uzair.landusesurvey.upload_later;

public class Item {

    public int Id;
    public String iconFile;
    public int localIDs;
    public String districtName;
    public String tehsilName;
    public String puNumIDS;
    public String localityIDS;
    public String wardIDS;
    public String blockIDS;
    public String DateTimeIDS;
    public String parcelsIDS;
    public String image1;
    public String image2;
    public String destru;
    public String image3;
    public String image4;
    public String image5;
    public String image6;


//    public Item(int id, String iconFile, int localIDs, String circleIDS, String srNumIDS, String puNumIDS, String localityIDS, String wardIDS, String blockIDS, String DateTimeIDS, String parcelsIDS) {
//        Id = id;
//        this.iconFile = iconFile;
//        this.localIDs = localIDs;
//        this.circleIDS = circleIDS;
//        this.srNumIDS = srNumIDS;
//        this.puNumIDS = puNumIDS;
//        this.localityIDS = localityIDS;
//        this.wardIDS = wardIDS;
//        this.blockIDS = blockIDS;
//        this.DateTimeIDS = DateTimeIDS;
//        this.parcelsIDS = parcelsIDS;
//    }

    public Item(int id, String iconFile, int localIDs, String districtName, String tehsilName, String image1, String image2, String s) {
        Id = id;
        this.iconFile = iconFile;
        this.localIDs = localIDs;
        this.districtName = districtName;
        this.tehsilName = tehsilName;
        this.image1 = image1;
        this.image2 = image2;
//        this.destru = destruction;

    }

}
