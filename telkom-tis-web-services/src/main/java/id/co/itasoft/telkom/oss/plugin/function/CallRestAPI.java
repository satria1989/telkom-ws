/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.function;

import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.joget.commons.util.LogUtil;
import org.json.JSONObject;

/**
 *
 * @author tarkiman
 */
public class CallRestAPI {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    LogInfo info = new LogInfo();
    // one instance, reuse
    ConnectionPool connectionPool = new ConnectionPool(20, 5, TimeUnit.MINUTES);
    private final OkHttpClient httpClient =
            new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectionPool(connectionPool)
                    .retryOnConnectionFailure(false)
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
                .addHeader("token", apiConfig.getApiToken())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            response.body().close();
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
                .addHeader("token", apiConfig.getApiToken())
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            response.body().close();
            response.close();
        }
        return stringResponse;
    }

    //=====================================================================================================================//
    public String sendGetWithoutToken(ApiConfig apiConfig, HashMap<String, String> params) throws Exception {

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
                .addHeader("Origin", "https://oss-incident.telkom.co.id")
                .build();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                stringResponse = response.body().string();
                response.body().close();
                response.close();
                throw new IOException("Unexpected code " + response);
            } else {
                stringResponse = response.body().string();
                response.body().close();
                response.close();
            }
            long time = response.receivedResponseAtMillis() - response.sentRequestAtMillis();
            // Get response body
        } catch (Exception ex) {
            info.Log(getClass().getName(), ex.getMessage());
        } finally {
            response.close();
        }

        return stringResponse;

    }

    public String sendGetWithoutToken2(ApiConfig apiConfig, HashMap<String, String> params) throws Exception {

        String stringResponse = "";

        HttpUrl.Builder httpBuilder = HttpUrl.parse(apiConfig.getUrl()).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            response.body().close();
            response.close();
        }

        return stringResponse;

    }

    public String sendPostWithoutToken(ApiConfig apiConfig, String idTicket) throws Exception {

        String stringResponse = "";

        JSONObject objData = new JSONObject();
        objData.put("ticket_number", idTicket);
        objData.put("action", "REOPEN");
        RequestBody body = RequestBody.create(JSON, objData.toString());

        Request request = new Request.Builder()
                .url(apiConfig.getUrl())
                .addHeader("api_key", apiConfig.getApiKey()) // add request headers
                .addHeader("api_id", apiConfig.getApiId())
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            response.body().close();
            response.close();
        }
        return stringResponse;
    }

    public String sendPostTokenIbooster(ApiConfig apiConfig, RequestBody formBody) throws Exception {

        String stringResponse = "";

        Request request = new Request.Builder()
                .url(apiConfig.getUrl())
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            response.body().close();
            response.close();
        }
        return stringResponse;
    }

    public String getToken() throws Exception {
        ApiConfig apiConf = new ApiConfig();
        MasterParamDao dao = new MasterParamDao();
        apiConf = dao.getUrlToken("get_access_token");
        String stringResponse = "";
        String token = "";

        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", apiConf.getClientId())
                .add("client_secret", apiConf.getClientSecret())
                .build();

        Request request = new Request.Builder()
                .url(apiConf.getUrl())
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            JSONObject obj = new JSONObject(stringResponse);
            token = obj.getString("access_token");
            response.body().close();
            response.close();
        }
        return token;
    }

    public String getTokenApigwWsaDev() throws Exception {
        ApiConfig apiConf = new ApiConfig();
        MasterParamDao dao = new MasterParamDao();
        apiConf = dao.getUrlToken("get_access_token_apigwwsadev");
        String stringResponse = "";
        String token = "";

        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", apiConf.getClientId())
                .add("client_secret", apiConf.getClientSecret())
                .build();

        Request request = new Request.Builder()
                .url(apiConf.getUrl())
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            JSONObject obj = new JSONObject(stringResponse);
            token = obj.getString("access_token");
            response.body().close();
            response.close();
        }
        return token;
    }

    public String getTokenApigwsit() throws Exception {
        ApiConfig apiConf = new ApiConfig();
        MasterParamDao dao = new MasterParamDao();
        apiConf = dao.getUrlToken("get_access_token_apigwsit");
        String stringResponse = "";
        String token = "";

        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", apiConf.getClientId())
                .add("client_secret", apiConf.getClientSecret())
                .build();

        Request request = new Request.Builder()
                .url(apiConf.getUrl())
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            JSONObject obj = new JSONObject(stringResponse);
            token = obj.getString("access_token");
            response.body().close();
            response.close();
        }
        return token;
    }

    public String sendPostIbooster(ApiConfig apiConfig, RequestBody formBody, String token) throws Exception {

        String stringResponse = "";
        Request request = new Request.Builder()
                .url(apiConfig.getUrl())
                .addHeader("Authorization", "Bearer " + token)
                .post(formBody)
                .build();

        info.Log("url ibooster : ", apiConfig.getUrl());
        info.Log(this.getClass().getName(), "MASUK FUNGSI PANGGIL API");

        try (Response response = httpClient.newCall(request).execute()) {
            info.Log(this.getClass().getName(), "INI UDAH MANGGIL API BUD " + response.body().toString());

            if (!response.isSuccessful()) {
//                throw new IOException("Unexpected code " + response);
                JSONObject responseError = new JSONObject();
                responseError.put("code", response.code());
                responseError.put("status", response.isSuccessful());
                return responseError.toString();
            }

            // Get response body
            stringResponse = response.body().string();
            response.body().close();
            response.close();
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
        }
        return stringResponse;
    }

    public String handlePutRequest(ApiConfig apiConfig, RequestBody formBody, String token) throws Exception {

        String stringResponse = "";
        Request request = new Request.Builder()
                .url(apiConfig.getUrl())
                .addHeader("Authorization", "Bearer " + token)
                .put(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            response.body().close();
            response.close();
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
        }
        return stringResponse;
    }

    public String updateWo(ApiConfig apiConfig, String wonum, String idTicket) throws Exception {

        id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao lhDao = new id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao();
        org.json.JSONObject resObj;

        String stringResponse = "";

        JSONObject objData = new JSONObject();
        objData.put("wonum", wonum);
        objData.put("status", "CANCELED");
        objData.put("changeBy", "Outter-system");
        RequestBody body = RequestBody.create(JSON, objData.toString());

        Request request = new Request.Builder()
                .url(apiConfig.getUrl())
                .addHeader("api_key", apiConfig.getApiKey()) // add request headers
                .addHeader("api_id", apiConfig.getApiId())
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            stringResponse = response.body().string();

            response.body().close();
            response.close();

            resObj = new org.json.JSONObject(stringResponse.toString());
            lhDao.SENDHISTORY(
                    idTicket,
                    "update_wo_canceled",
                    apiConfig.getUrl(),
                    "GET",
                    objData,
                    resObj,
                    response.code());

        } catch (Exception ex) {
            info.Log(getClass().getName(), ex.getMessage());
        } finally {
            apiConfig = null;
            lhDao = null;
            objData = null;
            resObj = null;
        }
        return stringResponse;
    }

    public JSONObject callToApigw(ApiConfig apiConfig, RequestBody formBody, String token, String ticketId, String action, JSONObject JsonRequset) throws Exception {
        JSONObject result = new JSONObject();
        JSONObject req = null;
        JSONObject res = null;
        String stringResponse = "";
        Request request = new Request.Builder()
                .url(apiConfig.getUrl())
                .addHeader("Authorization", "Bearer " + token)
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            int responseCode = response.code();
            info.Log(this.getClass().getName(), "##responseCode : "+responseCode);
            if (response.isSuccessful()) {
                info.Log(this.getClass().getName(), "##response is success");
                stringResponse = response.body().string();
                result.put("status", true);
                result.put("response", stringResponse);
                result.put("status_code", response.code());
            } else {
                info.Log(this.getClass().getName(), "##response is failed");
                stringResponse = response.body().string();
                result.put("status", false);
                result.put("response", stringResponse);
            }
            req = new JSONObject(JsonRequset.toString());
            info.Log(this.getClass().getName(), "##REQ FORM BODY : "+JsonRequset.toString());
            res = new JSONObject(stringResponse);

            id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao lhDao = new id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao();
            lhDao.SENDHISTORY(ticketId,
                    action,
                    apiConfig.getUrl(),
                    "POST",
                    req,
                    res,
                    responseCode);

            response.body().close();
            response.close();

        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
        }
        return result;
    }

}
