/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.CheckTicketSQMDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONObject;

/**
 *
 * @author ADMIN
 */
public class CheckTicketSQM extends Element implements PluginWebSupport {

    public static String pluginName
            = "Telkom New OSS - Ticket Incident Services - Check Ticket SQM";

    @Override
    public String renderTemplate(FormData formData, Map dataModel) {
        return null;
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
        return null;
    }

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        JSONObject mainObj = new JSONObject();
        CheckTicketSQMDao res = new CheckTicketSQMDao();
        MasterParamDao par = new MasterParamDao();
        ApiConfig config = new ApiConfig();

        String apiKey = request.getHeader("api_key");
        String apiId = request.getHeader("api_id");
        String method = request.getMethod();

        try {
            config = par.getUrlapi("ticket_incident_api");
            if ("GET".equals(method)) {
                if (apiId.equals(config.getApiId()) && apiKey.equals(config.getApiKey())) {
//                    String serviceId = request.getParameter("service_id");
                    String serviceNumber = request.getParameter("service_number");
                    String servcieType = request.getParameter("service_type");
                    String actualSolution = request.getParameter("actual_solution");

                    boolean result = res.getDataTicketSQM(servcieType, actualSolution, serviceNumber);
                    mainObj.put("code", 200);
                    mainObj.put("result", result);
                    mainObj.write(response.getWriter());
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
                }
            } else {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method Not Allowed");
            }

        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
        }finally{
            mainObj = null;
            res = null;
            par = null;
            config = null;
        }
    }
}
