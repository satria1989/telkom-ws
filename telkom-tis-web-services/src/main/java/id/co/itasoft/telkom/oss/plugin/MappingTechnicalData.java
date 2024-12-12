/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.MappingTechnicalDataDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONObject;

/**
 *
 * @author mtaup
 */
public class MappingTechnicalData extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Reload Related Record";
    LogInfo logInfo = new LogInfo();

    @Override
    public String renderTemplate(FormData formData, Map dataModel) {
        return "";
    }

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "7.0.0";
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
        String parentId = req.getParameter("record");
        String method = req.getMethod();

        if ("GET".equals(method)) {
            WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
            if (!workflowUserManager.isCurrentUserAnonymous()) {
                MappingTechnicalDataDao dao = new MappingTechnicalDataDao();
                Map<String, String> datek = new HashMap<>();
                try {
                    datek = dao.getDatek(parentId);
                    JSONObject obj = new JSONObject();
                    JSONObject data = new JSONObject();
                    data.put("CPE_TYPE", datek.get("CPE_TYPE") == null ? "" : datek.get("CPE_TYPE"));
                    data.put("SP_PORT", datek.get("SP_PORT") == null ? "" : datek.get("SP_PORT"));
                    data.put("SP_TARGET", datek.get("SP_TARGET") == null ? "" : datek.get("SP_TARGET"));
                    data.put("DOWNLOAD_SPEED", datek.get("DOWNLOAD_SPEED") == null ? "" : datek.get("DOWNLOAD_SPEED"));
                    data.put("CPE_SN", datek.get("CPE_SN") == null ? "" : datek.get("CPE_SN"));
                    data.put("SERVICE_NUMBER", datek.get("SERVICE_NUMBER") == null ? "" : datek.get("SERVICE_NUMBER"));
                    data.put("DOMAIN_NAME", datek.get("DOMAIN_NAME") == null ? "" : datek.get("DOMAIN_NAME"));
                    data.put("UPLOAD_SPEED", datek.get("UPLOAD_SPEED") == null ? "" : datek.get("UPLOAD_SPEED"));
                    data.put("PRIMER_FEEDER", datek.get("PRIMER_FEEDER") == null ? "" : datek.get("PRIMER_FEEDER"));
                    data.put("RK_ODC", datek.get("RK_ODC") == null ? "" : datek.get("RK_ODC"));
                    data.put("SEKUNDER_DISTRIBUSI", datek.get("SEKUNDER_DISTRIBUSI") == null ? "" : datek.get("SEKUNDER_DISTRIBUSI"));
                    data.put("STP_TARGET", datek.get("STP_TARGET") == null ? "" : datek.get("STP_TARGET"));
                    obj.put("data", data);
                    obj.write(res.getWriter());
                } catch (Exception ex) {
                    logInfo.Error(this.getClassName(), "MainGetDatek", ex);
                }
            } else {
                res.sendError(res.SC_UNAUTHORIZED, "Unauthorized");
            }
        } else {
            res.sendError(res.SC_METHOD_NOT_ALLOWED, "Method not allowed");
        }

    }

}
