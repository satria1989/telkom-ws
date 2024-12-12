/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.SelectCollections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author mtaup
 */
public class GetAssignedNte extends Element implements PluginWebSupport {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - GetAssignedNte";

    LogInfo logInfo = new LogInfo();

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
        return "7.0";
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
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
        if (!workflowUserManager.isCurrentUserAnonymous()) {
            try {
                SelectCollections sc = new SelectCollections();
                JSONObject getAssignedNteResponse = null;

                RESTAPI _RESTAPI = new RESTAPI();
                Request request;
                MasterParam mp = new MasterParam();
                MasterParamDao mpd = new MasterParamDao();

                String technician = req.getParameter("technician");
                String custSegment = req.getParameter("custSegment");
                String seqExtIdNte = sc.getSeqExtIdNte();

                if ("PL-TSEL".equalsIgnoreCase(custSegment)) {
                    mp = mpd.getUrl("getAssignedNtePlTsel");
                    String token = _RESTAPI.getTokenFlexible("get_access_token_apigwdev");

                    JSONObject jsonBody = new JSONObject();
                    JSONObject getAssignedNteRequest = new JSONObject();
                    JSONObject wsaHeader = new JSONObject();
                    JSONObject wsaBody = new JSONObject();

                    wsaHeader.put("externalId", seqExtIdNte);
                    wsaHeader.put("timestamp", "");
                    wsaHeader.put("callerID", "TELKOM_APPS");
                    getAssignedNteRequest.put("wsaHeader", wsaHeader);

                    wsaBody.put("technicianId", technician);
                    wsaBody.put("sourceSystem", "INSERA");
                    getAssignedNteRequest.put("wsaBody", wsaBody);
                    jsonBody.put("getAssignedNteRequest", getAssignedNteRequest);

                    RequestBody body = RequestBody.create(_RESTAPI.JSON, jsonBody.toString());

                    request
                            = new Request.Builder()
                                    .url(mp.getUrl())
                                    .addHeader("Authorization", "Bearer " + token)
                                    .post(body)
                                    .build();
                    getAssignedNteResponse = _RESTAPI.CALLAPIHANDLER(request);
                } else {
                    mp = mpd.getUrl("getAssignedNteNonPlTsel");
                    String token = _RESTAPI.getToken();
                    request
                            = new Request.Builder()
                                    .url(mp.getUrl() + technician + "/MYTECH")
                                    .addHeader("Authorization", "Bearer " + token)
                                    .get()
                                    .build();
                    getAssignedNteResponse = _RESTAPI.CALLAPIHANDLER(request);
                }

                getAssignedNteResponse.write(res.getWriter());
            } catch (JSONException ex) {
                LogUtil.error(getClass().getName(), ex, ex.getMessage());
            } catch (Exception e) {
                LogUtil.error(getClass().getName(), e, e.getMessage());
            }
        } else {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
        }
    }

}
