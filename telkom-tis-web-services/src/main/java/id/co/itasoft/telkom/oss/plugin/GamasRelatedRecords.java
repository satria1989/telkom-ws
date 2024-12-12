/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ASANI
 */
public class GamasRelatedRecords extends DefaultApplicationPlugin implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Gamas Related Records";

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        try {
//            CheckOrigin checkOrigin = new CheckOrigin();
//            String origin = request.getHeader("Origin");
//            boolean allowOrigin = checkOrigin.checkingOrigin(origin, response);
//
//            if (allowOrigin) {

                PrintWriter out = response.getWriter();

                Connection con = null;
                PreparedStatement ps = null;
                ResultSet rs = null;

                String id_ticket = request.getParameter("id_ticket");
                StringBuilder sb = null;

                DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
                WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

                if (!workflowUserManager.isCurrentUserAnonymous()) {
                    try {
                        con = ds.getConnection();
                        String sql = "SELECT DISTINCT id, dateCreated, c_perangkat, c_source_ticket, c_class_description, c_ticket_status, c_region FROM app_fd_ticket WHERE c_id_ticket = ?";
                        ps = con.prepareStatement(sql);
                        ps.setString(1, id_ticket);
                        rs = ps.executeQuery();

                        sb = new StringBuilder("{\"data\":");
                        while (rs.next()) {
                            sb.append("{\"id\":\"").append(rs.getString("id")).append("\",\"dateCreated\":\"")
                                    .append(rs.getString("dateCreated"))
                                    .append("\",\"source_ticket\":\"")
                                    .append(rs.getString("c_source_ticket"))
                                    .append("\",\"class_description\":\"")
                                    .append(rs.getString("c_class_description"))
                                    .append("\",\"ticket_status\":\"")
                                    .append(rs.getString("c_ticket_status"))
                                    .append("\",\"region\":\"")
                                    .append(rs.getString("c_region"))
                                    .append("\" }");
                        }
                        sb.append("}");

                    } catch (Exception ex) {
                        LogUtil.error(this.getClass().getName(), ex, "Error" + ex.getMessage());
                    } finally {
                        if (ps != null) {
                            try {
                                ps.close();
                            } catch (SQLException ex) {
                                LogUtil.error(this.getClass().getName(), ex, "Error" + ex.getMessage());
                            }
                        }
                        if (con != null) {
                            try {
                                con.close();
                            } catch (SQLException ex) {
                                LogUtil.error(this.getClass().getName(), ex, "Error" + ex.getMessage());
                            }
                        }
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (SQLException ex) {
                                LogUtil.error(this.getClass().getName(), ex, "Error" + ex.getMessage());
                            }
                        }
                    }
                    out.print(sb.toString());
                } else {
                    try {
                        JSONObject mainObj = new JSONObject();
                        JSONObject jObj;
                        jObj = new JSONObject();
                        mainObj.put("code", "401");
                        mainObj.put("message", "you are not logged in");
                        mainObj.write(response.getWriter());
                    } catch (JSONException ex) {
                        LogUtil.error(this.getClassName(), ex, "Error : " + ex.getMessage());
                    }
                }



    }

    @Override
    public Object execute(Map map) {
        return null;
    }

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "1.0.0";
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

}
