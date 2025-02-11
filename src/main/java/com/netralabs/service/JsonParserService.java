package com.netralabs.service;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JsonParserService {

  public static JSONObject parseJson(String jsonFile) throws IOException, ParseException, FileNotFoundException {
    JSONParser parser = new JSONParser();
    FileReader reader = new FileReader(jsonFile);
    Object obj = parser.parse(reader);
    if (obj instanceof JSONArray jsonArray) {
      return (JSONObject) jsonArray.get(0);
    }
    return null;
  }

}
