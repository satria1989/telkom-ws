/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import id.co.itasoft.telkom.oss.plugin.dao.*;
import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.function.FilterJsonArrObjHandler;
import id.co.itasoft.telkom.oss.plugin.function.GenerateSHA1Handler;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.function.RequestToken;
import id.co.itasoft.telkom.oss.plugin.integrasiBromoGamas.IntegrasiBromoGamasService;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TechnicalDataModel;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import id.co.itasoft.telkom.oss.plugin.model.WhattshapLog;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author itasoft UNTUK CALL API LUAR
 */
public class CallApiNonJogetHandler extends Element implements PluginWebSupport {

    public static String pluginName =
            "Telkom New OSS - Ticket Incident Services - Call API Non Joget";
    LogInfo info = new LogInfo();

    @Override
    public String renderTemplate(FormData fd, Map map) {
        return "";
    }

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getDescription() {
        return pluginName;
    }

    @Override
    public String getLabel() {
        return pluginName;
    }

    @Override
    public String getClassName() {
        return this.getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        JSONArray _JSONARRAY;
        JSONObject _JSONOBJECT = new JSONObject();
        JSONObject _JSONPARAMLIST;
        LogHistoryDao logElastic = new LogHistoryDao();
        UpdateTicketDao updateTicket = new UpdateTicketDao();
        ApiConfig apiConfig;
        LogHistory lh;
        MasterParam mp;
        MasterParamDao mpd;
        CheckOrigin checkOrigin;
        GenerateSHA1Handler generateSHA1Handler;
        RequestToken requestToken;
        LoadTicketDao daoTicket;
        TicketStatus ts;
        RESTAPI _RESTAPI;
        CallRestAPI callRestAPI;
        ConfigurationDao configurationDao = new ConfigurationDao();

        outerLabel:
        try {

            checkOrigin = new CheckOrigin();
//            lhdao = new LogHistoryDao();
            String origin = req.getHeader("Origin");
            String ACAO = req.getHeader("Access-Control-Allow-Origin");
            if (origin == null) {
                origin = ACAO;
            }
            boolean allowOrigin = checkOrigin.checkingOrigin(origin, res);
            boolean max = false;
            if (allowOrigin) {
                boolean ACCESS_API = true;

                apiConfig = new ApiConfig();
                lh = new LogHistory();
                mp = new MasterParam();
                mpd = new MasterParamDao();
                daoTicket = new LoadTicketDao();
                _RESTAPI = new RESTAPI();
                callRestAPI = new CallRestAPI();

                WorkflowUserManager workflowUserManager =
                        (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

                generateSHA1Handler = new GenerateSHA1Handler();
                requestToken = new RequestToken();

                // "==============PLUGIN CALL API NON JOGET====================");
                String URL = (req.getParameter("url") != null) ? req.getParameter("url") : "";
                String PARAM = (req.getParameter("param") != null) ? req.getParameter("param") : "";
                String type = (req.getParameter("type") != null) ? req.getParameter("type") : "";
                String ticket_id = (req.getParameter("ticketId") != null) ? req.getParameter("ticketId") : "";
                String userLogin = workflowUserManager.getCurrentUsername();
                String paramCode = daoTicket.getEmployeeCode(userLogin);
                String idTableTicket = "";

                String action = (req.getParameter("action") != null) ? req.getParameter("action") : "";
                String api_key = "";
                String api_id = "";
                String reqBodyWa = "";

                String[] listType =
                        new String[]{
                            "callscc",
                            "updateChief",
                            "createwo",
                            "updateBookingId",
                            "sendWA",
                            "retryTechnicalData",
                            "loadAssignmentHistory",
                            "getDetailWorkoder",
                            "checkStatusSc"
                        };
                List vowelsList = Arrays.asList(listType);
                boolean typeBool = vowelsList.contains(type);

                if (!workflowUserManager.isCurrentUserAnonymous()) {
                    JSONObject jsonOBJJ, jsonHeader, jsonBody, jsonChild;
                    info.Log(getClassName(), "PARAM :" + PARAM);
                    Object json = new JSONTokener(PARAM).nextValue();
                    jsonOBJJ = (JSONObject) json;
//                    String token = _RESTAPI.getToken();
                    String token = "";
                    if ("getAssignedNte".equalsIgnoreCase(type)) {
                        token = _RESTAPI.getTokenFlexible("get_access_token_apigwdev");
                    } else {
                        token = _RESTAPI.getToken();
                    }
                    String stringResponse = "";
                    JSONObject jsonReq;
                    RequestBody body = null;
                    Request request;
                    String reqAPI = "";
                    Object object = new Object();
                    info.Log(getClassName(), "PARAM ::" + jsonOBJJ.toString());

                    switch (type) {
                        case ("callscc"):
                            mp = mpd.getUrl("get_scc");
                            URL = mp.getUrl();
                            String serviceType = jsonOBJJ.getString("serviceType");

                            String hash = "INFOMEDIA#" + jsonOBJJ.getString("serviceNo") + "#NUSANTARA";
                            jsonHeader = new JSONObject();
                            jsonBody = new JSONObject();
                            jsonChild = new JSONObject();

                            // set jsonHeader
                            jsonChild.put("externalId", "");
                            jsonChild.put("timestamp", new Timestamp(System.currentTimeMillis()));
                            jsonBody.put("eaiHeader", jsonChild);

                            // set jsonBody
                            jsonChild = new JSONObject();
                            jsonChild.put("nd", jsonOBJJ.getString("serviceNo"));
                            jsonChild.put("hash", generateSHA1Handler.sha1(hash));
                            jsonChild.put("ticket", jsonOBJJ.getString("ticketId"));
                            jsonBody.put("eaiBody", jsonChild);

                            jsonOBJJ = new JSONObject();
                            jsonOBJJ.put("apiCloseTickInet_Request", jsonBody);

                            jsonBody = null;
                            jsonHeader = null;
                            jsonChild = null;
                            hash = null;

                            body = RequestBody.create(_RESTAPI.JSON, jsonOBJJ.toString());
                            request =
                                    new Request.Builder()
                                            .url(URL)
                                            .addHeader("Authorization", "Bearer " + token)
                                            .post(body)
                                            .build();

                            JSONObject resScc = _RESTAPI.CALLAPIHANDLER(request);

                            boolean boolCheckScc = resScc.getBoolean("status");
                            int stsCodeScc = resScc.getInt("status_code");

                            if (boolCheckScc) {
                                UpdateTicketDao updTicketDao = new UpdateTicketDao();
                                String timestamp = "";
                                String testResult = "";
                                String close = "";
                                String responseCode = "";
                                String error = "";
                                String msg = resScc.getString("msg");
                                object = new JSONTokener(msg).nextValue();
                                _JSONOBJECT = (JSONObject) object;

                                if (_JSONOBJECT.has("apiCloseTickInet_Response")) {
                                    timestamp = _JSONOBJECT.getJSONObject("apiCloseTickInet_Response")
                                            .getJSONObject("eaiHeader").getString("responseTimestamp");
                                    responseCode = _JSONOBJECT.getJSONObject("apiCloseTickInet_Response")
                                            .getJSONObject("eaiStatus").getString("srcResponseCode");
                                }

                                if (_JSONOBJECT.getJSONObject("apiCloseTickInet_Response")
                                        .has("eaiBody")) {
                                    if (_JSONOBJECT.getJSONObject("apiCloseTickInet_Response")
                                            .getJSONObject("eaiBody").has("data")) {

                                        close = _JSONOBJECT.getJSONObject("apiCloseTickInet_Response")
                                                .getJSONObject("eaiBody").getJSONObject("data")
                                                .getString("close");
                                        if ("1".equals(close)) {
                                            testResult = "PASSED";
                                        } else {
                                            testResult = "FAILED";
                                        }
                                    }
                                    if (_JSONOBJECT.getJSONObject("apiCloseTickInet_Response")
                                            .getJSONObject("eaiBody").has("error")) {
                                        error = _JSONOBJECT.getJSONObject("apiCloseTickInet_Response")
                                                .getJSONObject("eaiBody").getJSONArray("error").getString(0);
                                        if (error.contains("not found")) {
                                            testResult = "NOT FOUND";
                                        }
                                    }
                                } else {
                                    testResult = "NOT FOUND";
                                }

                                updTicketDao.updateSCC(
                                        ticket_id,
                                        timestamp,
                                        serviceType,
                                        testResult,
                                        responseCode
                                );
                                msg = null;

                                _JSONOBJECT.write(res.getWriter());
                            } else {
                                String msgScc = resScc.getString("response_message");
                                res.sendError(stsCodeScc, msgScc);
                            }
                            InsertTicketWorkLogsDao insertTicketWorkLogs = new InsertTicketWorkLogsDao();
                            insertTicketWorkLogs.InsertTicketWorkLogsByParentTicket(
                                    userLogin,
                                    "Check SCC QC Internet Test",
                                    _JSONOBJECT.toString(),
                                    ticket_id
                            );
                            logElastic.SENDHISTORY(
                                    ticket_id,
                                    "CallSCC",
                                    URL,
                                    "POST",
                                    jsonOBJJ,
                                    resScc,
                                    stsCodeScc
                            );
                            resScc = null;
                            break;
                        case ("updateChief"):
                            mp = mpd.getUrl("update_chief");
                            URL = mp.getUrl();
                            api_id = mp.getApi_id();
                            api_key = mp.getApi_key();

                            body = RequestBody.create(_RESTAPI.JSON, jsonOBJJ.toString());
                            String TICKETID = jsonOBJJ.getString("ticketID");
                            String wonum = jsonOBJJ.getString("wonum");

                            request =
                                    new Request.Builder()
                                            .url(URL)
                                            .addHeader("api_key", api_key)
                                            .addHeader("api_id", api_id)
                                            .post(body)
                                            .build();

                            JSONObject resUpdateChief = _RESTAPI.CALLAPIHANDLER(request);
                            boolean boolCheckChief = resUpdateChief.getBoolean("status");
                            int stsCheckChief = resUpdateChief.getInt("status_code");
                            if (boolCheckChief) {
                                String msg = resUpdateChief.getString("msg");
                                object = new JSONTokener(msg).nextValue();
                                _JSONOBJECT = (JSONObject) object;
                                _JSONOBJECT.write(res.getWriter());
                                lh.setResponse(reqAPI);
                                msg = null;

                                // INSERT WORKLOG JIKA BERHASIL UPDATE CHIEF
                                StringBuilder detailLog = new StringBuilder();

                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                String timeStamp = df.format(new Date());

                                detailLog
                                        .append(" Reassigned by HD, ")
                                        .append(" Assigned Date: " + timeStamp + ",")
                                        .append(" Technician : from " + jsonOBJJ.getString("last_chief"))
                                        .append(" to " + jsonOBJJ.getString("chief"));

                                InsertTicketWorkLogsDao insertTicketWorkLogsDao = new InsertTicketWorkLogsDao();
                                insertTicketWorkLogsDao.
                                        InsertTicketWorkLogsByParentTicket(
                                                userLogin,
                                                "Update Chief",
                                                detailLog.toString(),
                                                TICKETID
                                        );
                                /*
                                    Fitur Integrasi Bromo Gamas
                                 */
//                                SelectCollections querySelect = new SelectCollections();
//                                Ticket t = new Ticket();
//                                JSONObject reqIntegrasiBromo = new JSONObject();
//
//                                t = querySelect.reqDataBromoGamas(TICKETID);
//                                IntegrasiBromoGamasService ibgs = new IntegrasiBromoGamasService();
//                                reqIntegrasiBromo = ibgs.integrasiBromoGamas(TICKETID, t);
//                                mp = mpd.getUrl("IntegrasiBromoGamas");
//                                URL = mp.getUrl();
//                                body = RequestBody.create(_RESTAPI.JSON, reqIntegrasiBromo.toString());
//                                request
//                                        = new Request.Builder()
//                                                .url(URL)
//                                                .addHeader("Authorization", "Bearer " + token)
//                                                .post(body)
//                                                .build();
//
//                                JSONObject resIntegrasiBromo = _RESTAPI.CALLAPIHANDLER(request);
//                                boolean boolIntgerasiBromo = resUpdateChief.getBoolean("status");
//                                int stsIntegrasiBromo = resUpdateChief.getInt("status_code");
//
//                                logElastic.SENDHISTORY(
//                                        ticket_id,
//                                        "IntegrasiBromo",
//                                        URL,
//                                        "POST",
//                                        reqIntegrasiBromo,
//                                        resIntegrasiBromo,
//                                        stsIntegrasiBromo
//                                );

                            } else {
                                String msgScc = resUpdateChief.getString("response_message");
                                lh.setResponse(msgScc);
                                res.sendError(stsCheckChief, msgScc);
                            }
                            resUpdateChief = null;

                            lh.setCreatedBy(paramCode);
                            lh.setUrl(URL);
                            lh.setMethod("POST");
                            lh.setTicketId(ticket_id);
                            lh.setRequest(jsonOBJJ.toString());
                            lh.setAction("Update Chief");

                            break;
                        case ("createwo"):
                            mp = mpd.getUrl("createWorkOrder");
                            URL = mp.getUrl();
                            api_id = mp.getApi_id();
                            api_key = mp.getApi_key();
                            String ticketStatus = jsonOBJJ.getString("ticketStatus");
                            String idTicket = jsonOBJJ.getString("externalId");
                            TicketStatus getStatus = daoTicket.LoadTicketByIdTicket(idTicket);

                            if (!ticketStatus.equalsIgnoreCase(getStatus.getStatusCurrent())) {
                                res.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Status is different");
                                break outerLabel;
                            }
                            body = RequestBody.create(_RESTAPI.JSON, jsonOBJJ.toString());
                            request =
                                    new Request.Builder()
                                            .url(URL)
                                            .addHeader("api_key", api_key)
                                            .addHeader("api_id", api_id)
                                            .post(body)
                                            .build();

                            JSONObject jsonWO = _RESTAPI.CALLAPIHANDLER(request);
                            boolean boolWO = jsonWO.getBoolean("status");
                            int stsWo = jsonWO.getInt("status_code");
                            if (boolWO) {
                                String msg = jsonWO.getString("msg");
                                object = new JSONTokener(msg).nextValue();
                                _JSONOBJECT = (JSONObject) object;
                                _JSONOBJECT.write(res.getWriter());
                                lh.setAction(
                                        "createWOManual(" + _JSONOBJECT.getJSONObject("data").getString("wonum") + ")");
                                msg = null;
                            } else {
                                lh.setAction("createWOManual(" + "Error Response" + ")");
                                String msgScc = jsonWO.getString("response_message");
                                lh.setResponse(msgScc);
                                res.sendError(stsWo, msgScc);
                            }

                            WorkOrderDao woDao = new WorkOrderDao();
                            logElastic.SENDHISTORY(
                                    ticket_id,
                                    "createWoManual",
                                    URL,
                                    "POST",
                                    jsonOBJJ,
                                    jsonWO,
                                    stsWo
                            );
                            boolean updateFinalcheck = woDao.UpdateFinalcheck(ticket_id); // update finalcheck

                            break;
                        case ("updateBookingId"):
                            // DIGANTI LOGIC DENGAN UPDATE LANGSUNG KE DB
                            String bkDate = (req.getParameter("booking_date") != null) ?
                                    req.getParameter("booking_date") : "";
                            if (!bkDate.isEmpty()) {
                                DateFormat fmtDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = fmtDate.parse(bkDate);
                                Timestamp bookingDate = new Timestamp(date.getTime());
                                daoTicket.updateBookingDateByTicket(bookingDate, ticket_id);
                                res.setStatus(HttpServletResponse.SC_OK);
                            } else {
                                res.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Booking date is empty");
                            }
                            break;
                        case ("sendWA"):
                            if (workflowUserManager.isCurrentUserInRole("ROLE_ADMIN")) {
                                String referer = req.getHeader("Referer");
                                String id = referer.substring(referer.indexOf("id=") + 3, referer.length());

                                WhattshapLog whattshapLog = new WhattshapLog();
                                whattshapLog = mpd.getReqSendWa(id);
                                reqBodyWa = whattshapLog.getRequest();
                                int counterRetryWa =
                                        Integer.valueOf(
                                                mpd.getCounterRetryWa(
                                                        whattshapLog.getTicketId(), whattshapLog.getAction()));
                                action = whattshapLog.getAction();
                                if (counterRetryWa < 4) {
                                    mp = mpd.getUrl("notificationWA");
                                    URL = mp.getUrl();
                                    api_id = mp.getApi_id();
                                    api_key = mp.getApi_key();
                                    body = RequestBody.create(_RESTAPI.JSON, reqBodyWa);
                                    request =
                                            new Request.Builder()
                                                    .url(URL)
                                                    .addHeader("Authorization", "Bearer " + token)
                                                    .post(body)
                                                    .build();

                                    reqAPI = _RESTAPI.CALLAPI(request); // CALL SERVICE
                                    object = new JSONTokener(reqAPI).nextValue();

                                    _JSONOBJECT = (JSONObject) object;

                                    lh.setCreatedBy(paramCode);
                                    lh.setUrl(URL);
                                    lh.setMethod("POST");
                                    lh.setTicketId(ticket_id);
                                    lh.setRequest(reqBodyWa);
                                    lh.setResponse(reqAPI);
                                    lh.setAction(action);
//                                    lhdao.insertToLogHistoryWA(lh);
                                    _JSONOBJECT.write(res.getWriter());
                                } else {
                                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Wa has exceeded");
                                }
                            } else {
                                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                            }
                            break;
                        case ("retryTechnicalData"):
                            //sync datek dulu sebelum call technical data baru
                            LoadTicketDao loadTicketDao = new LoadTicketDao();
                            TicketStatus getTicketById = loadTicketDao.LoadTicketByIdTicket(ticket_id);
                            MasterParam syncTechnicalData = mpd.getUrl("syncTechnicalData");
                            JSONObject syncDatekReq = new JSONObject();
                            JSONObject resSyncDatek = new JSONObject();
                            String tokenAggregator = mpd.getTokenFromMstApi("get_token_aggregator");
                            info.Log(this.getClassName(), "token from mst api : " + tokenAggregator);
                            RequestBody requestBodySyncDatek;
                            Request requestSyncDatek;
                            syncDatekReq.put("service_id", getTicketById.getServiceNo());
                            info.Log(this.getClassName(), "request sync datek == " + syncDatekReq.toString());
                            requestBodySyncDatek = RequestBody.create(_RESTAPI.JSON, syncDatekReq.toString());

                            requestSyncDatek = new Request.Builder()
                                    .url(syncTechnicalData.getUrl())
                                    .addHeader("Authorization", "Bearer " + tokenAggregator)
                                    .post(requestBodySyncDatek)
                                    .build();

                            resSyncDatek = _RESTAPI.CALLAPIHANDLER(requestSyncDatek);
                            if (resSyncDatek.has("response_message")) {
                                String responseMessage = (resSyncDatek.getString("response_message") != null) ? resSyncDatek.getString("response_message") : "";
                                if (responseMessage.equalsIgnoreCase("Unauthorized")) {
                                    tokenAggregator = _RESTAPI.getTokenAggregator();
                                    info.Log(this.getClassName(), "unauthorized dapat token baru :" + tokenAggregator);
                                    requestBodySyncDatek = RequestBody.create(_RESTAPI.JSON, syncDatekReq.toString());

                                    requestSyncDatek = new Request.Builder()
                                            .url(syncTechnicalData.getUrl())
                                            .addHeader("Authorization", "Bearer " + tokenAggregator)
                                            .post(requestBodySyncDatek)
                                            .build();

                                    resSyncDatek = _RESTAPI.CALLAPIHANDLER(requestSyncDatek);
                                }
                            }
//                            int stsSyncDatek = resSyncDatek.getInt("status_code");
//                            boolean statusSyncDatek = resSyncDatek.getBoolean("status");
                            boolean statusSyncDatek = true;

                            info.Log(this.getClassName(), "response syncdatek == " + resSyncDatek.toString());

                            if (statusSyncDatek) {
//                                TimeUnit.SECONDS.sleep(5);
                                JSONObject resApi = new JSONObject();
                                String serviceNumber = (getTicketById.getServiceNo() == null ? "" : getTicketById.getServiceNo());
                                String serviceId = (getTicketById.getServiceId() == null ? "" : getTicketById.getServiceId());
                                if (!"".equals(serviceNumber)) {
//
                                    idTableTicket = (jsonOBJJ.has("recordId")) ? jsonOBJJ.getString("recordId") : "";
                                    ticket_id = (jsonOBJJ.has("ticketId")) ? jsonOBJJ.getString("ticketId") : "";
                                    info.Log(getClassName(), "idTableTicket :" + idTableTicket);
                                    info.Log(getClassName(), "ticket_id :" + ticket_id);
//                                params untuk request lama
                                    String params = (jsonOBJJ.has("param")) ? jsonOBJJ.getJSONObject("param").toString() : "";
                                    info.Log(getClassName(), "idTableTicket :" + idTableTicket);
                                    info.Log(getClassName(), "ticket_id :" + ticket_id);
                                    info.Log(getClassName(), "params :" + params);

                                    lh.setCreatedBy(paramCode);
                                    lh.setUrl(URL);
                                    lh.setMethod("POST");
                                    lh.setTicketId(ticket_id);
                                    lh.setRequest(PARAM);
                                    lh.setAction("RETRY TECHNICAL DATA");

                                    String resDatekStatus = "";
                                    String resDatekMessage = "";

                                    TicketAutomationDaoV4 ticketAutomationDaoV4 = new TicketAutomationDaoV4();
                                    JSONObject resTechnicalDataCustom = ticketAutomationDaoV4.getTechnicalData(ticket_id, serviceNumber, serviceId, idTableTicket, "RELOAD");
                                    info.Log(this.getClassName(), "res datek custom == " + resTechnicalDataCustom);
                                    boolean boolTechData = false;
                                    if (resTechnicalDataCustom.has("status")) {
                                        boolTechData = resTechnicalDataCustom.getBoolean("status");
                                    }
                                    if (boolTechData) {

                                        TechnicalDataDao technicalDataHandlerDao = new TechnicalDataDao();
//                                        technicalDataHandlerDao.deleteTechnicalData(idTableTicket);
//                                        technicalDataHandlerDao.insertBatchOutTechnicalData(outTechnicalDataList);
                                        technicalDataHandlerDao.updateTicketFromTechnicalData(ticket_id, resTechnicalDataCustom);

                                        // UPDATE TECHNOLOGY TO OTHER SERVICE
                                        mp = mpd.getUrl("UpdateTechToWo");

                                        String ticketID = (ticket_id == null) ? "" : ticket_id;
                                        String techno = "";
                                        if (resTechnicalDataCustom.has("TECHNOLOGY")) {
                                            techno = (resTechnicalDataCustom.getString("TECHNOLOGY") == null) ? "" : resTechnicalDataCustom.getString("TECHNOLOGY");
                                        }

                                        lh = new LogHistory();
                                        lh.setAction("UPDATE TECHNOLOGY(" + ticketID + ")");
                                        lh.setCreatedBy("SYSTEM");
                                        lh.setMethod("POST");
                                        lh.setTicketId(ticketID);
                                        lh.setUrl(mp.getUrl());

                                        JSONObject jsonWo = new JSONObject();
                                        jsonWo.put("action", "UpdateTech");
                                        jsonWo.put("externalId", ticketID);
                                        jsonWo.put("changeBy", "SYSTEM");
                                        jsonWo.put("technologyType", techno);
                                        lh.setRequest(jsonWo.toString());

                                        body = RequestBody.create(_RESTAPI.JSON, jsonWo.toString());
                                        request =
                                                new Request.Builder()
                                                        .url(mp.getUrl())
                                                        .addHeader("api_key", mp.getApi_key()) // add request headers
                                                        .addHeader("api_id", mp.getApi_id())
                                                        .addHeader("Origin", "https://oss-incident.telkom.co.id")
                                                        .post(body)
                                                        .build();

                                        JSONObject requestUpdate = _RESTAPI.CALLAPIHANDLER(request);
                                        info.Log(getClassName(), "response techdata: " + requestUpdate);
                                        lh.setResponse(requestUpdate.toString());

                                        resApi.put("status", "Success");
                                        resApi.put("message", "Data Updated");
                                        resApi.write(res.getWriter());

//                                        logElastic.SENDHISTORY(
//                                                ticket_id,
//                                                "loadDate",
//                                                URL,
//                                                "POST",
//                                                getDataTeknisRequest,
//                                                jsonTechData,
//                                                stsTechData);
                                    } else {
                                        resApi.put("status", "Failed");
                                        resApi.put("message", "failed to reload technical data");
                                        resApi.write(res.getWriter());
                                    }
//                                    else {
//                                        String msgScc = jsonTechData.getString("response_message");
//                                        res.sendError(stsTechData, msgScc);
//                                        lh.setResponse(msgScc);
//                                    }
                                } else {
                                    resApi.put("status", "Failed");
                                    resApi.put("message", "The service number on the ticket is empty, please fill it in first");
                                    resApi.write(res.getWriter());
                                }
                            }
                            break;
                        case "loadAssignmentHistory":

                            info.Log(getClassName(), "Ticket ID CALL LOAD ASSIGNMENTHISTORY ::" + ticket_id);

                            JSONObject reqAssignmentHistory = new JSONObject();
                            reqAssignmentHistory.put("ticketID", ticket_id);

                            mp = mpd.getUrl("loadAssignmentHistory");
                            body = RequestBody.create(_RESTAPI.JSON, reqAssignmentHistory.toString());
                            request = new Request.Builder()
                                    .url(mp.getUrl())
                                    .addHeader("api_key", mp.getApi_key()) // add request headers
                                    .addHeader("api_id", mp.getApi_id())
                                    .addHeader("Origin", "https://oss-incident.telkom.co.id")
                                    .post(body)
                                    .build();

                            JSONObject resultAssignmentHistory = _RESTAPI.CALLAPIHANDLER(request);
                            boolean boolAH = resultAssignmentHistory.getBoolean("status");
                            int stsAH = resultAssignmentHistory.getInt("status_code");
                            if (boolAH) {
                                String msg = resultAssignmentHistory.getString("msg");
                                object = new JSONTokener(msg).nextValue();
                                _JSONOBJECT = (JSONObject) object;
                                _JSONOBJECT.write(res.getWriter());
                            } else {
                                String msgScc = resultAssignmentHistory.getString("response_message");
                                res.sendError(stsAH, msgScc);
                            }

                            break;
                        case "getDetailWorkoder":

                            JSONObject detailWoJsonReq = new JSONObject();
                            detailWoJsonReq.put("action", "GetWorkorder");
                            detailWoJsonReq.put("externalId", ticket_id);

                            mp = mpd.getUrl("list_work_order");
                            body = RequestBody.create(_RESTAPI.JSON, detailWoJsonReq.toString());
                            request = new Request.Builder()
                                    .url(mp.getUrl())
                                    .addHeader("api_key", mp.getApi_key()) // add request headers
                                    .addHeader("api_id", mp.getApi_id())
                                    .addHeader("Origin", "https://oss-incident.telkom.co.id")
                                    .post(body)
                                    .build();

                            JSONObject resDetailWo = _RESTAPI.CALLAPIHANDLER(request);
                            boolean boolDetailWo = resDetailWo.getBoolean("status");
                            int scDWO = resDetailWo.getInt("status_code");
                            if (boolDetailWo) {
                                String msg = resDetailWo.getString("msg");
                                object = new JSONTokener(msg).nextValue();
                                _JSONOBJECT = (JSONObject) object;
                                _JSONOBJECT.write(res.getWriter());
                            } else {
                                String msgScc = resDetailWo.getString("response_message");
                                res.sendError(scDWO, msgScc);
                            }

                            break;
                        case "checkStatusSc":
                            mp = mpd.getUrl("check_status_sc");
                            URL = mp.getUrl();
                            String scId = jsonOBJJ.getString("scid");

                            jsonBody = new JSONObject();
                            jsonChild = new JSONObject();

                            jsonChild.put("scId", scId);
                            jsonBody.put("data", jsonChild);
                            jsonBody.put("code", "0");
                            jsonBody.put("guid", "0");

                            body = RequestBody.create(_RESTAPI.JSON, jsonBody.toString());
                            request =
                                    new Request.Builder()
                                            .url(URL)
                                            .addHeader("Authorization", "Bearer " + token)
                                            .post(body)
                                            .build();
                            JSONObject response = _RESTAPI.CALLAPIHANDLER(request);
                            boolean boolCheckStatusSc = response.getBoolean("status");
                            int stsCodeStatusSc = response.getInt("status_code");
                            if (boolCheckStatusSc) {
                                String msg = response.getString("msg");
                                object = new JSONTokener(msg).nextValue();
                                _JSONOBJECT = (JSONObject) object;
                                _JSONOBJECT.write(res.getWriter());
                                String returnMessage = _JSONOBJECT.getString("returnMessage");
                                String scStatus = "";
                                if ("found".equals(returnMessage)) {
                                    scStatus = _JSONOBJECT.getJSONObject("data").getString("statusCode") +
                                            " " + _JSONOBJECT.getJSONObject("data").getString("statusName");

                                } else {
                                    scStatus = "NA NA";
                                }
                                UpdateTicketDao updateTicketDao = new UpdateTicketDao();
                                updateTicketDao.updateScStatus(ticket_id, scStatus);

                                InsertTicketWorkLogsDao insertTicketWorkLogsDao = new InsertTicketWorkLogsDao();
                                insertTicketWorkLogsDao.InsertTicketWorkLogsByParentTicket(
                                        userLogin,
                                        "Ukur SC Status",
                                        _JSONOBJECT.toString(),
                                        ticket_id
                                );

                                logElastic.SENDHISTORY(
                                        ticket_id,
                                        "checkStatusSc",
                                        URL,
                                        "POST",
                                        jsonBody,
                                        response,
                                        stsCodeStatusSc
                                );

                                msg = null;
                            } else {
                                String msgScc = response.getString("response_message");
                                res.sendError(stsCodeStatusSc, msgScc);
                            }
                            resScc = null;
                            break;
                        case "reconfigONT":
                            mp = mpd.getUrl("reconfigONT");
                            URL = mp.getUrl();

                            JSONObject reconfigONTRequest = new JSONObject(PARAM);
//                            info.Log(this.getClass().getName(), "PARAM reconfigONT : " + PARAM);

                            body = RequestBody.create(_RESTAPI.JSON, reconfigONTRequest.toString());

                            request =
                                    new Request.Builder()
                                            .url(URL)
                                            .addHeader("Authorization", "Bearer " + token)
                                            .post(body)
                                            .build();
                            JSONObject reconfigONTResponse = _RESTAPI.CALLAPIHANDLER(request);
                            reconfigONTResponse.write(res.getWriter());
                            int stsreconfigONTResponse = reconfigONTResponse.getInt("status_code");
                            logElastic.SENDHISTORY(
                                    ticket_id,
                                    "reconfigONT",
                                    URL,
                                    "POST",
                                    reconfigONTRequest,
                                    reconfigONTResponse,
                                    stsreconfigONTResponse);
                            break;
                        case "replaceONT":
                            mp = mpd.getUrl("replaceONT");
                            URL = mp.getUrl();

                            ObjectMapper objectMapper = new ObjectMapper();

                            JSONObject paramReplace = new JSONObject(PARAM);
                            JsonNode jsonNodeReolace = objectMapper.readTree(PARAM);
                            ObjectNode objNodeReplace = (ObjectNode) jsonNodeReolace;
                            objNodeReplace.remove("technician");
                            PARAM = objectMapper.writeValueAsString(objNodeReplace);

                            JSONObject reqReplace = new JSONObject(PARAM);
                            JSONObject technicianReplace = paramReplace.getJSONObject("technician");
                            JSONObject replaceONTRequest = reqReplace.getJSONObject("replaceONTRequest");
                            JSONObject bodyReplace = replaceONTRequest.getJSONObject("eaiBody");

                            String oldOnuSn = bodyReplace.getString("old_onu_sn");
                            String newOnuSn = bodyReplace.getString("new_onu_sn");
                            String newOnuType = bodyReplace.getString("new_onu_type");
                            String techCode = technicianReplace.getString("technician_code");
                            String techName = technicianReplace.getString("technician_name");

                            body = RequestBody.create(_RESTAPI.JSON, reqReplace.toString());

                            request =
                                    new Request.Builder()
                                            .url(URL)
                                            .addHeader("Authorization", "Bearer " + token)
                                            .post(body)
                                            .build();
                            JSONObject replaceONTResponse = _RESTAPI.CALLAPIHANDLER(request);

                            JSONObject jsonResReplace = new JSONObject(replaceONTResponse.get("msg").toString());

                            String responseMsgReplace = "";
                            String srcResponseCodeReplace = "";
                            String resInfoReplace = "";

                            if (jsonResReplace.has("replaceONTResponse")) {
                                JSONObject replaceONTRes = jsonResReplace.getJSONObject("replaceONTResponse");
                                if (replaceONTRes.has("eaiStatus")) {
                                    JSONObject eaiStatus = replaceONTRes.getJSONObject("eaiStatus");
                                    responseMsgReplace = eaiStatus.getString("responseMsg");
                                    srcResponseCodeReplace = eaiStatus.getString("srcResponseCode");
                                }
                                if (replaceONTRes.has("eaiBody")) {
                                    JSONObject eaiBody = replaceONTRes.getJSONObject("eaiBody");
                                    resInfoReplace = eaiBody.getString("info");
                                }
                            }
                            if ("SUCCESS".equalsIgnoreCase(responseMsgReplace) && "200".equals(srcResponseCodeReplace) && "berhasil".equalsIgnoreCase(resInfoReplace)) {
                                UpdateProgressConfiguration upc = new UpdateProgressConfiguration();
                                SelectCollections select = new SelectCollections();
                                Ticket ticket = new Ticket();
                                ticket = select.getDataTicket(ticket_id);
                                updateTicket.updateReplaceONT(newOnuSn, newOnuType, techCode, techName, ticket_id);
                                if ("PL-TSEL".equalsIgnoreCase(ticket.getCust_segment())) {
                                    upc.updateOntInventoryPlTsel(ticket_id, ticket, oldOnuSn, newOnuSn, techCode);
                                } else {
                                    upc.updateOntInventoryNonPlTsel(ticket_id, ticket, oldOnuSn, newOnuSn, techCode);
                                }

                            }

                            replaceONTResponse.write(res.getWriter());
                            int stsreplaceONTResponse = replaceONTResponse.getInt("status_code");
                            logElastic.SENDHISTORY(
                                    ticket_id,
                                    "replaceONT",
                                    URL,
                                    "POST",
                                    reqReplace,
                                    replaceONTResponse,
                                    stsreplaceONTResponse);
                            break;

                        case "integrateToKominfo":
                            lh = new LogHistory();
                            mp = mpd.getUrl("integrateToKominfo");
                            URL = mp.getUrl();

                            info.Log(getClass().getName(), "PARAM KOMINFO : " + PARAM);

                            JSONObject apiAlarmRequest = new JSONObject(PARAM);

                            body = RequestBody.create(_RESTAPI.JSON, apiAlarmRequest.toString());

                            request = new Request.Builder()
                                    .url(URL)
                                    .addHeader("Authorization", "Bearer " + token)
                                    .post(body)
                                    .build();

                            JSONObject msgResponse = _RESTAPI.CALLAPIHANDLER(request);
                            JSONObject apiAlarmResponse = new JSONObject(msgResponse.get("msg").toString());

                            msgResponse.write(res.getWriter());
                            boolean status = msgResponse.getBoolean("status");
                            int stsCodeintegrasiResponse = msgResponse.getInt("status_code");
                            if (200 == stsCodeintegrasiResponse) {
                                updateTicket.updateIntegrateKominfo(ticket_id);
                                info.Log(getClass().getName(), "update status" + updateTicket.updateIntegrateKominfo(ticket_id));
                            }
                            lh.setRequest(apiAlarmRequest.toString());
                            lh.setMethod("POST");
                            lh.setResponse(apiAlarmResponse.toString());
                            lh.setAction("integrateToKominfo");
                            lh.setUrl(URL);
                            lh.setTicketId(ticket_id);
                            lh.setStatus(String.valueOf(status));
                            logElastic.insertToLog(lh);
                            logElastic.SENDHISTORY(
                                    ticket_id,
                                    "integrateToKominfo",
                                    URL,
                                    "POST",
                                    apiAlarmRequest,
                                    msgResponse,
                                    stsCodeintegrasiResponse);
                            break;
                        case "reloadCustSegement":
                            String tokenProd = callRestAPI.getTokenApigwsit();
                            String custSegment = "";
                            lh = new LogHistory();
                            mp = mpd.getUrl("Pelanggan2Request");
                            URL = mp.getUrl();

                            info.Log(getClass().getName(), "PARAM reloadCustSegement : " + PARAM);

                            JSONObject getPelanggan2Request = new JSONObject(PARAM);

                            body = RequestBody.create(_RESTAPI.JSON, getPelanggan2Request.toString());

                            request = new Request.Builder()
                                    .url(URL)
                                    .addHeader("Authorization", "Bearer " + tokenProd)
                                    .post(body)
                                    .build();

                            JSONObject msgRes = _RESTAPI.CALLAPIHANDLER(request);
                            JSONObject msg = new JSONObject(msgRes.get("msg").toString());

                            msgRes.write(res.getWriter());
                            int stsCodeGetBud = msgRes.getInt("status_code");
                            if (200 == stsCodeGetBud) {
                                if (msg.has("getPelanggan2Response")) {
                                    JSONObject getPelanggan2Response = new JSONObject(msg.get("getPelanggan2Response").toString());
                                    if (getPelanggan2Response.has("eaiBody")) {
                                        org.json.JSONObject eaiBodyres = new org.json.JSONObject(getPelanggan2Response.get("eaiBody").toString());
                                        if (eaiBodyres.has("cabasaDetails")) {
                                            org.json.JSONObject cabasaDetails = new org.json.JSONObject(eaiBodyres.get("cabasaDetails").toString());
                                            custSegment = (cabasaDetails.getString("caLGEST") == null) ?
                                                    "" : cabasaDetails.getString("caLGEST");
                                            String[] listCstSegment = {"DPS", "DSS", "DES", "DGS", "DBS", "DCS", "PL-TSEL", "REG", "RBS"};
                                            String fixCstSegment = "";
                                            for (String cs : listCstSegment) {
                                                if (custSegment.contains(cs)) {
                                                    fixCstSegment = cs;
                                                    break;
                                                }
                                            }
                                            if (!"".equals(fixCstSegment)) {
                                                custSegment = fixCstSegment;
                                            }
                                            updateTicket.updateCustSegment(custSegment, ticket_id);
                                            info.Log(getClass().getName(), "update status" + updateTicket.updateIntegrateKominfo(ticket_id));
                                        }
                                    }
                                }
                            }
                            logElastic.SENDHISTORY(
                                    ticket_id,
                                    "getCustomerSegment",
                                    URL,
                                    "POST",
                                    getPelanggan2Request,
                                    msgRes,
                                    stsCodeGetBud);
                            break;
                        case "checkConfigONT":
                            mp = mpd.getUrl("checkConfigONT");
                            URL = mp.getUrl();

                            ObjectMapper objMapper = new ObjectMapper();

                            JSONObject paramCheckConfig = new JSONObject(PARAM);

                            JsonNode jsonNodeCheckConfig = objMapper.readTree(PARAM);
                            ObjectNode objNodeCheckConfig = (ObjectNode) jsonNodeCheckConfig;
                            objNodeCheckConfig.remove("actSol");
                            PARAM = objMapper.writeValueAsString(objNodeCheckConfig);

                            JSONObject reqCheckConfig = new JSONObject(PARAM);
                            JSONObject checkConfigONTRequest = reqCheckConfig.getJSONObject("checkConfigONTRequest");
                            JSONObject actsolCheckConfig = paramCheckConfig.getJSONObject("actSol");
                            JSONObject bodyCheckConfig = checkConfigONTRequest.getJSONObject("eaiBody");

                            String keterangan = bodyCheckConfig.getString("keterangan");
                            String actsolCode = actsolCheckConfig.getString("actSolCode");
                            String actsolDesc = actsolCheckConfig.getString("actSolDesc");
                            body = RequestBody.create(_RESTAPI.JSON, reqCheckConfig.toString());

                            request =
                                    new Request.Builder()
                                            .url(URL)
                                            .addHeader("Authorization", "Bearer " + token)
                                            .post(body)
                                            .build();
                            JSONObject checkConfigONTResponse = _RESTAPI.CALLAPIHANDLER(request);

                            JSONObject jsonResCheckConfig = new JSONObject(checkConfigONTResponse.get("msg").toString());

                            String resMsgCheckConfig = "";
                            String responseCodeCheckConfig = "";
                            String resInfoCheckConfig = "";

                            checkConfigONTResponse.write(res.getWriter());
                            int stscheckConfigONTResponse = checkConfigONTResponse.getInt("status_code");
                            if (jsonResCheckConfig.has("checkConfigONTResponse")) {
                                JSONObject checkConfigONTRes = jsonResCheckConfig.getJSONObject("checkConfigONTResponse");
                                if (checkConfigONTRes.has("eaiStatus")) {
                                    JSONObject eaiStatus = checkConfigONTRes.getJSONObject("eaiStatus");
                                    resMsgCheckConfig = eaiStatus.getString("responseMsg");
                                    responseCodeCheckConfig = eaiStatus.getString("srcResponseCode");
                                }
                                if (checkConfigONTRes.has("eaiBody")) {
                                    JSONObject eaiBody = checkConfigONTRes.getJSONObject("eaiBody");
                                    resInfoCheckConfig = eaiBody.getString("info");
                                }
                            }

                            if ("SUCCESS".equalsIgnoreCase(resMsgCheckConfig) && "200".equals(responseCodeCheckConfig) && "berhasil".equalsIgnoreCase(resInfoCheckConfig)) {
                                updateTicket.updateCheckONT(keterangan, actsolCode, actsolDesc, ticket_id);
                            }

                            logElastic.SENDHISTORY(
                                    ticket_id,
                                    "checkConfigONT",
                                    URL,
                                    "POST",
                                    reqCheckConfig,
                                    checkConfigONTResponse,
                                    stscheckConfigONTResponse);
                            break;
                        case "getAssignedNte":
                            mp = mpd.getUrl("replaceONT");
                            URL = mp.getUrl();

                            JSONObject getAssignedNteRequest = new JSONObject(PARAM);

                            body = RequestBody.create(_RESTAPI.JSON, getAssignedNteRequest.toString());

                            request =
                                    new Request.Builder()
                                            .url(URL)
                                            .addHeader("Authorization", "Bearer " + token)
                                            .post(body)
                                            .build();
                            JSONObject getAssignedNteResponse = _RESTAPI.CALLAPIHANDLER(request);

                            getAssignedNteResponse.write(res.getWriter());
                            int stsgetAssignedNteResponse = getAssignedNteResponse.getInt("status_code");
                            logElastic.SENDHISTORY(
                                    ticket_id,
                                    "replaceONT",
                                    URL,
                                    "POST",
                                    getAssignedNteRequest,
                                    getAssignedNteResponse,
                                    stsgetAssignedNteResponse);
                            break;
                        default:
                            res.sendError(HttpServletResponse.SC_BAD_REQUEST);
                            break;
                    }

                } else {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
                }
            } else {
                // Throw 403 status OR send default allow
                res.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "");
            }
        } catch (Exception ex) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Servicer Error");
            info.Error(getClass().getName(), ex.getMessage(), ex);
        } finally {
            _JSONPARAMLIST = null;
//            lhdao = null;
            apiConfig = null;
            lh = null;
            mp = null;
            mpd = null;
            checkOrigin = null;
            generateSHA1Handler = null;
            requestToken = null;
            daoTicket = null;
            ts = null;
            _RESTAPI = null;
        }
    }

    private JSONObject ListScheduleFormatJson(JSONObject json, JSONObject paramList) {
        JSONObject root = new JSONObject();
        FilterJsonArrObjHandler jsonArrObjHandler = new FilterJsonArrObjHandler();

        try {
            JSONArray arr;
            if ("SUCCESS".equalsIgnoreCase(json.getString("message"))) {
                String data = json.getString("data");
                arr = (JSONArray) new JSONTokener(data).nextValue();
                JSONArray filtered = new JSONArray();
                JSONObject filteredItem;

                if (!"".equals(paramList.getString("bookingID"))) {
                    for (int i = 0; i < arr.length(); ++i) {
                        filteredItem = new JSONObject();
                        JSONObject obj = arr.getJSONObject(i);
                        String name = obj.getString("bookingID");
                        if (name.toLowerCase().contains(paramList.getString("bookingID").toLowerCase())) {
                            filteredItem.put("bookingDate", obj.getString("bookingDate"));
                            filteredItem.put("slot", obj.getString("slot"));
                            filteredItem.put("bookingID", obj.getString("bookingID"));
                            filteredItem.put("crew", obj.getString("crew"));
                            filteredItem.put("sto", obj.getString("sto"));
                            filteredItem.put("status", obj.getString("status"));
                            filtered.put(filteredItem);
                        }
                    }
                    arr = filtered;
                }

                JSONArray sorted = jsonArrObjHandler.sortedArray(arr);
                ////                 LogUtil.info(getClassName(), "JSONARR RE:" + sorted);
                arr = sorted;

                root.put("size", arr.length());
                root.put("total", arr.length());
                root.put("data", arr);

                filtered = null;
                arr = null;
                sorted = null;
                filteredItem = null;
            } else {
                arr = new JSONArray();
                JSONObject objData = new JSONObject();
                objData.put("bookingDate", "SDF");
                objData.put("slot", "DSF");
                objData.put("bookingID", "SDF");
                objData.put("crew", "SDF");
                objData.put("sto", "DSF");
                objData.put("status", "DSD");
                objData.put("count", "DSD");
                //                objData.put("date", "DSD");

                arr.put(objData);
                arr = new JSONArray();
                root.put("size", arr.length());
                root.put("total", arr.length());
                root.put("data", arr);

                arr = null;
                objData = null;
            }

        } catch (JSONException ex) {
            info.Error(getClass().getName(), ex.getMessage(), ex);
        } finally {
            jsonArrObjHandler = null;
        }
        return root;
    }

    // FOR DATALIST FORMAT JOGET
    private JSONObject ListTechnicianFormatJson(JSONObject json, JSONObject paramlist) {
        JSONObject root, header, body, status, jsoncalback;
        JSONArray data;
        JSONArray filtered = new JSONArray();
        JSONObject filteredItem;
        jsoncalback = new JSONObject();
        try {
            root = json.getJSONObject("listTechnicianResponse");
            header = root.getJSONObject("eaiHeader");
            status = root.getJSONObject("eaiStatus");
            body = root.getJSONObject("eaiBody");
            data = body.getJSONArray("data");

            if ("success".equalsIgnoreCase(body.getString("info"))) {
                if (!"".equals(paramlist.getString("param_name")) &&
                        !"".equals(paramlist.getString("param_labor"))) {
                    for (int i = 0; i < data.length(); ++i) {
                        filteredItem = new JSONObject();
                        JSONObject obj = data.getJSONObject(i);
                        String name = obj.getString("name");
                        String labor = obj.getString("laborcode");
                        if (name.toLowerCase().contains(paramlist.getString("param_name").toLowerCase()) &&
                                labor.toLowerCase().contains(paramlist.getString("param_labor").toLowerCase())) {
                            filteredItem.put("name", obj.getString("name"));
                            filteredItem.put("laborcode", obj.getString("laborcode"));
                            filteredItem.put("status_name", obj.getString("status_name"));
                            filteredItem.put("verification_completed", obj.getString("verification_completed"));
                            filtered.put(filteredItem);
                        }
                    }
                    data = filtered;
                } else if (!"".equals(paramlist.getString("param_name")) &&
                        "".equals(paramlist.getString("param_labor"))) {
                    for (int i = 0; i < data.length(); ++i) {
                        filteredItem = new JSONObject();
                        JSONObject obj = data.getJSONObject(i);
                        String name = obj.getString("name");
                        String labor = obj.getString("laborcode");
                        if (name.toLowerCase().contains(paramlist.getString("param_name").toLowerCase())) {
                            filteredItem.put("name", obj.getString("name"));
                            filteredItem.put("laborcode", obj.getString("laborcode"));
                            filteredItem.put("status_name", obj.getString("status_name"));
                            filteredItem.put("verification_completed", obj.getString("verification_completed"));
                            filtered.put(filteredItem);
                        }
                    }
                    data = filtered;
                } else if ("".equals(paramlist.getString("param_name")) &&
                        !"".equals(paramlist.getString("param_labor"))) {
                    for (int i = 0; i < data.length(); ++i) {
                        filteredItem = new JSONObject();
                        JSONObject obj = data.getJSONObject(i);
                        String name = obj.getString("name");
                        String labor = obj.getString("laborcode");
                        if (labor.toLowerCase().contains(paramlist.getString("param_labor").toLowerCase())) {
                            filteredItem.put("name", obj.getString("name"));
                            filteredItem.put("laborcode", obj.getString("laborcode"));
                            filteredItem.put("status_name", obj.getString("status_name"));
                            filteredItem.put("verification_completed", obj.getString("verification_completed"));
                            filtered.put(filteredItem);
                        }
                    }
                    data = filtered;
                }

                jsoncalback.put("size", data.length());
                jsoncalback.put("total", data.length());
                jsoncalback.put("data", data);
            }

        } catch (JSONException ex) {

            info.Error(getClass().getName(), ex.getMessage(), ex);
        } finally {
            data = null;
            filtered = null;
            root = null;
            header = null;
            body = null;
            status = null;
        }

        return jsoncalback;
    }
}
