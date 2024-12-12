/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
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
 * @author itasoft
 */
public class DeleteRelatedRecord extends Element implements PluginWebSupport {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Delete Related Record";

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
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {


                WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

                if (!workflowUserManager.isCurrentUserAnonymous()) {

                    try {
                        JSONObject mainObj = new JSONObject();
                        String id = req.getParameter("id");
                        String parent_id = req.getParameter("parent_id");
                        String id_tiket = req.getParameter("id_ticket");
                        String classificationType = req.getParameter("classification_type");
                        URI uri = new URI(req.getRequestURL().toString());
                        String host = uri.getHost();
                        
                        boolean result = deleteRelatedRecord(id);
                        updateIdTicketGamasOnChildTicketDelete("", "FALSE", id_tiket);
                        if ("FISIK".equalsIgnoreCase(classificationType)) {
                            res.sendRedirect("https://"+host+"/jw/web/userview/ticketIncidentService/ticketIncidentService/_/relatedRecords?embed=true&parent_id=" + parent_id);
                        } else {
                            res.sendRedirect("https://"+host+"/jw/web/userview/ticketIncidentService/ticketIncidentService/_/relatedRecordsLogic?embed=true&parent_id=" + parent_id);
                        }
                    } catch (URISyntaxException ex) {
                        LogUtil.error(this.getClassName(), ex, "error delete related record : "+ex.getMessage());
                    }
                } else {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
                }

    }

    private boolean deleteRelatedRecord(String id) {
        boolean result = false;
        int i = 0;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "DELETE FROM app_fd_related_record_id WHERE id = ? ";

        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, id);
            i = ps.executeUpdate();

            if (i > 0) {
                result = true;
                con.close();
            }

        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
        }
        return result;
    }

    public void updateIdTicketGamasOnChildTicket(String idTicket, String statusChildGamas, String ticketId) {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        PreparedStatement ps = null;
        String queryUpdate = "update app_fd_ticket " +
                "set c_ticket_id_gamas = ? " +
                ", c_related_to_gamas = ? " +
                "WHERE c_id_ticket = ? ";

        try {
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(queryUpdate);
                ps.setString(1, idTicket);
                ps.setString(2, "1");
                ps.setString(3, ticketId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }

            } catch (SQLException ex) {
                LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
            }
        }
    }
    
    public void updateIdTicketGamasOnChildTicketDelete(String idTicket, String statusChildGamas, String ticketId) {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        PreparedStatement ps = null;
        String queryUpdate = "update app_fd_ticket " +
                "set c_ticket_id_gamas = ? " +
                ", c_child_gamas = ? " +
                ", c_related_to_gamas = ? " +
                "WHERE c_id_ticket = ? ";

        try {
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(queryUpdate);
                ps.setString(1, idTicket);
                ps.setString(2, statusChildGamas);
                ps.setString(3, "0");
                ps.setString(4, ticketId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }

            } catch (SQLException ex) {
                LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
            }
        }
    }

}
