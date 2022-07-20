package com.college.college_RestAPI.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Controller
public class CollegeController {

    @RequestMapping("/getCollegeDetailsBycountryNameAndSearchString")
    public @ResponseBody
    JsonObject getLocalityDetailsByZipCode(String  countryName,String name) throws IOException {

    	JsonArray jsonArray = new JsonArray();
    	jsonArray = getCollegeDetailsByParams(countryName,name);
        JsonObject finalJsonObject = new JsonObject();
        //String country = jsonObject.get("country").toString();
        //country = country.replaceAll("^\"|\"$", "");
        ArrayList collegeList = new ArrayList();
        ArrayList stateList = new ArrayList();
        ArrayList webPageList = new ArrayList();
        //ArrayList longitudeList = new ArrayList();
        //jsonPlacesArray = jsonObject.get("places").getAsJsonArray();
        Iterator<JsonElement> objectIterator =  jsonArray.iterator();
        
        while(objectIterator.hasNext()) {
        	JsonElement object = objectIterator.next();
        	JsonObject jObj = object.getAsJsonObject();
        	//System.out.println(jObj.get("place name").toString() + jObj.get("state").toString() );
        	collegeList.add(jObj.get("name").toString().replaceAll("^\"|\"$", ""));
        	stateList.add(jObj.get("state-province").toString().replaceAll("^\"|\"$", ""));
        	webPageList.add(jObj.get("web_pages").toString().replaceAll("^\"|\"$", ""));
        }
        //finalJsonObject.addProperty("country", country);
        finalJsonObject.addProperty("associatedcolleges", collegeList.toString());
        finalJsonObject.addProperty("associatedcollegesize", collegeList.size());
        finalJsonObject.addProperty("state", stateList.toString());
        finalJsonObject.addProperty("statename", stateList.get(0).toString());
        finalJsonObject.addProperty("associatedwebpages", webPageList.toString());
        finalJsonObject.addProperty("associatedwebpagesize", webPageList.size());

        return finalJsonObject;
    }

    private JsonArray getCollegeDetailsByParams(String countryName,String name) throws IOException {

        StringBuilder responseData = new StringBuilder();
        JsonArray jsonArray = null;

        URL url = null;
        url = new URL("http://universities.hipolabs.com/search?name=" +  name + "&country=" + countryName);
        
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
//        System.out.println("Response Code : " + responseCode);

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {


            String line;

            while ((line = in.readLine()) != null) {
                responseData.append(line);
            }

            jsonArray = new Gson().fromJson(responseData.toString(), JsonArray.class);
            
        }
        return jsonArray;
    }
}