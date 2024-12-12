/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ProcessData;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;

/**
 *
 * @author mtaup
 */
public class AutoDuplicateProcess extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Duplicate Process";
    LogInfo info = new LogInfo();
    
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

       
    }

    private boolean UpdateStatus(String status, String ticketStatus, String saveStatus, String originProcessId) throws SQLException, Exception {

        boolean result = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        id.co.itasoft.telkom.oss.plugin.function.GetConnections gc = new id.co.itasoft.telkom.oss.plugin.function.GetConnections();
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
              info.Error(getClass().getName(), e.getMessage(), e);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
              info.Error(getClass().getName(), e.getMessage(), e);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
              info.Error(getClass().getName(), e.getMessage(), e);
            }
            query = null;
        }

        return result;

    }
    
    private ProcessData getData(String idTicket){
        ProcessData data = new ProcessData();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        StringBuilder query = new StringBuilder();
        
        
        return data;
    }

}
