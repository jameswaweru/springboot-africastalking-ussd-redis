package com.jameswaweru.springbootussd.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

public class Utilities {
    /**
     * To check if a value is empty or null
     * @param value Object being determined
     * @return boolean true|false
     */
    public static boolean isEmpty(Object value) {
        boolean isEmpty = false;
        if (value == null || value.toString().replaceAll("\\s", String.valueOf(value)).equals("")) {
            isEmpty = true;
        }
        return isEmpty;
    }


    public static boolean isNumeric(String string) {
        int intValue;

        System.out.println(String.format("Parsing string: \"%s\"", string));

        if(string == null || string.equals("")) {
            System.out.println("String cannot be parsed, it is null or empty.");
            return false;
        }

        try {
            intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Input String cannot be parsed to Integer.");
        }
        return false;
    }


    public static JSONObject getTheObjectWithNeededValue(JSONArray jsonArray ,
                                                         String key, Logger LOGGER) throws JSONException {
        JSONObject objectToReturn = new JSONObject();
        //objectToReturn.put(key, "no value");
        if(jsonArray.length() > 0){
            for (int ij = 0; ij < jsonArray.length(); ij++) {
                JSONObject details = null;
                try {
                    details = (JSONObject) jsonArray
                            .get(ij);
                    LOGGER.info("Checking if :"+details.toString()+" has "+key);
                    if (details.has(key)) {
                        objectToReturn = details;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return objectToReturn;
    }

    public static JSONArray checkObjectDuplicates(JSONArray jsonArray ,
                                                  String key, Logger LOGGER) throws JSONException {


        JSONArray finalArray = new JSONArray();
        if(jsonArray.length() > 0){

            for (int ij = 0; ij < jsonArray.length(); ij++) {
                JSONObject details = null;
                try {
                    details = (JSONObject) jsonArray
                            .get(ij);
                    LOGGER.info("Checking if :"+details.toString()+" has "+key);
                    if (!details.has(key)) {
                       finalArray.put(details);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

//
//
//
//
//
//
//
//
//
//
//        ArrayList<String> list = new ArrayList<String>();
//        int position = 0;
//        boolean isThereDuplicate = false;
//        int len = jsonArray.length();
//        if (jsonArray != null) {
//            for (int i=0;i<len;i++){
//
//                JSONObject details = (JSONObject) jsonArray
//                        .get(i);
//
//                LOGGER.info("Checking if :"+details.toString()+" has "+key);
//                if (details.has(key)) {
//                    isThereDuplicate = true;
//                    position = i;
//                }
//                list.add(jsonArray.get(i).toString());
//            }
//        }
//        if(isThereDuplicate){
//            //Remove the element from arraylist
//            list.remove(position);
//        }
//
//        //Recreate JSON Array
//        JSONArray jsArray = new JSONArray(list);
        return finalArray;
    }

}
