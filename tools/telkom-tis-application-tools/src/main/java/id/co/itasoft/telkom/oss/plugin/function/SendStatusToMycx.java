/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.function;

import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.io.IOException;
import java.sql.SQLException;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.simple.JSONObject;

/**
 *
 * @author itasoft
 */
public class SendStatusToMycx {

    RESTAPI _RESTAPI = null;
    boolean result = false;

    public void updateStatusTicket(TicketStatus ts, MasterParam param) throws SQLException {
        LogHistory lh = new LogHistory();
        LogHistoryDao lhdao = new LogHistoryDao();
        JSONObject json = new JSONObject();
        _RESTAPI = new RESTAPI();

        try {
            json = new JSONObject();
            String status = (ts.getStatus() != null) ? ts.getStatus() : ts.getStatusCurrent();

            if ("REQUEST_PENDING".contains(status)) {
                status = ts.getStatusCurrent();
            }

            if ("ANALYSIS".equalsIgnoreCase(status)) {
                status = "QUEUED";
            }

            String time = ts.getDateModified().toString().split("\\.")[0];
            json.put("ticketid", ts.getTicketId());
            json.put("status", status);
            json.put("description", "Update Status " + status.toUpperCase());
            json.put("date", time.toString());
            json.put("ref", "REOPEN");

            // SET TO LOG HISTORY MODEL
            lh.setUrl(param.getUrl());
            lh.setAction("updateStatusTicketMYCX(" + status + ")");
            lh.setMethod("POST");
            lh.setRequest(json.toString());
            lh.setTicketId(ts.getTicketId());

            RequestBody jsonRequestBody = RequestBody.create(_RESTAPI.JSON, json.toJSONString());
            String _token = _RESTAPI.getToken();
            Request request = new Request.Builder()
                    .url(param.getUrl())
                    .addHeader("Authorization", "Bearer " + _token)
                    .post(jsonRequestBody)
                    .build();

            String stringResponse = _RESTAPI.CALLAPI(request);
            lh.setResponse(stringResponse);
            jsonRequestBody = null;
            request = null;
            stringResponse = null;
        } catch (IOException ex) {
            // LogUtil.error(this.getClass().getName(), ex, "ERROR UPDATE STATUS TICKET TO MYCX : " + ex.getMessage());
        } finally {
            try {
                lhdao.insertToLogHistory(lh);
            } catch (Exception ex) {
                // LogUtil.error(getClass().getName(), ex, "err:" + ex.getMessage());
            }
            _RESTAPI = null;
            lh = null;
            lhdao = null;
            json.clear();
        }

    }

    public void updateStatusTicketWithToken(TicketStatus ts, MasterParam param, String token) throws SQLException {
        LogHistory lh = new LogHistory();
        LogHistoryDao lhdao = new LogHistoryDao();
        JSONObject json = new JSONObject();
        _RESTAPI = new RESTAPI();

        try {
            json = new JSONObject();
            String status = (ts.getStatus() != null) ? ts.getStatus() : ts.getStatusCurrent();

            if ("REQUEST_PENDING".contains(status)) {
                status = ts.getStatusCurrent();
            }

            if ("ANALYSIS".equalsIgnoreCase(status)) {
                status = "QUEUED";
            }

            String time = ts.getDateModified().toString().split("\\.")[0];
            json.put("ticketid", ts.getTicketId());
            json.put("status", status);
            json.put("description", "Update Status " + status.toUpperCase());
            json.put("date", time.toString());
            json.put("ref", "REOPEN");
//            json.put("ref", "REOPEN");

            // SET TO LOG HISTORY MODEL
            lh.setUrl(param.getUrl());
            lh.setAction("updateStatusTicketMYCX(" + status + ")");
            lh.setMethod("POST");
            lh.setRequest(json.toString());
            lh.setTicketId(ts.getTicketId());

            RequestBody jsonRequestBody = RequestBody.create(_RESTAPI.JSON, json.toJSONString());
            Request request = new Request.Builder()
                    .url(param.getUrl())
                    .addHeader("Authorization", "Bearer " + token)
                    .post(jsonRequestBody)
                    .build();

            String stringResponse = _RESTAPI.CALLAPI(request);
            lh.setResponse(stringResponse);
            jsonRequestBody = null;
            request = null;
            stringResponse = null;
        } catch (IOException ex) {
            // LogUtil.error(this.getClass().getName(), ex, "ERROR UPDATE STATUS TICKET TO MYCX : " + ex.getMessage());
        } finally {
            try {
                lhdao.insertToLogHistory(lh);
            } catch (Exception ex) {
                // LogUtil.error(getClass().getName(), ex, "err:" + ex.getMessage());
            }
            _RESTAPI = null;
            lh = null;
            lhdao = null;
            json.clear();
        }

    }

    public void sendStatusSalamsim(TicketStatus ts, MasterParam param, String action_status) throws SQLException {

        JSONObject json = new JSONObject();
        JSONObject jsonRoot1 = new JSONObject();
        ApiConfig apiConfig = new ApiConfig();
        LogHistory lh = new LogHistory();
        LogHistoryDao lhdao = new LogHistoryDao();
        String REQUEST = "";

        _RESTAPI = new RESTAPI();
        apiConfig.setUrl(param.getUrl());
        String IN_STATUS = "";
        json = new JSONObject();
        boolean sendWAStat = false;
        boolean result = false;
        String act = "";

        if ("RESOLVE".equalsIgnoreCase(action_status) && !"CLOSED".equalsIgnoreCase(ts.getStatusCurrent())) { // ketika masuk kesalamsim
            IN_STATUS = "11";
            sendWAStat = !sendWAStat;
            act = "ENTRY";
        } else if ("REOPEN".equalsIgnoreCase(action_status)) { // reopen dari salamsim
            IN_STATUS = "14";
            sendWAStat = !sendWAStat;
            act = "REOPEN";
        } else if ("RESOLVE".equalsIgnoreCase(action_status) && "CLOSED".equalsIgnoreCase(ts.getStatusCurrent()) && !"16".equalsIgnoreCase(ts.getClosedBy())) { // closed dari salamsim 
            IN_STATUS = "13";
            sendWAStat = !sendWAStat;
            act = "CLOSED";
        } else {
            // LogUtil.info(getClass().getName(), "sendSalamsimToMycx : IN_STATUS EMPTY");
        }

        if (sendWAStat) {
            jsonRoot1.put("IN_ID", ts.getTicketId());
            jsonRoot1.put("IN_STATUS", IN_STATUS);
            jsonRoot1.put("IN_SOURCE", "NOSSA");
            jsonRoot1.put("IN_NOTE", "");

            json.put("inboxProspect", jsonRoot1);

            RequestBody body = RequestBody.create(_RESTAPI.JSON, json.toString());

            try {
                String token = _RESTAPI.getToken();
                Request request = new Request.Builder()
                        .url(param.getUrl())
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", "Bearer " + token)
                        .post(body)
                        .build();

                String sendSalamsim = _RESTAPI.CALLAPI(request);
                request = null;
                // SET TO LOG HISTORY MODEL
                lh.setUrl(param.getUrl());
                lh.setAction("salamsimToMycx(" + act + ")");
                lh.setMethod("POST");
                lh.setRequest(json.toString());
                lh.setResponse(sendSalamsim);
                lh.setTicketId(ts.getTicketId());
                // lh.setStatus(IN_STATUS);
                request = null;
            } catch (Exception ex) {
                // LogUtil.error(getClass().getName(), ex, "ERROR SEND SALAMSIM :" + ex.getMessage());
            } finally {
                try {
                    lhdao.insertToLogHistory(lh);
                } catch (Exception ex) {
                    // LogUtil.error(getClass().getName(), ex, "msg :" + ex.getMessage());
                }
                _RESTAPI = null;
                json.clear();
                jsonRoot1.clear();
                apiConfig = null;
                lh = null;
                lhdao = null;
                body = null;
            }
        }
    }
    
    public void sendStatusSalamsimWithToken(TicketStatus ts, MasterParam param, String action_status, String TOKEN) throws SQLException {

        JSONObject json = new JSONObject();
        JSONObject jsonRoot1 = new JSONObject();
        ApiConfig apiConfig = new ApiConfig();
        LogHistory lh = new LogHistory();
        LogHistoryDao lhdao = new LogHistoryDao();
        String REQUEST = "";

        _RESTAPI = new RESTAPI();
        apiConfig.setUrl(param.getUrl());
        String IN_STATUS = "";
        json = new JSONObject();
        boolean sendWAStat = false;
        boolean result = false;
        String act = "";

        if ("RESOLVE".equalsIgnoreCase(action_status) && !"CLOSED".equalsIgnoreCase(ts.getStatusCurrent())) { // ketika masuk kesalamsim
            IN_STATUS = "11";
            sendWAStat = !sendWAStat;
            act = "ENTRY";
        } else if ("REOPEN".equalsIgnoreCase(action_status)) { // reopen dari salamsim
            IN_STATUS = "14";
            sendWAStat = !sendWAStat;
            act = "REOPEN";
        } else if ("RESOLVE".equalsIgnoreCase(action_status) && "CLOSED".equalsIgnoreCase(ts.getStatusCurrent()) && !"16".equalsIgnoreCase(ts.getClosedBy())) { // closed dari salamsim 
            IN_STATUS = "13";
            sendWAStat = !sendWAStat;
            act = "CLOSED";
        } else {
            // LogUtil.info(getClass().getName(), "sendSalamsimToMycx : IN_STATUS EMPTY");
        }

        if (sendWAStat) {
            jsonRoot1.put("IN_ID", ts.getTicketId());
            jsonRoot1.put("IN_STATUS", IN_STATUS);
            jsonRoot1.put("IN_SOURCE", "NOSSA");
            jsonRoot1.put("IN_NOTE", "");

            json.put("inboxProspect", jsonRoot1);

            RequestBody body = RequestBody.create(_RESTAPI.JSON, json.toString());

            try {
                Request request = new Request.Builder()
                        .url(param.getUrl())
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", "Bearer " + TOKEN)
                        .post(body)
                        .build();

                String sendSalamsim = _RESTAPI.CALLAPI(request);
                request = null;
                // SET TO LOG HISTORY MODEL
                lh.setUrl(param.getUrl());
                lh.setAction("salamsimToMycx(" + act + ")");
                lh.setMethod("POST");
                lh.setRequest(json.toString());
                lh.setResponse(sendSalamsim);
                lh.setTicketId(ts.getTicketId());
                // lh.setStatus(IN_STATUS);
                request = null;
            } catch (Exception ex) {
                // LogUtil.error(getClass().getName(), ex, "ERROR SEND SALAMSIM :" + ex.getMessage());
            } finally {
                try {
                    lhdao.insertToLogHistory(lh);
                } catch (Exception ex) {
                    // LogUtil.error(getClass().getName(), ex, "msg :" + ex.getMessage());
                }
                _RESTAPI = null;
                json.clear();
                jsonRoot1.clear();
                apiConfig = null;
                lh = null;
                lhdao = null;
                body = null;
            }
        }
    }
}
