/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.function;

import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.io.IOException;
import java.sql.SQLException;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONObject;

/**
 *
 * @author mtaup
 */
public class SendStatusToMYIHX {

    String REQUEST = "";
    boolean result = false;

    public void updateStatusToMyihx(TicketStatus ts, MasterParam param) throws SQLException {
        RESTAPI _RESTAPI = new RESTAPI();
        
        String currentStatus = (ts.getStatus() == null)? "" : ts.getStatus();
        String classificationType = (ts.getClassification_type() == null) ?
            "" : ts.getClassification_type();
        String extTicketId = (ts.getExternalTicketid() == null) ?
            "" : ts.getExternalTicketid();
        String TicketId = (ts.getTicketId() == null) ?
            "" : ts.getTicketId();
        
        String status = "";
        if ("CLOSED".equalsIgnoreCase(currentStatus)) {
            if ("FISIK".equalsIgnoreCase(classificationType)) {
                status = "SOLVED";
            } else if ("LOGIC".equalsIgnoreCase(classificationType)) {
                status = "RESOLVED";
            }
        } else {
            status = "COMPLETED";
        }
        JSONObject json = new JSONObject();
        try {
            String token = _RESTAPI.getToken();
            json.put("transactionId", extTicketId);
            json.put("status", status);
            boolean REQUEST = requestToAPIGWSIT(
                "updateStatusToMyihx", 
                currentStatus, 
                TicketId, 
                param.getUrl(), 
                json, 
                token);
        } catch (IOException ex) {
            // LogUtil.error(this.getClass().getName(), ex, "ERROR UPDATE STATUS TICKET TO MYIHX : " + ex.getMessage());
        } finally {
            json.clear();
        }
    }
    
    public void updateStatusToMyihxWithToken(TicketStatus ts, MasterParam param, String token) throws SQLException {
        String currentStatus = (ts.getStatus() == null)? "" : ts.getStatus();
        String classificationType = (ts.getClassification_type() == null) ?
            "" : ts.getClassification_type();
        String extTicketId = (ts.getExternalTicketid() == null) ?
            "" : ts.getExternalTicketid();
        String TicketId = (ts.getTicketId() == null) ?
            "" : ts.getTicketId();
        String status = "";
        if ("CLOSED".equalsIgnoreCase(currentStatus)) {
            if ("FISIK".equalsIgnoreCase(classificationType)) {
                status = "SOLVED";
            } else if ("LOGIC".equalsIgnoreCase(classificationType)) {
                status = "RESOLVED";
            }
        } else {
            status = "COMPLETED";
        }
        JSONObject json = new JSONObject();
        try {
            json.put("transactionId", ts.getExternalTicketid());
            json.put("status", status);
            boolean REQUEST = requestToAPIGWSIT(
                "updateStatusToMyihx", 
                currentStatus, 
                TicketId, 
                param.getUrl(), 
                json, 
                token
            );
        } catch (IOException ex) {
            // LogUtil.error(this.getClass().getName(), ex, "ERROR UPDATE STATUS TICKET TO MYIHX : " + ex.getMessage());
        } finally {
            json.clear();
        }
    }

    public void updateTicketToMyihx(TicketStatus ts, MasterParam param, String token) throws SQLException {
        JSONObject json = new JSONObject();
        String extTicketId = (ts.getExternalTicketid() == null) ?
            "" : ts.getExternalTicketid();
        String TicketId = (ts.getTicketId() == null) ?
            "" : ts.getTicketId();
        
        try {
            json.put("transactionId", extTicketId);
            json.put("ticketId", TicketId);
            boolean REQUEST = requestToAPIGWSIT(
                "updateTicketToMyihx", 
                "", 
                TicketId, 
                param.getUrl(), 
                json, 
                token
            );
        } catch (IOException ex) {
            // LogUtil.error(this.getClass().getName(), ex, "ERROR UPDATE TICKET TO MYIHX : " + ex.getMessage());
        } finally {
            json.clear();
        }
    }

    public Boolean requestToAPIGWSIT(String action, String status, String ticketId, String url, JSONObject json, String token) throws IOException, SQLException {

        LogHistory lh = new LogHistory();
        LogHistoryDao lhdao = new LogHistoryDao();
        RESTAPI _RESTAPI = new RESTAPI();

        result = !result;
        String stringResponse = "";

        RequestBody jsonRequestBody = RequestBody.create(_RESTAPI.JSON, json.toJSONString());
//        String _token = _RESTAPI.getToken();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(jsonRequestBody)
                .build();

        try {
            stringResponse = _RESTAPI.CALLAPI(request);
        } catch (Exception ex) {
            // LogUtil.error(this.getClass().getName(), ex, "ERROR requestToAPIGWSIT :" + ex.getMessage());
        } finally {
            try {
                lh.setUrl(url);
                lh.setAction(action + "(" + status + ")");
                lh.setMethod("POST");
                lh.setRequest(json.toString());
                lh.setResponse(stringResponse);
                lh.setTicketId(ticketId);
                lhdao.insertToLogHistory(lh);
            } catch (Exception ex) {
                // LogUtil.error(getClass().getName(), ex, "msg :" + ex.getMessage());
            }
            json.clear();
            lh = null;
            lhdao = null;
            request = null;
            jsonRequestBody = null;
            _RESTAPI = null;
        }
        return result;
    }
}
