/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.function;

import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.joget.commons.util.LogUtil;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author asani
 */
public class RESTAPI {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpSingleton localSingleton = OkHttpSingleton.getInstance();

    public String CALLAPI(Request request) throws IOException {
        String stringResponse = "";
        Response response = null;
        try {
            response = localSingleton.getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                stringResponse = response.message();
                throw new IOException("Unexpected code " + response);
            } else {
                stringResponse = response.body().string();
            }

        } catch (Exception ex) {
             LogUtil.error(getClass().getName(), ex, ex.getMessage());
        } finally {

            // Get response body
            response.body().close();
            response.close();
        }
        return stringResponse;

    }

    public JSONObject CALLAPIHANDLER(Request request) throws IOException {
        String stringResponse = "";
        JSONObject json = new JSONObject();
        Response response = null;
        try {

            response = localSingleton.getClient().newCall(request).execute();

            if (!response.isSuccessful()) {
                json.put("status", response.isSuccessful());
                json.put("status_code", response.code());
                json.put("msg", response.message());
//                throw new IOException("Unexpected code " + response);
            } else {
                json.put("status", response.isSuccessful());
                json.put("status_code", response.code());
                json.put("msg", response.body().string());
            }

        } catch (Exception ex) {
            // LogUtil.error(getClass().getName(), ex, ex.getMessage());
        } finally {
            response.close();
        }
        return json;

    }

    public String getToken() {
        ApiConfig apiConfig = new ApiConfig();
        MasterParam param = new MasterParam();
        MasterParamDao paramDao = new MasterParamDao();
        String response = "", token = "";
        try {
            param = new MasterParam();
            apiConfig = new ApiConfig();

            param = paramDao.getUrl("get_access_token");
            apiConfig.setUrl(param.getUrl());

            RequestBody formBody = new FormBody.Builder()
                    .add("grant_type", "client_credentials")
                    .add("client_id", param.getClient_id())
                    .add("client_secret", param.getClient_secret())
                    .build();
            Request request = new Request.Builder()
                    .url(apiConfig.getUrl())
                    .post(formBody)
                    .build();

            response = CALLAPI(request);
            Object object = new JSONTokener(response).nextValue();
            if (object instanceof JSONObject) {
                JSONObject json = (JSONObject) object;
                token = json.get("access_token").toString();
            }
            formBody = null;
            response = null;
            request = null;

        } catch (Exception ex) {
            // LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            param = null;
            apiConfig = null;
            paramDao = null;
        }
        return token;
    }

}
