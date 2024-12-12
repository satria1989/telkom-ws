/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.function;

import static id.co.itasoft.telkom.oss.plugin.function.RESTAPI.JSON;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.io.IOException;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author mtaupiq
 */
public class SendStatusToMYIHXApi {

    JSONObject json = new JSONObject();
//    RequestToken rt = new RequestToken();
    RESTAPI _RESTAPI = new RESTAPI();
    String REQUEST = "";
    boolean result = false;
    OkHttpSingleton localSingleTon = OkHttpSingleton.getInstance();
    LogInfo info = new LogInfo();
    id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao logHistory = new id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao();

    public Boolean updateStatusToMyihx(Ticket ts, ApiConfig ac) {

        try {
            String transactionId = (ts.getExtenalTicketId() == null) ? "" : ts.getExtenalTicketId();
            JSONObject request = new JSONObject();
            request.put("transactionId", transactionId);
            request.put("status", "IN_PROGRESS");
            
            
            info.Log(getClass().getName(), "UPDATE MYIHX TIBA :"+ request.toString());
            boolean REQUEST = requestToAPIGWSIT(
                    "updateStatusToMyihx (in_progress)", 
                    ts.getStatus(), 
                    ts.getIdTicket(), 
                    ac.getUrl(), 
                    request
            );
        } catch (Exception ex) {
          info.Log(getClass().getName(), ex.getMessage());
        } finally {
            json = null;
        }
        return result;
    }

    public Boolean requestToAPIGWSIT(String action, String status, String ticketId, String url, JSONObject json) throws IOException, JSONException {

        LogHistory lh = new LogHistory();
        LogHistoryDao lhdao = new LogHistoryDao();

        result = !result;
        String stringResponse = "";
        // LogUtil.info(this.getClass().getName(), "JSON :" + json.toJSONString());
        RequestBody jsonRequestBody = RequestBody.create(JSON, json.toString());
        String _token = _RESTAPI.getToken();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + _token)
                .post(jsonRequestBody)
                .build();
        Response response = null;
        int responseCode = 0;
        
        info.Log(getClass().getName(), "CALL MYIHX TIBA ");
        try {
            response = localSingleTon.getClient().newCall(request).execute();
            responseCode = response.code();
            
            if (!response.isSuccessful()) {
                result = !result;
                stringResponse = response.message();
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            response.body().close();
            response.close();
        } catch (Exception ex) {
          info.Log(getClass().getName(), ex.getMessage());
        } finally {

//            String ticketId,
//            String action,
//            String url,
//            String method,
//            JSONObject request,
//            JSONObject response,
//            int responseCode
//            
            Object object = new JSONTokener(stringResponse).nextValue();
            JSONObject res = (JSONObject) object;
            logHistory.SENDHISTORY(
                    ticketId, 
                    action, 
                    url, 
                    "POST", 
                    json, 
                    res, 
                    responseCode
            );
            
            response.close();
        }

        return result;
    }
}
