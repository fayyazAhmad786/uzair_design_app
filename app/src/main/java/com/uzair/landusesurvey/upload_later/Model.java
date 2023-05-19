package com.uzair.landusesurvey.upload_later;

import java.util.ArrayList;


public class Model {

    public static ArrayList<Item> Items;

//    public static void LoadModel(String[] imagePath, int[] localIDs, String[] circleIDS, String[] srNumIDS, String[] puNumIDS, String[] localityIDS
//            , String[] wardIDS, String[] blockIDS, String[] DateTimeIDS, String[] parcelsIDS) {
//        Items = new ArrayList<Item>();
//        try {
//            for (int i = 1; i < localIDs.length; i++) {
////                Items.add(new Item(i, imagePath[i]));
//                Items.add(new Item(i, imagePath[i], localIDs[i], circleIDS[i],srNumIDS[i], puNumIDS[i], localityIDS[i], wardIDS[i],blockIDS[i], DateTimeIDS[i],parcelsIDS[i]));
//            }
//        } catch (Exception e) {
//            @SuppressWarnings("unused")
//            String msg = e.getMessage();
//        }
//    }

    public static void LoadModel(String[] imagePath, int[] localIDs, String[] districtName, String[] tehsilName, String[] imagePath1, String[] imagePath2) {
        Items = new ArrayList<Item>();
        try {
            for (int i = 1; i < localIDs.length; i++) {
//                Items.add(new Item(i, imagePath[i]));
                //Items.add(new Item(i, imagePath[i], localIDs[i], circleIDS[i],srNumIDS[i], puNumIDS[i], localityIDS[i], wardIDS[i],blockIDS[i], DateTimeIDS[i],parcelsIDS[i]));
                Items.add(new Item(i, imagePath[i], localIDs[i], districtName[i],tehsilName[i],
                        imagePath1[i], imagePath2[i], imagePath2[i]));
            }
        } catch (Exception e) {
            @SuppressWarnings("unused")
            String msg = e.getMessage();
        }
    }

    public static Item GetbyId(int id) {

        for (Item item : Items) {
            if (item.Id == id) {
                return item;
            }
        }
        return null;
    }

}
