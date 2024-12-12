/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.service.WorkflowManager;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mtaup
 */
public class DuplicateProcessAdmin extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Duplicate Process";

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
    //        hsr1.getWriter().print(response);
    public void webService(HttpServletRequest hsr, HttpServletResponse hsr1) throws ServletException, IOException {

        try {
            JSONObject headMainObj = new JSONObject();
            JSONObject mainObj = new JSONObject();

            String processDefId = "ticketIncidentService:latest:flowIncidentTicket";
            String originProcessId = hsr.getParameter("originProcessId");
            String status = hsr.getParameter("status"); 
            String ticketStatus = hsr.getParameter("ticketStatus");
            String saveStatus = hsr.getParameter("saveStatus");

            boolean updateStatus = UpdateStatus(status, ticketStatus, saveStatus, originProcessId);
            WorkflowProcessResult startProcess = null;
            if (updateStatus) {
                ApplicationContext ac = AppUtil.getApplicationContext();
                WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
                try {
                    startProcess = workflowManager.processStartWithLinking(processDefId, null, "Automation", originProcessId);
                    String process_id = startProcess.getParentProcessId();

                    JSONObject jObj;
                    jObj = new JSONObject();
                    mainObj.put("code", 200);
                    mainObj.put("message", "data update successful");
                    mainObj.put("processId", process_id);
                    mainObj.write(hsr1.getWriter());

                } catch (Exception ex) {
                    JSONObject jObj;
                    jObj = new JSONObject();
                    mainObj.put("code", 500);
                    mainObj.put("message", "failed");
                    headMainObj.put("error", mainObj);
                    headMainObj.write(hsr1.getWriter());
                    LogUtil.error(this.getClassName(), ex, "errror : " + ex.getMessage());
                }

            }
        } catch (Exception ex) {
            LogUtil.error(this.getClassName(), ex, "errror : " + ex.getMessage());
        }

    }

    private boolean UpdateStatus(String status, String ticketStatus, String saveStatus, String originProcessId) throws SQLException, Exception {

        boolean result = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        GetConnections gc = new GetConnections();
        StringBuilder query;
        try {
            con = gc.getJogetConnection();
            query = new StringBuilder();
            query
                    .append(" UPDATE app_fd_ticket ")
                    .append(" SET c_status = ? " )
                    .append(" , c_ticket_status = ? ")
                    .append(" , c_save_status = ? ")
                    .append(" WHERE c_parent_id = ? ");
            ps = con.prepareStatement(query.toString());
            ps.setString(1, status);
            ps.setString(2, ticketStatus);
            ps.setString(3, saveStatus);
            ps.setString(4, originProcessId);

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message rs : " + e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message ps : " + e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message con : " + e.getMessage());
            }
            query = null;
        }

        return result;

    }

}
