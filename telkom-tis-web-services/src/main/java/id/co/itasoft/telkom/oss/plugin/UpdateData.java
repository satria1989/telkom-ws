/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.CompleteActivityTicketIncidentApiDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationContext;


/**
 *
 * @author mtaup
 */
public class UpdateData extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Update Data";
    LogInfo log = new LogInfo();

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

    String errorUpdateData = "";

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ApplicationContext ac = AppUtil.getApplicationContext();
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
        String method = req.getMethod();
        CompleteActivityTicketIncidentApiDao caDao = new CompleteActivityTicketIncidentApiDao();
        if (!workflowUserManager.isCurrentUserAnonymous()) {
            if ("POST".equalsIgnoreCase(method)) {
                try {
                    StringBuilder jb = new StringBuilder();
                    String line = null;
                    try {
                        BufferedReader reader = req.getReader();
                        while ((line = reader.readLine()) != null) {
                            jb.append(line);
                        }
                    } catch (IOException e) {
                      log.Log(getClassName(), e.getMessage());
                    }

                    String bodyParam = jb.toString();
                    JSONParser parse = new JSONParser();
                    org.json.simple.JSONObject data_obj = (org.json.simple.JSONObject) parse.parse(bodyParam);
                    String id = data_obj.get("id").toString();
                    org.json.simple.JSONObject dataUpdate = null;
                    dataUpdate = (org.json.simple.JSONObject) parse.parse(data_obj.get("data").toString());
                    boolean statusUpdate = caDao.updateTicketParam(dataUpdate, id);
                    if (statusUpdate) {
                        res.sendError(HttpServletResponse.SC_OK, "Udate Data Success");
                    } else {
                        res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "please check the log files");
                    }

                } catch (ParseException ex) {
                  log.Log(getClassName(), ex.getMessage());
                }

            } else {
                res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method not Allowed");
            }
        } else {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
        }
    }

}
