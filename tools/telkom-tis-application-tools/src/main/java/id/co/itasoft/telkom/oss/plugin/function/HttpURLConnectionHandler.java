/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author asani
 */
public class HttpURLConnectionHandler {

    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String GET_URL = "https://localhost:9090/SpringMVCExample";

    private static final String POST_URL = "https://oss-person.telkom.co.id/jw/web/json/plugin/co.id.telkom.person.plugin.GetListPersonGroupMember/service";

    private static final String POST_PARAMS = "{person_code:CAT}";

//    public static void main(String[] args) throws IOException {
////        sendGET();
//        System.out.println("GET DONE");
//        sendPOST2();
//        System.out.println("POST DONE");
//    }

    private static void sendGET() throws IOException {
        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("GET request did not work.");
        }

    }

//    private static void sendPOST() throws IOException, JSONException {
//        URL obj = new URL(POST_URL);
//        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//        con.setRequestMethod("POST");
//        con.setRequestProperty("User-Agent", USER_AGENT);
////        con.seta
//        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//        con.setRequestProperty("Accept", "application/json");
//        JSONObject jsons;
//        // set body param
//        jsons = new JSONObject();
//        jsons.put("person_code", "CAT");
//
//        // For POST only - START
//        con.setDoOutput(true);
////        OutputStream os = con.getOutputStream();
//        OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
//        os.write(jsons.toString());
//        os.flush();
//        os.close();
//        // For POST only - END
//
//        int responseCode = con.getResponseCode();
//        System.out.println("POST Response Code :: " + responseCode);
//
//        if (responseCode == HttpURLConnection.HTTP_OK) { //success
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String inputLine;
//            StringBuffer response = new StringBuffer();
//
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//            // print result
//            System.out.println(response.toString());
//        } else {
//            System.out.println("POST request did not work.");
//        }
//    }
    private static void sendPOST2() throws MalformedURLException, IOException {
        URL url = new URL(POST_URL);
        String method = "POST";
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        System.out.println("id.co.itasoft.telkom.oss.plugin.function.HttpURLConnectionHandler.sendPOST2()");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("Accept", "application/json");
//        con.setRequestProperty("api_id", "API-79b23cd8-571b-4f11-98e7-5168a992ba2a");
//        con.setRequestProperty("api_key", "050124dfb45c41ef80d15a20e9ae5cff");
        con.setDoOutput(true);

        //Construct JSON Request 
//        JSONObject obj = new JSONObject();
//        obj.put("person_code", "CAT");
        try {
//            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
//            //Make a Request 
//            wr.write(POST_PARAMS);
//            wr.flush();
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(POST_PARAMS.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);
            System.out.println(HttpURLConnection.HTTP_OK);
            if (responseCode == HttpURLConnection.HTTP_OK) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                
                in.close();

                // print result
                System.out.println(response.toString());
            } else {
                System.out.println("POST request did not work.");
            }

            //Get Response 
//            String line;
//            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            StringBuilder sb = new StringBuilder();
//            while ((line = br.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//            br.close();
//            String js = sb.toString();
//            System.out.println(js);
//            JSONObject jsonObject = new JSONObject(js);
//            //LogUtil.info(this.getClass().getName(), "Response From Schedulling ori: " + js); 
//            String data_sched = js;
            //record call API schedulling 
//            String convResp = String.valueOf(data_sched);
//            String convReq = String.valueOf(obj);
//            wr.close();
        } catch (Exception e) {
        } finally {
            con.disconnect();
//            obj = null;
        }

    }
}
