package com.example.traveloholic;

import android.provider.Contacts;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    public String placeName,vicinity,latitude,longitude,reference;
    Photo[] mphotos={};

    private HashMap<String,String> getPlace(JSONObject googlePlaceJson)
    {
        HashMap<String ,String> googlePlaceMap=new HashMap<>();
        placeName="--NA--";
        vicinity="--NA--";
        latitude="";
        longitude="";
        reference="";

        Log.d("DataParser","jsonobject ="+googlePlaceJson.toString());

        try{
            if (!googlePlaceJson.isNull("name")){
                placeName=googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")){
                vicinity=googlePlaceJson.getString("vicinity");
            }
            if(!googlePlaceJson.isNull("photos")){
                JSONArray photos = googlePlaceJson.getJSONArray("photos");
                mphotos = new Photo[photos.length()];
                for(int i=0;i<photos.length();i++){
                    mphotos[i] = new Photo();
                    mphotos[i].mWidth = ((JSONObject)photos.get(i)).getInt("width");
                    mphotos[i].mHeight = ((JSONObject)photos.get(i)).getInt("height");
                    mphotos[i].mPhotoReference = ((JSONObject)photos.get(i)).getString("photo_reference");
                    JSONArray attributions = ((JSONObject)photos.get(i)).getJSONArray("html_attributions");
                    mphotos[i].mAttributions = new Attribution[attributions.length()];
                    for(int j=0;j<attributions.length();j++){
                        mphotos[i].mAttributions[j] = new Attribution();
                        mphotos[i].mAttributions[j].mHtmlAttribution = attributions.getString(j);
                    }
                }
            }

            latitude=googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude=googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference=googlePlaceJson.getString("reference");

            googlePlaceMap.put("place_name",placeName);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("lat",latitude);
            googlePlaceMap.put("lng",longitude);
            googlePlaceMap.put("reference",reference);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return googlePlaceMap;
    }

    private List<HashMap<String,String >>getPlaces(JSONArray jsonArray)
    {
        int count=jsonArray.length();
        List<HashMap<String ,String >> placelist=new ArrayList<>();
        HashMap<String,String> placeMap=null;

        for(int i=0;i<count;i++)
        {
            try{
                placeMap=getPlace((JSONObject)jsonArray.get(i));
                placelist.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placelist;

    }

    public List<HashMap<String ,String>> parse(String jsonData)
    {
        JSONArray jsonArray=null;
        JSONObject jsonObject;

        Log.d("json data",jsonData);

        try{
            jsonObject=new JSONObject(jsonData);
            jsonArray=jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }
}
