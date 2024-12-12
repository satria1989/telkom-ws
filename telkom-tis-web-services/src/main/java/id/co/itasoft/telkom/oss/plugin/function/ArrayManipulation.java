/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.function;

import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author asani
 */
public class ArrayManipulation {

    LogInfo logInfo;
    
    public ArrayManipulation() {
      logInfo = new LogInfo();
    }
    /**
     * Using binary search
     *
     * @return boolean
     */
    public boolean SearchDataOnArray(String[] arr, String value) {
        Arrays.sort(arr);

        // check if the specified element
        // is present in the array or not
        // using Binary Search method
        int res = Arrays.binarySearch(arr, value);
        boolean test = res >= 0 ? true : false;
        return test;
    }
    
    public String getOwnergroupOnJsonArray(JSONArray __arr, String paramChannel, String paramCustSegment) {
      String result="";
      try {
        for(int i= 0; i<__arr.length(); i++) {
          JSONObject json = __arr.getJSONObject(i);
          String channel = json.getString("channel");
          String custSegment = json.getString("customer_segment");
          String ownergroup = json.getString("ownergroup");
          boolean reversevalue = json.getBoolean("reversevalue");
          
          String _arrChannel[] = channel.split(";");
          String _arrCustSegment[] = custSegment.split(";");
          
          List<String> listChannel = Arrays.asList(_arrChannel);
          List<String> listCustSegment = Arrays.asList(_arrCustSegment);
          
          // PARAM channel = 25 && customersegment=dbs
          if(reversevalue) {
            if(!listChannel.contains(paramChannel) && listCustSegment.contains(paramCustSegment)) {
              result = ownergroup;
              break;
            } else {
              continue;
            }
          } else {
            if(listChannel.contains(paramChannel) && listCustSegment.contains(paramCustSegment)) {
              result = ownergroup;
              break;
            } else {
              continue;
            }
          }
          
        }
      } catch(Exception ex) {
        logInfo.Log(getClass().getName(), ex.getMessage());
      }
      return result;
      
   
    }
}
