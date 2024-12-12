/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.function;

import java.util.*;
import org.json.*;

/**
 *
 * @author itasoft
 */
public class FilterJsonArrObjHandler {

    LogInfo info = new LogInfo();
  
    public boolean hasValue(JSONArray json, String key, String value) throws JSONException {
        boolean result = false;
        for (int i = 0; i < json.length(); i++) {
            JSONObject obj = json.getJSONObject(i);
            String name = obj.getString(key);

            Iterator<?> paramIterator = obj.keys();
            while (paramIterator.hasNext()) {
                String param = (String) paramIterator.next();
                if (key.equals(param)) {
                    if (value.equals(obj.getString(key))) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    public JSONArray sortedArray(JSONArray json) {

        JSONArray jsonArray = json;
        JSONArray sortedJsonArray = new JSONArray();
        try {
            List list = new ArrayList();
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getJSONObject(i));
            }

      Collections.sort(
          list,
          new Comparator() {
            private static final String KEY_NAME = "bookingDate";

            @Override
            public int compare(Object a, Object b) {
              String str1 = new String();
              String str2 = new String();
              JSONObject x = (JSONObject) a;
              JSONObject y = (JSONObject) b;

              try {
                str1 = (String) x.get(KEY_NAME);
                str2 = (String) y.get(KEY_NAME);
              } catch (JSONException e) {
                info.Error(getClass().getName(), e.getMessage(), e);
              }

              return str1.compareTo(str2);
            }
          });
            for (int i = 0; i < jsonArray.length(); i++) {
                sortedJsonArray.put(list.get(i));
            }

            sortedJsonArray = groupingArrayByKey(sortedJsonArray);

        } catch (Exception ex) {
            info.Error(getClass().getName(), ex.getMessage(), ex);
        }
        return sortedJsonArray;
    }

    public JSONArray groupingArrayByKey(JSONArray json) {
        JSONArray x = new JSONArray();

        try {
            int count = 1;
            String bookingDatex = "";
            for (int i = 0; i < json.length(); i++) {
                bookingDatex = "";
                JSONObject jb = json.getJSONObject(i);
                String bookingDate = json.getJSONObject(i).getString("bookingDate");
                if ((i + 1) <= json.length()) {
                    // GET COUNT
                    int total = getCountByKey(json, "bookingDate", bookingDate);

                    if ((i + 1) != json.length()) {
                        bookingDatex = json.getJSONObject(i + 1).getString("bookingDate");
                    }

                    if (!bookingDate.contentEquals(bookingDatex)) {

                        jb.put("count", total);
                        x.put(jb);
                    }
                }

            }
        } catch (Exception ex) {
            info.Error(getClass().getName(), ex.getMessage(), ex);
        }
        return x;
    }

    public int getCountByKey(JSONArray jsonArr, String key, String value) {
        int count = 0;

        try {

            for (int i = 0; i < jsonArr.length(); i++) {
                String bookingDate = jsonArr.getJSONObject(i).getString("bookingDate");
                if (bookingDate.equals(value)) {
                    count++;
                }
            }
        } catch (Exception ex) {
            info.Error(getClass().getName(), ex.getMessage(), ex);
        }

        return count;
    }
}
