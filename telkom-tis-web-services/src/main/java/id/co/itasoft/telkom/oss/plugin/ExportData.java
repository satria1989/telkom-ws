/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.ExportDataDao;
import id.co.itasoft.telkom.oss.plugin.model.HistoryTicket;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;

/**
 *
 * @author mtaup
 */
public class ExportData extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Export Data";

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
        return null;
    }

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

        if (!workflowUserManager.isCurrentUserAnonymous()) {
            String param = req.getParameter("param");
            String reqData = req.getParameter("reqData");
            String fileName = "";
            ExportDataDao expDao = new ExportDataDao();
            StringBuilder sb = null;
            try {
                if ("history_ticket".equalsIgnoreCase(reqData)) {
                    List<HistoryTicket> historyTicketList = expDao.getHistoryTicket(param);
                    sb = new StringBuilder();

                    sb.append("Ticket ID,");
                    sb.append("Status,");
                    sb.append("Owner,");
                    sb.append("Owner Group,");
                    sb.append("Change Date,");
                    sb.append("Change By,");
                    sb.append("Memo,");
                    sb.append("Status Tracking");
                    sb.append("\r");
                    if (!historyTicketList.isEmpty()) {
                        for (HistoryTicket historyTicket : historyTicketList) {
                            sb.append(historyTicket.getTicketId()).append(",");
                            sb.append(historyTicket.getStatus()).append(",");
                            sb.append(historyTicket.getOwner()).append(",");
                            sb.append(historyTicket.getOwnerGroup()).append(",");
                            sb.append(historyTicket.getChangeDate()).append(",");
                            sb.append(historyTicket.getChangeBy()).append(",");
                            sb.append(historyTicket.getMemo()).append(",");
                            sb.append(historyTicket.getStatusTracking());
                            sb.append("\r");
                        }
                    }

                    fileName = "History Ticket " + historyTicketList.get(0).getTicketId() + ".csv";

                }

                res.setContentType("text/csv");
                res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

                try (OutputStream outputStream = res.getOutputStream()) {
                    String outputResult = sb.toString();
                    outputStream.write(outputResult.getBytes());
                    outputStream.flush();
                }

            } catch (SQLException ex) {
                LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
            }
        } else {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
        }
    }

}
