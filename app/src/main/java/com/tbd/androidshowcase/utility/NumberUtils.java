package com.tbd.androidshowcase.utility;

/**
 * Created by Trevor on 9/18/2016.
 */
public class NumberUtils {
    public static boolean TryParseFloat(String value){
        try {
            Float.parseFloat(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
