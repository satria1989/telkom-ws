/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketWorkLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.LoadTicketDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateTicketDao;
import id.co.itasoft.telkom.oss.plugin.function.GenerateSHA1Handler;
import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author asani
 */
public class UkurTSC extends Element implements PluginWebSupport {

    private String pluginName = "Telkom New OSS - Ticket Incident Services - UKUR TSC";
    CheckOrigin checkOrigin;
    RESTAPI _RESTAPI;
    LoadTicketDao daoTicket;
    TicketStatus ticketStatus;
    GenerateSHA1Handler generateSHA1;
    MasterParam masterParam;
    MasterParamDao masterParamDao;
    UpdateTicketDao update;
    LogHistoryDao logElastic;
    InsertTicketWorkLogsDao insertWorkLogs;
    LogInfo logInfo;

    public UkurTSC() {
        masterParam = new MasterParam();
        masterParamDao = new MasterParamDao();
        checkOrigin = new CheckOrigin();
        _RESTAPI = new RESTAPI();
        daoTicket = new LoadTicketDao();
        ticketStatus = new TicketStatus();
        generateSHA1 = new GenerateSHA1Handler();
        update = new UpdateTicketDao();
        logElastic = new LogHistoryDao();
        insertWorkLogs = new InsertTicketWorkLogsDao();
        logInfo = new LogInfo();
    }

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
        return "Plugin untuk ukur TSC";
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

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");

        try {

            if (!workflowUserManager.isCurrentUserAnonymous()) {
                String referrer = req.getHeader("referer");
                String origin = req.getHeader("Origin");
                String ACAO = req.getHeader("Access-Control-Allow-Origin");

                URL url = new URL(referrer);
                String queryStr = url.getQuery();
                if (origin == null) {
                    origin = ACAO;
                }
                boolean allowOrigin = checkOrigin.checkingOrigin(origin, res); // CHECK ORIGIN ACCESS
                if (allowOrigin) {

                    // LIST PARAM OR QUERY STRING ON REFERER HEADER
                    String[] params = queryStr.split("&");
                    JSONObject paramReferer;
                    paramReferer = new JSONObject();
                    for (String param : params) {
                        String key = param.substring(0, param.indexOf('='));
                        String val = param.substring(param.indexOf('=') + 1);
                        paramReferer.put(key, val);
                    }
                    params = null;

                    if (paramReferer.has("id")) {
                        String PARENT_ID = paramReferer.getString("id");
                        ticketStatus = daoTicket.LoadTicketByParentId(PARENT_ID);
                        String TICKETID = (ticketStatus.getTicketId() == null) ? "" : ticketStatus.getTicketId();
                        String SERVICENO = (ticketStatus.getServiceNo() == null) ? "" : ticketStatus.getServiceNo();
                        String status = (ticketStatus.getStatus() == null) ? "" : ticketStatus.getStatus();

                        // create request body TSC
                        JSONObject JSONReqeuest, jsonApiRequestResult, jsonEaiHeader, jsonEaiBody;

                        //FORMAT SHA1
                        StringBuilder sha1 = new StringBuilder();
                        sha1.append("INFOMEDIA#");
                        sha1.append(SERVICENO);
                        sha1.append("#NUSANTARA");

                        String SHA1Build = generateSHA1.sha1(sha1.toString());

                        // GET CURRENT DATE 
                        String pattern = "YYYY-MM-dd HH:mm:ss.S";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                        String date = simpleDateFormat.format(new Date());

                        jsonEaiHeader = new JSONObject();
                        jsonEaiHeader.put("externalId", "");
                        jsonEaiHeader.put("timestamp", date);

                        jsonEaiBody = new JSONObject();
                        jsonEaiBody.put("ticket", TICKETID);
                        jsonEaiBody.put("nd", SERVICENO);
                        jsonEaiBody.put("hash", SHA1Build);

                        jsonApiRequestResult = new JSONObject();
                        jsonApiRequestResult.put("eaiHeader", jsonEaiHeader);
                        jsonApiRequestResult.put("eaiBody", jsonEaiBody);

                        JSONReqeuest = new JSONObject();
                        JSONReqeuest.put("apiResultRequest", jsonApiRequestResult);

//                        LogUtil.info(getClassName(), "request ::" + JSONReqeuest.toString());
                        RequestBody body = RequestBody.create(_RESTAPI.JSON, JSONReqeuest.toString());
                        String TOKEN = _RESTAPI.getToken();
                        masterParam = masterParamDao.getUrl("ukur_tsc");
                        Request request = new Request.Builder()
                                .url(masterParam.getUrl())
                                .addHeader("Authorization", "Bearer " + TOKEN)
                                .post(body)
                                .build();

                        JSONObject response = _RESTAPI.CALLAPIHANDLER(request);
                        boolean boolCheckTSC = response.getBoolean("status");
                        int stsCodeTSC = response.getInt("status_code");
                        if (boolCheckTSC) {
                            String userLogin = workflowUserManager.getCurrentUsername();

                            Object jsontscResult = "";

                            String tscResCategory = "";
                            String tscResult = "";
                            String tscTime = "";

                            String msgRes = (response.has("msg")) ? response.getString("msg") : "EAI Error";
                            Object object = new JSONTokener(msgRes).nextValue();

                            JSONObject resObj = (JSONObject) object;

                            if (resObj.has("apiResultResponse")) {
                                JSONObject apiResultResponse = new JSONObject(resObj.get("apiResultResponse").toString());

                                if (apiResultResponse.has("eaiHeader") && apiResultResponse.has("eaiBody")) {
                                    JSONObject eaiHeaderRes = new JSONObject(apiResultResponse.get("eaiHeader").toString());
                                    JSONObject eaiBodyRes = new JSONObject(apiResultResponse.get("eaiBody").toString());

                                    tscTime = (eaiHeaderRes.getString("timestamp") == null)
                                            ? "" : eaiHeaderRes.getString("timestamp");
                                    if (eaiBodyRes.has("Message")) {
                                        String message = eaiBodyRes.getString("Message");

                                        if (message.equalsIgnoreCase("Success")) {
                                            jsontscResult = (eaiBodyRes.get("data") == null)
                                                    ? "" : eaiBodyRes.get("data");
                                            tscResult = jsontscResult.toString();

                                            JSONObject data = new JSONObject(eaiBodyRes.get("data").toString());

                                            Object speedTest = (data.get("speed_passed") == null)
                                                    ? null : data.get("speed_passed");
                                            Object redamanTest = (data.get("redaman_passed") == null)
                                                    ? null : data.get("redaman_passed");

                                            if ((speedTest.equals("0") || speedTest.equals(0))
                                                    && (redamanTest.equals("1") || redamanTest.equals(1))) {
                                                tscResCategory = "FAILED";

                                            } else if ((speedTest.equals("1") || speedTest.equals(1))
                                                    && (redamanTest.equals("1") || redamanTest.equals(1))) {
                                                tscResCategory = "PASSED";

                                            } else {
                                                tscResCategory = "FAILED";

                                            }

                                        }
                                    } else {
                                        tscResCategory = "not_found";
                                        tscResult = (eaiBodyRes.getString("error") == null)
                                                ? "" : eaiBodyRes.getString("error");
                                    }
                                }
                            }

                            update.updateTSC(
                                    TICKETID,
                                    tscTime,
                                    tscResult,
                                    tscResCategory
                            );
                            insertWorkLogs.InsertTicketWorkLogsByParentTicket(
                                    userLogin,
                                    "Check TSC : " + tscResCategory,
                                    tscResult,
                                    TICKETID);
                            logElastic.SENDHISTORY(
                                    TICKETID,
                                    "UkurTSC",
                                    masterParam.getUrl(),
                                    "POST",
                                    JSONReqeuest,
                                    response,
                                    stsCodeTSC);
                            msgRes = null;

                            resObj.write(res.getWriter());
                        } else {
                            String msgErr = (response.has("msg")) ? response.getString("msg") : "EAI Error";
                            res.sendError(stsCodeTSC, msgErr);
                        }

                    } else {
                        res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
                    }
                } else {
                    res.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Origin Not Allow.");
                }
            } else {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UnAuthorized");
            }

        } catch (Exception ex) {
            logInfo.Error(this.getClassName(), ex.getMessage(), ex);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR");
        } finally {
            checkOrigin = null;
            _RESTAPI = null;
            daoTicket = null;
            ticketStatus = null;
            generateSHA1 = null;
            masterParam = null;
            masterParamDao = null;
        }
    }

}
