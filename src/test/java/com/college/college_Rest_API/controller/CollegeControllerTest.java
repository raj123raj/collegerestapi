package com.college.college_Rest_API.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.college.college_RestAPI.controller.CollegeController;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CollegeControllerTest {
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new CollegeController()).build();
    }

    @Test
    public void testCollegeDetails() throws Exception {        
        //expected

        StringBuilder responseData = new StringBuilder();
        JsonObject expectedJsonObject = null;
        JsonArray expectedJsonArray = null;
        ArrayList expectedCollegeList = new ArrayList();
        ArrayList expectedStateList = new ArrayList();
        ArrayList expectedWebPageList = new ArrayList();
        
        ArrayList actualCollegeList = new ArrayList();
        ArrayList actualStateList = new ArrayList();
        ArrayList actualWebPageList = new ArrayList();
        URL url = new URL("http://universities.hipolabs.com/search?name=anna&country=India");  //This one will work

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {

            String line;

            while ((line = in.readLine()) != null) {
                responseData.append(line);
            }

            expectedJsonArray = new Gson().fromJson(responseData.toString(), JsonArray.class);

            Iterator<JsonElement> objectIterator =  expectedJsonArray.iterator();
            
            while(objectIterator.hasNext()) {
            	JsonElement object = objectIterator.next();
            	JsonObject jObj = object.getAsJsonObject();
            	//System.out.println(jObj.get("place name").toString() + jObj.get("state").toString() );
            	expectedCollegeList.add(jObj.get("name").toString().replaceAll("^\"|\"$", ""));
            	expectedStateList.add(jObj.get("state-province").toString().replaceAll("^\"|\"$", ""));
            	expectedWebPageList.add(jObj.get("web_pages").toString().replaceAll("^\"|\"$", ""));
            	//expectedLongitudeList.add(jObj.get("longitude").toString().replaceAll("^\"|\"$", ""));
            }
            
        }

        //actual
        MvcResult result = mockMvc.perform(get("/getCollegeDetailsBycountryNameAndSearchString?countryName=India&name=anna"))
                .andReturn();
        String recievedResponse = result.getResponse().getContentAsString();
        JsonObject actualJsonObject = new Gson().fromJson(recievedResponse, JsonObject.class);

        String actualState = actualJsonObject.get("statename").toString();
        actualState = actualState.replaceAll("^\"|\"$", "");
        String actualCollegesSize = actualJsonObject.get("associatedcollegesize").toString();
        actualCollegesSize = actualCollegesSize.replaceAll("^\"|\"$", "");
        //System.out.println("Associated places size.. " + actualPlacesSize);
        
        String actualWebPageSize = actualJsonObject.get("associatedwebpagesize").toString();
        actualWebPageSize = actualWebPageSize.replaceAll("^\"|\"$", "");
        
        assertEquals(expectedStateList.get(0).toString(), actualState);
        assertEquals(expectedCollegeList.size(), Integer.parseInt(actualCollegesSize));
        assertEquals(expectedWebPageList.size(), Integer.parseInt(actualWebPageSize));
    }

}
