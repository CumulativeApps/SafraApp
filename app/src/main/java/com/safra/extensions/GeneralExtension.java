package com.safra.extensions;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class GeneralExtension {

    /**
     * Converts String to Array of long
     *
     * @param s          - string  that want to convert
     * @param splitRegex - string regex to split string
     * @return Array of long
     * or empty array of long if string is invalid to convert
     */
    public static Long[] toLongArray(String s, String splitRegex) {
        if (s != null) {
            if (Pattern.matches("^\\[(\\d+(,\\s)?)*\\]$", s)) {
                s = s.substring(1, s.length() - 1);

                if (s.length() > 0) {
                    String[] s1 = s.split(splitRegex);
                    Long[] longs = new Long[s1.length];
                    if (s1.length > 0) {
                        for (int i = 0; i < s1.length; i++) {
                            longs[i] = Long.parseLong(s1[i]);
                        }
                    }
                    return longs;
                }
            } else if (Pattern.matches("^(\\d+(,\\s)?)*$", s)) {
//                Log.e("GeneralExtension", "toLongArray: " + s);
//            s = s.substring(1, s.length() - 1);
                if (s.length() > 0) {
                    String[] s1 = s.split(splitRegex);
                    Long[] longs = new Long[s1.length];
                    if (s1.length > 0) {
                        for (int i = 0; i < s1.length; i++) {
                            longs[i] = Long.parseLong(s1[i]);
                        }
                    }

//                    Log.e("GeneralExtension", "toLongArray: " + longs[0]);
                    return longs;
                }
            } else {
//                Log.e("GeneralExtension", "toLongArray withoutspace : " + s);
//            s = s.substring(1, s.length() - 1);
                if (s.length() > 0) {
                    String[] s1 = s.split(splitRegex);
                    Long[] longs = new Long[s1.length];
                    if (s1.length > 0) {
                        for (int i = 0; i < s1.length; i++) {
                            longs[i] = Long.parseLong(s1[i]);
//                            Log.e("GeneralExtension", "toLongArray: " + longs[i]);
                        }
                    }
                    return longs;
                }
            }
        }

        return new Long[0];
    }

    /**
     * Converts String to List of long
     *
     * @param s          string that want to convert
     * @param splitRegex string regex to split string
     * @return List of Long
     * or empty array of long if string is invalid to convert
     */
    public static List<Long> toLongList(String s, String splitRegex) {
        if (s != null) {
            if (Pattern.matches("^\\[(\\d+(,\\s)?)*\\]$", s)) {
                s = s.substring(1, s.length() - 1);

                if (s.length() > 0) {
                    String[] s1 = s.split(splitRegex);
                    List<Long> longs = new ArrayList<>();
                    if (s1.length > 0) {
                        for (String value : s1) {
                            longs.add(Long.parseLong(value));
                        }
                    }
                    return longs;
                }
            } else if (Pattern.matches("^(\\d+(,\\s)?)*$", s)) {
//                Log.e("GeneralExtension", "toLongArray: " + s);
//            s = s.substring(1, s.length() - 1);
                if (s.length() > 0) {
                    String[] s1 = s.split(splitRegex);
                    List<Long> longs = new ArrayList<>();
                    if (s1.length > 0) {
                        for (String value : s1) {
                            longs.add(Long.parseLong(value));
                        }
                    }
//                    Log.e("GeneralExtension", "toLongArray: " + longs.get(0));
                    return longs;
                }
            } else if (Pattern.matches("^(\\d+(,)?)*$", s)) {
//                Log.e("GeneralExtension", "toLongArray: " + s);
//            s = s.substring(1, s.length() - 1);
                if (s.length() > 0) {
                    String[] s1 = s.split(splitRegex);
                    List<Long> longs = new ArrayList<>();
                    if (s1.length > 0) {
                        for (String value : s1) {
                            longs.add(Long.parseLong(value));
                        }
                    }
//                    Log.e("GeneralExtension", "toLongArray: " + longs.get(0));
                    return longs;
                }
            }
        }

        return new ArrayList<>();
    }

    /**
     * Convert Array of long to String
     *
     * @param longs Array of long
     * @return String as list of long array item with comma separate
     */
    public static String toString(Long[] longs) {
        if (longs != null) {
            StringBuilder sb = new StringBuilder();
            for (Long l : longs) {
                if (sb.toString().isEmpty())
                    sb.append(l);
                else
                    sb.append(",").append(l);
            }
            return sb.toString();
        }
        return null;
    }

    public static List<Long> toLongList(Long[] longs){
        List<Long> longList = new ArrayList<>();
        if(longs != null && longs.length > 0)
        for(int i=0; i<longs.length; i++){
            longList.add(i, longs[i]);
        }
        return longList;
    }

    /**
     * Converts List of long to String
     *
     * @param longs - List of long
     * @return - String as list of long array item with comma separate
     */
    public static String toString(List<Long> longs) {
        StringBuilder sb = new StringBuilder();
        for (Long l : longs) {
            if (sb.toString().isEmpty())
                sb.append(l);
            else
                sb.append(",").append(l);
        }
        return sb.toString();
    }

    public static Long[] toLongArray(List<Long> longs){
        Long[] longArray = new Long[longs.size()];
        for(int i=0; i<longs.size(); i++){
            longArray[i] = longs.get(i);
        }
        return longArray;
    }

    public static long[] toPrimitiveLongArray(List<Long> longs){
        long[] longArray= new long[longs.size()];
        for(int i=0; i<longs.size(); i++){
            longArray[i] = longs.get(i);
        }
        return longArray;
    }

    /**
     * Capitalize given string
     *
     * @param s string to capitalize
     * @return capitalized string
     */
    public static String capitalizeString(String s) {
        String fL = s.substring(0, 1);
        String rL = s.substring(1);

        fL = fL.toUpperCase();

        return fL + rL;
    }

    /**
     * Convert string representation of date to milliseconds
     *
     * @param s      - string of date
     * @param format - format of date
     * @return - milliseconds representation of given string
     * @throws ParseException - if format doesn't match string if date.
     */
    public static long convertStringToMilliSeconds(String s, String format) throws ParseException {
//        Log.e("GeneralExtension", "convertStringToMilliSeconds: " + s);
//        Log.e("GeneralExtension", "convertStringToMilliSeconds: " + format);
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return Objects.requireNonNull(sdf.parse(s)).getTime();
    }

    /**
     * Convert Long[] to long[]
     *
     * @param longs - Long[]
     * @return - long[]
     */
    public static long[] toPrimitiveLong(Long[] longs) {
        long[] primitives = new long[longs.length];
        int i = 0;
        for (Long t : longs) {
            primitives[i++] = t;
        }
        return primitives;
    }

    /**
     * Convert long[] to Long[]
     *
     * @param longs - long[]
     * @return - Long[]
     */
    public static Long[] fromPrimitiveLong(long[] longs) {
        Long[] nonPrimitive = new Long[longs.length];
        int i = 0;
        for (long t : longs) {
            nonPrimitive[i++] = t;
        }
        return nonPrimitive;
    }

    /**
     * Convert JSONArray to List of String
     *
     * @param jsonArray - provided JSONArray
     * @return - List<String>
     * @throws JSONException - if invalid JSONArray
     */
    public static List<String> convertJsonToList(JSONArray jsonArray) throws JSONException {
        List<String> list = new ArrayList<>();
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
        }
        return list;
    }

    public static HashMap<String, String> getKeyFromJSONObject(JSONObject jsonObject) throws JSONException {
        HashMap<String, String> hashMap = new HashMap<>();
        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String k = it.next();
            String v = jsonObject.getJSONObject(k).getString("name");
            hashMap.put(v, k);
        }
//        Log.e("TAG", "getKeyFromJSONObject: " + hashMap.size());
        return hashMap;
    }

    public static int findLevelFromClassName(String className){
        int level = 0;
        String[] classNameSplit = className.split(" ");
//        Log.e("GeneralExtension", "findLevelFromClassName: " + classNameSplit.length);
        for(String string: classNameSplit){
            if(string.contains("level_")){
//                Log.e("GeneralExtension", "findLevelFromClassName: " + string);
                String[] levelString = string.split("_");
                level = Integer.parseInt(levelString[1]);
//                Log.e("GeneralExtension", "findLevelFromClassName: " + level);
            }
        }
        return level;
    }
}
