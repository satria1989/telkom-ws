/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.function;

import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

/**
 *
 * @author tarkiman
 */
public class CallRestAPI {

    RESTAPI _RESTAPI;
    LogInfo logInfo = new LogInfo();
    
    // one instance, reuse
    OkHttpSingleton localSingleTon = OkHttpSingleton.getInstance();
    ConnectionPool connectionPool = new ConnectionPool(5, 5, TimeUnit.SECONDS);

    private OkHttpClient httpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .connectionPool(connectionPool)
            .build();

    public String sendGet(ApiConfig apiConfig, HashMap<String, String> params) throws Exception {

        String stringResponse = "";

        HttpUrl.Builder httpBuilder = HttpUrl.parse(apiConfig.getUrl()).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .addHeader("api_key", apiConfig.getApiKey()) // add request headers
                .addHeader("api_id", apiConfig.getApiId())
                .addHeader("User-Agent", "Incident")
                .addHeader("Origin", "https://oss-incident.telkom.co.id")
                .build();
        Response response = null;
        try {
            response = localSingleTon.getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            response.body().close();
            response.close();
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            apiConfig = null;
            params = null;
            httpBuilder = null;
            request = null;
            response.close();
        }

        return stringResponse;

    }

    public String sendPost(ApiConfig apiConfig, RequestBody formBody) throws Exception {

        String stringResponse = "";

        Request request = new Request.Builder()
                .url(apiConfig.getUrl())
                .addHeader("api_key", apiConfig.getApiKey()) // add request headers
                .addHeader("api_id", apiConfig.getApiId())
                .post(formBody)
                .build();
        Response response = null;
        try {
//            response = httpClient.newCall(request).execute();
            response = localSingleTon.getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                // Get response body
                stringResponse = response.body().string();
            }

            response.body().close();
            response.close();
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            response.close();
            apiConfig = null;
            formBody = null;
            request = null;
        }
        return stringResponse;
    }

    public String sendPostTokenIbooster(ApiConfig apiConfig, RequestBody formBody) throws Exception {

        String stringResponse = "";

        Request request = new Request.Builder()
                .url(apiConfig.getUrl())
                .post(formBody)
                .build();
        Response response = null;
        try {
//            response = httpClient.newCall(request).execute();
            response = localSingleTon.getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                // LogUtil.info(this.getClass().getName(), "Unexpected code " + response);
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            response.body().close();
            response.close();
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            apiConfig = null;
            formBody = null;
            request = null;
            response.close();
        }
        return stringResponse;
    }

    public String sendPostIbooster(ApiConfig apiConfig, RequestBody formBody, String token) throws Exception {

        String stringResponse = "";

        Request request = new Request.Builder()
                .url(apiConfig.getUrl())
                .addHeader("Authorization", "Bearer " + token)
                .post(formBody)
                .build();

        Response response = null;
        try {
//            response = httpClient.newCall(request).execute();
            response = localSingleTon.getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            response.body().close();
            response.close();
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            apiConfig = null;
            formBody = null;
            request = null;
            response.close();
        }
        return stringResponse;
    }

    public String sendGetHandler(ApiConfig apiConfig, HashMap<String, String> params, RequestBody formBody) throws Exception {
        String stringResponse = "";

        HttpUrl.Builder httpBuilder = HttpUrl.parse(apiConfig.getUrl()).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }
//        RequestBody body = new RAFImageInputStreamSpi
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();
        Response response = null;
        try {
//            response = httpClient.newCall(request).execute();
            response = localSingleTon.getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                // LogUtil.info(this.getClass().getName(), "Unexpected code " + response);
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            response.body().close();
            response.close();
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            apiConfig = null;
            params = null;
            httpBuilder = null;
            formBody = null;
            request = null;
            response.close();
        }

        return stringResponse;
    }

    public JSONObject sendGetWithoutToken(ApiConfig apiConfig, HashMap<String, String> params) throws Exception {

        _RESTAPI = new RESTAPI();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(apiConfig.getUrl()).newBuilder();
        if (params != null) {

            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .addHeader("api_key", apiConfig.getApiKey()) // add request headers
                .addHeader("api_id", apiConfig.getApiId())
                .build();

        JSONObject stringResponse = _RESTAPI.CALLAPIHANDLER(request);

        return stringResponse;
    }

    public String sendGetWithoutTokenString(ApiConfig apiConfig, HashMap<String, String> params) throws Exception {

        _RESTAPI = new RESTAPI();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(apiConfig.getUrl()).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .addHeader("api_key", apiConfig.getApiKey()) // add request headers
                .addHeader("api_id", apiConfig.getApiId())
                .build();

        String stringResponse = _RESTAPI.CALLAPI(request);
        return stringResponse;
    }

    public String updateWo(ApiConfig apiConfig, String wonum, String idTicket) throws Exception {
        LogHistory lh = new LogHistory();
        LogHistoryDao lhdao = new LogHistoryDao();
        _RESTAPI = new RESTAPI();
        String stringResponse = "";

        JSONObject objData = new JSONObject();
        objData.put("action", "CANCELED");
        objData.put("wonum", wonum);
        objData.put("status", "CANCELED");
        objData.put("changeBy", "Outter-system");
        RequestBody body = RequestBody.create(_RESTAPI.JSON, objData.toString());
        Request request = new Request.Builder()
                .url(apiConfig.getUrl())
                .addHeader("api_key", apiConfig.getApiKey()) // add request headers
                .addHeader("api_id", apiConfig.getApiId())
                .post(body)
                .build();
        Response response = null;
        try {
            response = localSingleTon.getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                // LogUtil.info(this.getClass().getName(), "Unexpected code " + response);
                throw new IOException("Unexpected code " + response);
            }

            stringResponse = response.body().string();

            response.body().close();
            response.close();
            lh.setUrl(apiConfig.getUrl());
            lh.setAction("update_wo_canceled");
            lh.setMethod("POST");
            lh.setRequest(objData.toString());
            lh.setResponse(stringResponse);
            lh.setTicketId(idTicket);
            boolean insertLog = lhdao.insertToLogHistory(lh);
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            objData = null;
            apiConfig = null;
            lh = null;
            lhdao = null;
            response.close();
            localSingleTon = null;
        }
        return stringResponse;
    }

}
