/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.JWebToken;
import id.co.itasoft.telkom.oss.plugin.dao.LoadTicketDao;
import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.function.Crypto;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * @author asani
 */
public class CallAPIWithToken extends Element implements PluginWebSupport {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Call API With Token";
    TicketStatus ts;
    // logInfo // logInfo = new // logInfo();

    @Override
    public String renderTemplate(FormData fd, Map map) {
        return null;
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
        return null;
    }

    Crypto crypto;
    LoadTicketDao daoTicket;
    CheckOrigin checkOrigin;
    JWebToken incomingToken;
    JSONObject paramReferer;
    JSONObject jsonPARAMTAKEOWNER;
    URL url;
    MasterParam mp;
    MasterParamDao mpd;
    Request request;
    RequestBody body;
    JSONArray arr_;
    JSONObject payloadData;
    RESTAPI _RESTAPI;
    LogHistory logHistory;
    LogHistoryDao logHistoryDao;

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        try {
            final String authorizationHeaderValue = req.getHeader("Authorization"); // GET BEARER TOKEN
            WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
            AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");

            _RESTAPI = new RESTAPI();
            crypto = new Crypto();
            daoTicket = new LoadTicketDao();
            checkOrigin = new CheckOrigin();
            ts = new TicketStatus();
            logHistory = new LogHistory();
            logHistoryDao = new LogHistoryDao();

            // GET ON HEADER
            String referrer = req.getHeader("referer");
            String origin = req.getHeader("Origin");
            String ACAO = req.getHeader("Access-Control-Allow-Origin");

            url = new URL(referrer);
            String queryStr = url.getQuery();
            if (origin == null) {
                origin = ACAO;
            }
            boolean allowOrigin = checkOrigin.checkingOrigin(origin, res); // CHECK ORIGIN ACCESS

            if (allowOrigin) {
                // LIST PARAM OR QUERY STRING ON REFERER HEADER
                String[] params = queryStr.split("&");
                paramReferer = new JSONObject();
                for (String param : params) {
                    String key = param.substring(0, param.indexOf('='));
                    String val = param.substring(param.indexOf('=') + 1);
                    paramReferer.put(key, val);
                }
                params = null;

                if (paramReferer.has("id")) {
                    String PARENT_ID = paramReferer.getString("id");
                    if (!workflowUserManager.isCurrentUserAnonymous()) { // IF NOT ANONYMOUS ACCESS
                        if (authorizationHeaderValue != null && authorizationHeaderValue.startsWith("Bearer")) {
                            String tokenD = authorizationHeaderValue.substring(7, authorizationHeaderValue.length());
                            incomingToken = new JWebToken(tokenD);

                            if (incomingToken.isValid()) {
                                payloadData = incomingToken.getPayloadJSONObject(tokenD);
                                arr_ = (JSONArray) payloadData.get("aud");
                                String USERTOKEN = arr_.get(0).toString();

                                if (USERTOKEN.equals(workflowUserManager.getCurrentUsername())) {
                                    mp = new MasterParam();
                                    mpd = new MasterParamDao();
                                    jsonPARAMTAKEOWNER = new JSONObject();

                                    String URL = "";
                                    String PARAM = (req.getParameter("param") != null) ? req.getParameter("param") : "";
                                    String type = (req.getParameter("type") != null) ? req.getParameter("type") : "";
                                    String ticket_id = (req.getParameter("ticketId") != null) ? req.getParameter("ticketId") : "";
                                    String api_key = "";
                                    String api_id = "";

                                    if ("takeOwner".equalsIgnoreCase(type)) {
                                        ts = daoTicket.LoadTicketByParentId(PARENT_ID);
                                    }

                                    JSONObject jsonOBJJ, jsonData, jsonHeader, jsonBody, jsonChild;
                                    jsonOBJJ = new JSONObject();
                                    if ("takeOwner".equalsIgnoreCase(type)) {
                                        JSONObject jsonReq = new JSONObject();

                                        String person_code = workflowUserManager.getCurrentUsername().toUpperCase();
                                        String param_person_gn = (ts.getAssignedOwnerGroup() == null) ? "" : ts.getAssignedOwnerGroup();
                                        jsonReq.put("person_code", person_code);
                                        jsonReq.put("person_group_name", param_person_gn);

                                        if ("APPROVED".equalsIgnoreCase(ts.getPendingStatus())) {
                                            ts.setStatusCurrent("PENDING");
                                        }

                                        jsonPARAMTAKEOWNER.put("ticket_id", ts.getTicketId());
                                        jsonPARAMTAKEOWNER.put("status", ts.getStatusCurrent());
                                        jsonPARAMTAKEOWNER.put("ownergroup", ts.getAssignedOwnerGroup());

                                        jsonOBJJ = jsonReq;
                                        jsonReq = null;

                                        mp = mpd.getUrl("takeowner");
                                        URL = mp.getUrl();
                                        api_id = mp.getApi_id();
                                        api_key = mp.getApi_key();

                                        logHistory.setUrl(URL);
                                        logHistory.setMethod("POST");
                                        logHistory.setRequest(jsonOBJJ.toString());
                                        logHistory.setTicketId(ts.getTicketId());
                                        logHistory.setAction("Takeowner");
                                    }

                                    body = RequestBody.create(_RESTAPI.JSON, jsonOBJJ.toString());

                                    request = new Request.Builder()
                                            .url(URL)
                                            .addHeader("api_key", api_key)
                                            .addHeader("api_id", api_id)
                                            .post(body)
                                            .build();

                                    JSONObject reqAPI = _RESTAPI.CALLAPIHANDLER(request);
                                    logHistory.setResponse(reqAPI.toString());
                                    boolean resBool = reqAPI.getBoolean("status");

                                    if (resBool) {
                                        String resData = reqAPI.getString("msg");
                                        Object object = new JSONTokener(resData).nextValue();
                                        logHistory.setResponse(reqAPI.toString());
                                        /// RESSPONSE API
                                        JSONObject _JSONOBJECT;
                                        if (object instanceof JSONObject) {
                                            _JSONOBJECT = (JSONObject) object;
                                            if ("takeOwner".equalsIgnoreCase(type)) {

                                                boolean status = false;

                                                final String TICKETID = jsonPARAMTAKEOWNER.getString("ticket_id");
                                                final String OWNERGROUP = (jsonPARAMTAKEOWNER.has("ownergroup")) ? jsonPARAMTAKEOWNER.getString("ownergroup") : "";
                                                final String owner = workflowUserManager.getCurrentUsername();
                                                // logInfo.Log(getClassName(), "OWNER USERNAME: " +owner);
                                                final String employeeCode = daoTicket.getEmployeeCode(owner);

                                                String statusCurrent = (jsonPARAMTAKEOWNER.getString("status") == null) ? "" : jsonPARAMTAKEOWNER.getString("status");
                                                String stsFalseRes = "";
                                                String ownerTs = (ts.getOwner() == null) ? "" : ts.getOwner().trim();

                                                if (_JSONOBJECT.has("data")) {
                                                    String access = _JSONOBJECT.getString("data");
                                                    String decode = crypto.decrypt(access, "S1GM4&T3LKOM");
                                                    String[] arrCode = decode.split(";");
                                                    if ("1".equalsIgnoreCase(arrCode[0])) {
                                                        String pendingStatus = ts.getPendingStatus();
                                                        if ("APPROVED".equalsIgnoreCase(ts.getPendingStatus())) {
                                                            ts.setStatus("PENDING");
                                                        }
                                                        // logInfo.Log(getClassName(), statusCurrent);
                                                        // logInfo.Log(getClassName(), ts.getStatus());
                                                        if (statusCurrent.trim().equalsIgnoreCase(ts.getStatus())) {
                                                            // logInfo.Log(getClassName(), "OWNER USERNAME: " +owner);
                                                            // logInfo.Log(getClassName(), "OWNER DB: " +ownerTs);

                                                            if (owner.trim().equalsIgnoreCase(ownerTs)) {
                                                                stsFalseRes = "OWNER IS SAME";
                                                            } else {
                                                                status = true;
                                                            }
                                                        } else {
                                                            stsFalseRes = "STATUS IS DIFERENCE, PLEASE RELOAD PAGE";
                                                        }

                                                        if (status) {
                                                            JSONObject mainObj = new JSONObject();
                                                            JSONObject payload = new JSONObject();
                                                            JSONObject payloadMain = new JSONObject();
                                                            JSONArray arr = new JSONArray();

                                                            LocalDateTime ldt = LocalDateTime.now(ZoneId.of("GMT")).plusSeconds(10);
                                                            String user = workflowUserManager.getCurrentUsername();

                                                            arr.put(user);
                                                            payload.put("sub", "1234");
                                                            payload.put("aud", arr);
                                                            payload.put("exp", ldt.toEpochSecond(ZoneOffset.UTC));

                                                            payloadMain.put("ticket_id", TICKETID);
                                                            payloadMain.put("ownergroup", OWNERGROUP);
                                                            payloadMain.put("status", ts.getStatus());
                                                            payloadMain.put("memo", "Take Ownership");

                                                            payload.put("data", payloadMain);

                                                            String token = new JWebToken(payload).toString();

                                                            // RESPONSE API SUCCESS
                                                            mainObj.put("message", "allow-access");
                                                            mainObj.put("ticket_id", TICKETID);
                                                            mainObj.put("ownergroup", OWNERGROUP);
                                                            mainObj.put("token", token);
                                                            mainObj.write(res.getWriter());
                                                            res.setStatus(HttpServletResponse.SC_OK);

                                                        } else {
                                                            res.sendError(HttpServletResponse.SC_CONFLICT, stsFalseRes);
                                                            logHistoryDao.insertToLogHistory(logHistory);
                                                        }
                                                    } else {
                                                        res.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not registered in this Owner Group");
                                                        logHistoryDao.insertToLogHistory(logHistory);
                                                    }
                                                } else {
                                                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, _JSONOBJECT.getString("message"));
                                                    logHistoryDao.insertToLogHistory(logHistory);
                                                }
                                            } else {
                                                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Type Not Support");
                                                logHistoryDao.insertToLogHistory(logHistory);
                                            }
                                        } else {
                                            res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Error Person Service response - format not object");
                                            logHistoryDao.insertToLogHistory(logHistory);
                                        }
                                    } else {
                                        res.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed To Check Ownergroup on Service Person");
                                        logHistoryDao.insertToLogHistory(logHistory);
                                    }
                                } else {
                                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Not Access.");
                                    logHistoryDao.insertToLogHistory(logHistory);
                                }
                            } else {
                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Expired");
                            }
                        } else {
                            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Invalid");
                        }
                    } else {
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "you must login first");
                    }
                } else {
                    res.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "ID Not Support");
                }
            }
        } catch (Exception ex) {
            // logInfo.Error(getClass().getName(), ex.getMessage(), ex);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "FAILED :" + ex.getMessage());
        } finally {
            body = null;
            crypto = null;
            daoTicket = null;
            checkOrigin = null;
            incomingToken = null;
            paramReferer = null;
            jsonPARAMTAKEOWNER = null;
            url = null;
            mp = null;
            mpd = null;
            request = null;
            arr_ = null;
            payloadData = null;

        }

    }
}
