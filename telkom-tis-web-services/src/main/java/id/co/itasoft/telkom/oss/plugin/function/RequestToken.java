/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.function;

import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 *
 * @author suenawati
 */
public class RequestToken {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    
    boolean reqToken = true;
    boolean reqIbooster = true;
    String token = "";
    ApiConfig apiConfig = new ApiConfig();
    OkHttpSingleton localSingleTon = OkHttpSingleton.getInstance();
    public final OkHttpClient httpClient = new OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
    
    

//    public String getToken() {
//
//        try {
//
//            apiConfig.setUrl("https://apigwsit.telkom.co.id:7777/invoke/pub.apigateway.oauth2/getAccessToken");
//
//            RequestBody formBody = new FormBody.Builder()
//                    .add("grant_type", "client_credentials")
//                    .add("client_id", "735235b2-7aa1-4264-a054-83109bc1d67f")
//                    .add("client_secret", "d4693b1c-a324-421a-be67-1e8d3a765245")
//                    .build();
//            String response = "";
//            Request request = new Request.Builder()
//                    .url(apiConfig.getUrl())
//                    .post(formBody)
//                    .build();
//            response = sendAPI(request);
//
//            JSONParser parse = new JSONParser();
//            JSONObject data_obj = (JSONObject) parse.parse(response);
//            token = data_obj.get("access_token").toString();
//
//        } catch (Exception ex) {
//            LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
//        }
//        return token;
//    }

    public String sendAPI(Request request) throws Exception {

        String stringResponse = "";

        try ( Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                reqToken = false;
                stringResponse = response.body().string();
//                throw new IOException("Unexpected code " + response);
//                  stringResponse;
            } else {
                // Get response body
                stringResponse = response.body().string();
            }

            response.body().close();
            response.close();
        }
        return stringResponse;
    }

}
