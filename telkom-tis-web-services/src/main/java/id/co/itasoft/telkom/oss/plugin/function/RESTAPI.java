/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.function;

import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import okhttp3.*;
import org.joget.commons.util.LogUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

/**
 * @author asani
 */
public class RESTAPI {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpSingleton localSingleton = OkHttpSingleton.getInstance();

    LogInfo info = new LogInfo();

    public String CALLAPI(Request request) throws IOException, JSONException {
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
            JSONObject obj = new JSONObject();
            obj.put("msg", "Nothing response from API");
            stringResponse = obj.toString();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return stringResponse;

    }

    public JSONObject CALLAPIHANDLER(Request request) throws IOException, JSONException {
        String stringResponse = "";
        JSONObject json = new JSONObject();
        Response response = null;
        try {
            response = localSingleton.getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                json.put("status", false);
                json.put("status_code", response.code());
                json.put("response_message", response.message());
                info.Log(getClass().getName(), response.toString());
                info.Log(getClass().getName(), response.message());
                json.put("msg", response);
            } else {
                json.put("status", response.isSuccessful());
                json.put("status_code", response.code());
                json.put("msg", response.body().string());
            }

        } catch (Exception ex) {
            int stsCode = (response.code() > 0) ? response.code() : 400;
            json.put("status", false);
            json.put("status_code", stsCode);
            json.put("msg", ex.getMessage());

            info.Error(getClass().getName(), ex.getMessage(), ex);
        } finally {
            response.close();
        }
        return json;
    }

    public JSONObject CALLAPIHANDLERWO(Request request) throws IOException {
        String stringResponse = "";
        JSONObject json = new JSONObject();
        Response response = null;
        try {
            response = localSingleton.getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                json.put("status", response.isSuccessful());
                json.put("status_code", response.code());
                json.put("response_message", response.message());
                json.put("msg", responseBody.string());
            } else {
                json.put("status", response.isSuccessful());
                json.put("status_code", response.code());
                json.put("msg", response.body().string());
            }

        } catch (Exception ex) {
            info.Log(getClass().getName(), ex.getMessage());
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
            info.Log(getClass().getName(), ex.getMessage());
        } finally {
            param = null;
            apiConfig = null;
            paramDao = null;
        }
        return token;
    }

    public String getTokenFlexible(String useOfApi) {
        ApiConfig apiConfig = new ApiConfig();
        MasterParam param = new MasterParam();
        MasterParamDao paramDao = new MasterParamDao();
        String response = "", token = "";
        try {
            param = new MasterParam();
            apiConfig = new ApiConfig();

            param = paramDao.getUrl(useOfApi);
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
            info.Log(getClass().getName(), ex.getMessage());
        } finally {
            param = null;
            apiConfig = null;
            paramDao = null;
        }
        return token;
    }

    public void InsertLogApi(JSONObject json, String TicketId) {
        try {
            MasterParam masterParam = new MasterParam();
            MasterParamDao masterParamDao = new MasterParamDao();
            RESTAPI _RESTAPI = new RESTAPI();

            masterParam = masterParamDao.getUrl("insertLogApi");

            RequestBody body = RequestBody.create(_RESTAPI.JSON, json.toString());
            Request request =
                    new Request.Builder()
                            .url(masterParam.getUrl())
                            .header(
                                    "Authorization",
                                    getBasicAuthenticationHeader(
                                            masterParam.getjUsername(),
                                            masterParam.getjPassword()
                                    )
                            )
                            .post(body)
                            .build();

            _RESTAPI.CALLAPIHANDLER(request);
        } catch (Exception ex) {
            info.Error(getClass().getName(), ex.getMessage(), ex);
        }
    }

    private static final String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    public String getCurrentDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Define a custom format for the date and time if needed.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }

    public void SENDHISTORY(
            String ticketId,
            String action,
            String url,
            String method,
            JSONObject request,
            JSONObject response,
            int responseCode
    ) {
        try {

            JSONObject reqLogHistory = new JSONObject();
            reqLogHistory.put("ticketId", ticketId);
            reqLogHistory.put("action", action);
            reqLogHistory.put("url", url);
            reqLogHistory.put("method", method);
            reqLogHistory.put("@TIMESTAMP", LocalDateTime.now() + "+07:00");
            reqLogHistory.put("request", request);
            reqLogHistory.put("response", response);
            reqLogHistory.put("responseCode", responseCode);

            info.Log(getClass().getName(), reqLogHistory.toString());
            this.InsertLogApi(reqLogHistory, ticketId);

            reqLogHistory = null;
        } catch (Exception ex) {
            info.Error(getClass().getName(), ex.getMessage(), ex);
        }
    }

    public void addWorklogCts(Ticket ticket, String notes, String description, String createdBy, String externalTicketId) throws SQLException, JSONException, Exception {

        ApiConfig apiConf = new ApiConfig();
        MasterParamDao paramDao = new MasterParamDao();
        CallRestAPI callApi = new CallRestAPI();
        LogHistoryDao lhDao = new LogHistoryDao();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        String plainTextDescription = description.replaceAll("\\<.*?\\>", "");

        apiConf = paramDao.getUrlapi("ADDWORKLOGCTS");
        String token = callApi.getToken();
        String url = apiConf.getUrl() + "/" + externalTicketId;
        apiConf.setUrl(url);

        JSONObject reqObj = new JSONObject();
        JSONObject IncidentTicketRequest = new JSONObject();
        JSONObject eaiHeader = new JSONObject();
        JSONObject eaiBody = new JSONObject();
        JSONObject request = new JSONObject();

        eaiBody.put("notes", notes);
        eaiBody.put("description", plainTextDescription);
        eaiBody.put("createdBy", createdBy);
        eaiBody.put("nossaTicketNumber", ticket.getIdTicket());
        eaiBody.put("ctsTicketNumber", ticket.getExtenalTicketId());
        IncidentTicketRequest.put("eaiBody", eaiBody);

        eaiHeader.put("externalId", ticket.getExtenalTicketId());
        eaiHeader.put("timestamp", formattedDateTime);
        IncidentTicketRequest.put("eaiHeader", eaiHeader);
        reqObj.put("incidentTicketRequest", IncidentTicketRequest);

//        LogUtil.info(this.getClass().getName(), "REQUEST CTS : "+reqObj.toString());
        RequestBody body = RequestBody.create(JSON, reqObj.toString());

        String response = callApi.sendPostIbooster(apiConf, body, token);
        JSONObject resObj = new JSONObject(response);

        lhDao.SENDHISTORY(
                ticket.getIdTicket(),
                "ADDWORKLOGCTS",
                apiConf.getUrl(),
                "POST",
                reqObj,
                resObj,
                200);

//        RequestBody formBody = 
    }

    public void addWorklogInap(Ticket ticket, String summary, String createdBy, String externalTicketId) throws SQLException, JSONException, Exception {
//        LogUtil.info(this.getClass().getName(), "mbravo cts");

        ApiConfig apiConf = new ApiConfig();
        MasterParamDao paramDao = new MasterParamDao();
        CallRestAPI callApi = new CallRestAPI();
        LogHistoryDao lhDao = new LogHistoryDao();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        apiConf = paramDao.getUrlapi("inapWorkLog");
        String token = callApi.getToken();
        String url = apiConf.getUrl();
        apiConf.setUrl(url);

        JSONObject reqObj = new JSONObject();
        JSONObject updateWorkLogSDMRequest = new JSONObject();
        JSONObject eaiHeader = new JSONObject();
        JSONObject eaiBody = new JSONObject();

        eaiHeader.put("externalId", ticket.getExtenalTicketId());
        eaiHeader.put("timestamp", formattedDateTime);
        updateWorkLogSDMRequest.put("eaiHeader", eaiHeader);

        eaiBody.put("tt_id", ticket.getExtenalTicketId());
        eaiBody.put("telkom_tt_id", ticket.getIdTicket());
        eaiBody.put("remarks", summary);
        eaiBody.put("createdBy", createdBy);
//        eaiBody.put("createdBy", "");
        updateWorkLogSDMRequest.put("eaiBody", eaiBody);

        reqObj.put("updateWorkLogSDMRequest", updateWorkLogSDMRequest);

        RequestBody body = RequestBody.create(JSON, reqObj.toString());

        String response = callApi.sendPostIbooster(apiConf, body, token);
        JSONObject resObj = new JSONObject(response);

        lhDao.SENDHISTORY(
                ticket.getIdTicket(),
                "ADDWORKLOGINAP",
                apiConf.getUrl(),
                "POST",
                reqObj,
                resObj,
                200);

//        RequestBody formBody = 
    }

    public void updateTicketTelkomInap(Ticket ticketStatus, String summary) throws SQLException, JSONException, Exception {

        ApiConfig apiConf = new ApiConfig();
        MasterParamDao paramDao = new MasterParamDao();
        CallRestAPI callApi = new CallRestAPI();
        LogHistoryDao lhDao = new LogHistoryDao();

        apiConf = paramDao.getUrlapi("update_ticket_telkom_inap");
        String token = callApi.getToken();
        String externalTicketId = (ticketStatus.getExtenalTicketTier3() == null || ticketStatus.getExtenalTicketTier3().isEmpty()) ? "-" : ticketStatus.getExtenalTicketTier3();
        String url = apiConf.getUrl() + "/" + externalTicketId;
        apiConf.setUrl(url);

        String summaryTicket = (ticketStatus.getSummary() == null || ticketStatus.getSummary().isEmpty()) ? "-" : ticketStatus.getSummary();
        String nossaTerritory = (ticketStatus.getWorkzone() == null || ticketStatus.getWorkzone().isEmpty()) ? "-" : ticketStatus.getWorkzone();
        String nossaTicketNumber = (ticketStatus.getIdTicket() == null) ? "" : ticketStatus.getIdTicket();
        String rootCause = (ticketStatus.getIncidentDomain() == null || ticketStatus.getIncidentDomain().isEmpty()) ? "-" : ticketStatus.getIncidentDomain();
        String nossaStatusTicket = (ticketStatus.getTicketStatus() == null) ? "" : ticketStatus.getTicketStatus();
        String action = (ticketStatus.getDescActsol() == null || ticketStatus.getDescActsol().isEmpty()) ? "-" : ticketStatus.getDescActsol();
//        String lastSummary = selectCollections.getLastSummaryWorklog(nossaTicketNumber);

        JSONObject reqObj = new JSONObject();
        JSONObject updateTicketRequest = new JSONObject();
        JSONObject eaiHeader = new JSONObject();
        JSONObject eaiBody = new JSONObject();

        eaiHeader.put("externalId", "");
        eaiHeader.put("timestamp", "");
        updateTicketRequest.put("eaiHeader", eaiHeader);

        eaiBody.put("summary", summaryTicket);
        eaiBody.put("nossaTerritory", nossaTerritory);
        eaiBody.put("nossaTicketNumber", nossaTicketNumber);
        eaiBody.put("rootCause", rootCause);
        eaiBody.put("nossaStatusTicket", nossaStatusTicket);
        eaiBody.put("action", action);
        eaiBody.put("worklog", summary);
        updateTicketRequest.put("eaiBody", eaiBody);

        reqObj.put("updateTicketRequest", updateTicketRequest);

        RequestBody body = RequestBody.create(JSON, reqObj.toString());

        String response = callApi.handlePutRequest(apiConf, body, token);
        info.Log(this.getClass().getName(), "response update inap === " + response);
        JSONObject resObj = new JSONObject(response);

        lhDao.SENDHISTORY(
                ticketStatus.getIdTicket(),
                "update_ticket_telkom_inap",
                apiConf.getUrl(),
                "PUT",
                reqObj,
                resObj,
                200);

    }

    public void updateWorklogOGD(String ticketId, String worklog, String logType) throws Exception, SQLException {
        info.Log(this.getClass().getName(), "Masuk updateWorklogOGD");
        ApiConfig apiConf = new ApiConfig();
        MasterParamDao paramDao = new MasterParamDao();
        CallRestAPI callApi = new CallRestAPI();
        LogHistoryDao lhDao = new LogHistoryDao();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime = now.format(formatter);

        apiConf = paramDao.getUrlapi("updateWorklogOgd");
        String token = callApi.getToken();
        String url = apiConf.getUrl();

        JSONObject insertWorklog = new JSONObject();
        JSONObject req = new JSONObject();

        req.put("no_tiket", ticketId);
        req.put("date_worklog", formattedDateTime);
        req.put("worklog", worklog);
        req.put("logtype", logType);
        insertWorklog.put("insertWorklog", req);

        RequestBody body = RequestBody.create(JSON, insertWorklog.toString());

        String response = callApi.sendPostIbooster(apiConf, body, token);
        JSONObject resObj = new JSONObject(response);

        lhDao.SENDHISTORY(
                ticketId,
                "updateWorklogOGD",
                url,
                response,
                insertWorklog,
                resObj,
                200);
    }

    public void updateCallbackToMyihx(List<String> serviceId, Ticket ts, String token) throws Exception, SQLException {
        ApiConfig apiConf = new ApiConfig();
        MasterParamDao paramDao = new MasterParamDao();
        CallRestAPI callApi = new CallRestAPI();
        LogHistoryDao lhDao = new LogHistoryDao();

        apiConf = paramDao.getUrlapi("CallbackNotificationGamas");
        String url = apiConf.getUrl();

        org.json.JSONObject json = new org.json.JSONObject();

        json.put("ticketNumber", ts.getIdTicket());
        json.put("type", "Gangguan Jaringan");
        json.put("doneEstimation", ts.getEstimation());
        json.put("affectedIHNumber", serviceId);

        if ("62".equals(ts.getChannel())) {
            json.put("channel", ts.getChannel());
        } else {
            json.put("channel", "gamas");
        }

        RequestBody body = RequestBody.create(JSON, json.toString());
        info.Log(this.getClass().getName(), "req callback : " + json.toString());
        info.Log(this.getClass().getName(), "token callback : " + token);

        String response = callApi.sendPostIbooster(apiConf, body, token);
        JSONObject resObj = new JSONObject(response);

        lhDao.SENDHISTORY(
                ts.getIdTicket(),
                "retry - CallbackNotificationGamas",
                url,
                response,
                json,
                resObj,
                0);
    }

    public void workLogMBravo(String ticketId, String externalId, String lati, String longi, String summary, String createdBy) throws Exception, SQLException {
//        try {
//        info.Log(getClass().getName(), "externalId = " + externalId);
//        LogUtil.info(this.getClass().getName(), "MASUK MBRAVO WORKLOGS kk");
//        final String updatedBy = "INSERA";
//        ApiConfig apiConf = new ApiConfig();
//        MasterParamDao paramDao = new MasterParamDao();
//        MasterParam mParam = new MasterParam();
//        CallRestAPI callApi = new CallRestAPI();
//        LogHistoryDao lhDao = new LogHistoryDao();
//        RESTAPI _RESTAPI = new RESTAPI();
//
////            mParam = paramDao.getUrl("addWorklogMBravo");
//        apiConf = paramDao.getUrlapi("addWorklogMBravo");
//        String token = callApi.getToken();
//        String url = mParam.getUrl();
//
//        JSONObject addWorklogRequest = new JSONObject();
//        JSONObject eaiHeader = new JSONObject();
//        JSONObject eaiBody = new JSONObject();
//        JSONObject req = new JSONObject();
//
//        eaiHeader.put("externalId", "");
//        eaiHeader.put("timestamp", new Timestamp(System.currentTimeMillis()));
//        addWorklogRequest.put("eaiHeader", eaiHeader);
//
//        eaiBody.put("ticketId", externalId);
//        eaiBody.put("longitude", longi);
//        eaiBody.put("latitude", lati);
//        eaiBody.put("longDescr", summary);
//        eaiBody.put("createdBy", createdBy);
//        eaiBody.put("updatedBy", updatedBy);
//        addWorklogRequest.put("eaiBody", eaiBody);
//
//        req.put("addWorklogRequest", addWorklogRequest);
//
//        RequestBody body = RequestBody.create(JSON, req.toString());
////            Request request
////                    = new Request.Builder()
////                            .url(mParam.getUrl())
////                            .addHeader("Authorization", "Bearer " + token)
////                            .post(body)
////                            .build();
//
////            JSONObject res = _RESTAPI.CALLAPIHANDLER(request);
//        String res = callApi.sendPostIbooster(apiConf, body, token);
//        JSONObject response = new JSONObject(res);
//
//        int status = response.getInt("status_code");
//
//        lhDao.SENDHISTORY(
//                ticketId,
//                "addWorklogMBravo",
//                url,
//                "POST",
//                req,
//                response,
//                status);
//        addWorklogRequest = null;
//        eaiHeader = null;
//        eaiBody = null;
//        req = null;
////        } catch (Exception e) {
////            info.Error(getClass().getName(), e.getMessage(), e);
////        }
    }

    public void addWorkLogsMBravo(Ticket ticket, String createdBy, String externalTicketTier3, String summary) throws SQLException, JSONException, Exception {

        ApiConfig apiConf = new ApiConfig();
        MasterParamDao paramDao = new MasterParamDao();
        CallRestAPI callApi = new CallRestAPI();
        LogHistoryDao lhDao = new LogHistoryDao();

        apiConf = paramDao.getUrlapi("addWorklogMBravo");
        String token = callApi.getToken();
        String url = apiConf.getUrl();
        apiConf.setUrl(url);

        JSONObject addWorklogRequest = new JSONObject();
        JSONObject eaiHeader = new JSONObject();
        JSONObject eaiBody = new JSONObject();
        JSONObject req = new JSONObject();

        eaiHeader.put("externalId", "");
        eaiHeader.put("timestamp", new Timestamp(System.currentTimeMillis()));
        addWorklogRequest.put("eaiHeader", eaiHeader);

        eaiBody.put("ticketId", externalTicketTier3);
        eaiBody.put("longitude", "");
        eaiBody.put("latitude", "");
        eaiBody.put("longDescr", summary);
        eaiBody.put("createdBy", createdBy);
        eaiBody.put("updatedBy", "INSERA");
        addWorklogRequest.put("eaiBody", eaiBody);

        req.put("addWorklogRequest", addWorklogRequest);
        RequestBody body = RequestBody.create(JSON, req.toString());

        String response = callApi.sendPostIbooster(apiConf, body, token);
        JSONObject resObj = new JSONObject(response);

        lhDao.SENDHISTORY(
                ticket.getIdTicket(),
                "addWorklogMBravo",
                apiConf.getUrl(),
                "POST",
                req,
                resObj,
                200);

//        RequestBody formBody = 
    }

    public void callApiWithBearerToken(String ticketId, String action, MasterParam masterParam, JSONObject jsonReq, String token) throws IOException, JSONException {
        info.Log(this.getClass().getName(), "SUCCESS CONDITION");
        info.Log(this.getClass().getName(), "URL : " + masterParam.getUrl());
        LogHistoryDao lhDao = new LogHistoryDao();
        String response = "";
        RequestBody body = RequestBody.create(JSON, jsonReq.toString());
        Request request = new Request.Builder()
                .url(masterParam.getUrl())
                .addHeader("Authorization", "Bearer " + token)
                .post(body)
                .build();

        response = CALLAPI(request);
        info.Log(this.getClass().getName(), "RESPONSE : " + response);
        JSONObject res = new JSONObject(response);

        lhDao.SENDHISTORY(
                ticketId,
                action,
                masterParam.getUrl(),
                "POST",
                jsonReq,
                res,
                0);
    }

    public void insertWorklogWFMTA(Ticket ticket, String summary, String createdBy, String detail, String ticketId) throws SQLException, JSONException, Exception {
        LogUtil.info(this.getClass().getName(), "insert worklog wfmta");

        ApiConfig apiConf = new ApiConfig();
        MasterParamDao paramDao = new MasterParamDao();
        CallRestAPI callApi = new CallRestAPI();
        LogHistoryDao lhDao = new LogHistoryDao();

        // Mendapatkan waktu saat ini
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedTimestamp = now.format(formatter);

        apiConf = paramDao.getUrlapi("insert_worklog_wfm_ta");
        String token = callApi.getTokenApigwWsaDev();
        String url = apiConf.getUrl();
        apiConf.setUrl(url);

        String ownerGroup = ticket.getOwnerGroup() != null ? ticket.getOwnerGroup() : "";
        String externalidTA = ticket.getExternalIdTA() != null ? ticket.getExternalIdTA() : "";

        JSONObject reqObj = new JSONObject();
        JSONObject insertWorkLogAssuranceRequest = new JSONObject();
        JSONObject wsaHeader = new JSONObject();
        JSONObject wsaBody = new JSONObject();

        wsaHeader.put("externalId", externalidTA);
        wsaHeader.put("callerID", "TELKOM_WFM");
        wsaHeader.put("timestamp", formattedTimestamp);
        insertWorkLogAssuranceRequest.put("wsaHeader", wsaHeader);

        wsaBody.put("recordKey", ticketId);
        wsaBody.put("class", "INCIDENT");
        wsaBody.put("logType", "AGENTNOTE");
        wsaBody.put("createdBy", createdBy);
        wsaBody.put("ownerGroup", ownerGroup);
        wsaBody.put("summary", summary);
        wsaBody.put("detail", detail);
        insertWorkLogAssuranceRequest.put("wsaBody", wsaBody);

        reqObj.put("insertWorkLogAssuranceRequest", insertWorkLogAssuranceRequest);

        RequestBody body = RequestBody.create(JSON, reqObj.toString());

        String response = callApi.sendPostIbooster(apiConf, body, token);
        JSONObject resObj = new JSONObject(response);

        lhDao.SENDHISTORY(
                ticket.getIdTicket(),
                "insert_worklog_wfm_ta",
                apiConf.getUrl(),
                "POST",
                reqObj,
                resObj,
                200);

//        RequestBody formBody =
    }

    public String getTokenAggregator() {
        JSONObject response = new JSONObject();
        JSONObject reqBody = new JSONObject();
        MasterParam masterParam = new MasterParam();
        MasterParamDao masterParamDao = new MasterParamDao();
        RequestBody requestBody;
        Request request;
        String token = "";
        try {
            masterParam = masterParamDao.getUrl("get_token_aggregator");
            String url = masterParam.getUrl();
            reqBody.put("username", masterParam.getjUsername());
            reqBody.put("password", masterParam.getjPassword());

            requestBody = RequestBody.create(this.JSON, reqBody.toString());
            request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            response = this.CALLAPIHANDLER(request);
            Boolean status = response.getBoolean("status");
            if (status) {
                JSONObject message = new JSONObject(response.getString("msg"));
                token = message.getString("accessToken");
                masterParamDao.updateTokenGetPerangkat(token);
            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
        } finally {
            response = null;
            reqBody = null;
            masterParam = null;
            requestBody = null;
            request = null;
        }
        return token;
    }

    public void updateTicketGamasWfmTa(String ticketId, String ticketIdGamas) throws SQLException, JSONException, Exception {
        LogUtil.info(this.getClass().getName(), "insert worklog wfmta");

        ApiConfig apiConf = new ApiConfig();
        MasterParamDao paramDao = new MasterParamDao();
        CallRestAPI callApi = new CallRestAPI();
        LogHistoryDao lhDao = new LogHistoryDao();
        
        apiConf = paramDao.getUrlapi("update_status_wfm_ta");
        String token = callApi.getTokenApigwWsaDev();
        String url = apiConf.getUrl();
        apiConf.setUrl(url);

        // Mendapatkan waktu saat ini
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedTimestamp = now.format(formatter);

        JSONObject wsaHeader = new JSONObject();
        JSONObject wsaBody = new JSONObject();
        JSONObject updateTicketAssuranceRequest = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject request = new JSONObject();

        wsaHeader.put("externalId", "incident");
        wsaHeader.put("callerID", "TELKOM_WFM");
        wsaHeader.put("timestamp", formattedTimestamp);
        updateTicketAssuranceRequest.put("wsaHeader", wsaHeader);

        data.put("ticketIdGamas", ticketIdGamas);
        wsaBody.put("data", data);
        wsaBody.put("ticketId", ticketId);
        updateTicketAssuranceRequest.put("wsaBody", wsaBody);

        request.put("updateTicketAssuranceRequest", updateTicketAssuranceRequest);

        RequestBody body = RequestBody.create(JSON, request.toString());

        String response = callApi.sendPostIbooster(apiConf, body, token);
        JSONObject resObj = new JSONObject(response);

        lhDao.SENDHISTORY(
                ticketId,
                "update_ticket_gamas_wfm_ta_manual",
                apiConf.getUrl(),
                "POST",
                request,
                resObj,
                200);

//        RequestBody formBody =
    }

}
